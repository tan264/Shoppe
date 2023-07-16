package com.example.shoppe.fragments.shopping

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppe.adapters.CartProductAdapter
import com.example.shoppe.databinding.FragmentCartBinding
import com.example.shoppe.firebase.FirebaseCommon
import com.example.shoppe.utils.Resource
import com.example.shoppe.utils.VerticalItemDecoration
import com.example.shoppe.viewmodels.CartViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    val binding get() = _binding!!

    private val cartAdapter by lazy {
        CartProductAdapter()
    }

    private val viewModel by activityViewModels<CartViewModel>()

    private var totalPrice = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCartRecycleView()
        cartAdapter.onPlusClick = {
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.INCREASE)
        }

        cartAdapter.onMinusClick = {
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.DECREASE)
        }

        cartAdapter.onCartProductClick = {
            findNavController().navigate(
                CartFragmentDirections.actionCartFragmentToProductDetailsFragment(
                    it.product
                )
            )
        }

        binding.apply {
            buttonCheckout.setOnClickListener {
                findNavController().navigate(
                    CartFragmentDirections.actionCartFragmentToBillingFragment(
                        totalPrice,
                        cartAdapter.differ.currentList.toTypedArray()
                    )
                )
            }
            imageCloseCart.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.cartProducts.collectLatest {
                        when (it) {
                            is Resource.Success -> {
                                binding.progressbarCart.visibility = View.INVISIBLE
                                if (it.data!!.isEmpty()) {
                                    showEmptyCart()
                                    hideOtherViews()
                                } else {
                                    hideEmptyCart()
                                    showOtherViews()
                                    cartAdapter.differ.submitList(it.data)
                                }
                            }

                            is Resource.Error -> {
                                binding.progressbarCart.visibility = View.INVISIBLE
                                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                            }

                            is Resource.Loading -> {
                                binding.progressbarCart.visibility = View.VISIBLE
                            }

                            else -> Unit
                        }
                    }
                }

                launch {
                    viewModel.productsPrice.collectLatest { price ->
                        price?.let {
                            totalPrice = it
                            binding.tvTotalPrice.text = String.format("$%.2f", it)
                        }
                    }
                }

                launch {
                    viewModel.deleteDialog.collectLatest {
                        val dialog = AlertDialog.Builder(context).apply {
                            setTitle("Delete item from cart")
                            setMessage("Do you want to delete this item from cart")
                            setNegativeButton("Cancel") { dialog, _ ->
                                dialog.dismiss()
                            }
                            setPositiveButton("Yes") { dialog, _ ->
                                viewModel.deleteCartProduct(it)
                                dialog.dismiss()
                            }
                        }
                        dialog.create()
                        dialog.show()
                    }
                }
            }
        }
    }

    private fun showOtherViews() {
        binding.apply {
            rvCart.visibility = View.VISIBLE
            totalBoxContainer.visibility = View.VISIBLE
            buttonCheckout.visibility = View.VISIBLE
        }
    }

    private fun hideOtherViews() {
        binding.apply {
            rvCart.visibility = View.GONE
            totalBoxContainer.visibility = View.GONE
            buttonCheckout.visibility = View.GONE
        }
    }

    private fun hideEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.GONE
        }
    }

    private fun showEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.VISIBLE
        }
    }

    private fun setupCartRecycleView() {
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = cartAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }
}