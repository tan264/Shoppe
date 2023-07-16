package com.example.shoppe.fragments.shopping

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppe.adapters.AddressAdapter
import com.example.shoppe.adapters.BillingProductAdapter
import com.example.shoppe.databinding.FragmentBillingBinding
import com.example.shoppe.models.Address
import com.example.shoppe.models.CartProduct
import com.example.shoppe.models.order.Order
import com.example.shoppe.models.order.OrderStatus
import com.example.shoppe.utils.HorizontalItemDecoration
import com.example.shoppe.utils.Resource
import com.example.shoppe.viewmodels.BillingViewModel
import com.example.shoppe.viewmodels.OrderViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BillingFragment : Fragment() {
    private var _binding: FragmentBillingBinding? = null
    val binding get() = _binding!!

    private val viewModel by viewModels<BillingViewModel>()
    private val orderViewModel by viewModels<OrderViewModel>()

    private val args by navArgs<BillingFragmentArgs>()

    private var products = emptyList<CartProduct>()
    private var totalPrice = 0f
    private var selectedAddress: Address? = null

    private val addressAdapter by lazy {
        AddressAdapter()
    }

    private val billingProductAdapter by lazy {
        BillingProductAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBillingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        products = args.products.toList()
        totalPrice = args.totalPrice

        setupBillingProductRv()
        setupAddressRv()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.address.collectLatest {
                        when (it) {
                            is Resource.Loading -> {
                                binding.progressbarAddress.visibility = View.VISIBLE
                            }

                            is Resource.Error -> {
                                binding.progressbarAddress.visibility = View.GONE
                                Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_LONG)
                                    .show()
                            }

                            is Resource.Success -> {
                                binding.progressbarAddress.visibility = View.GONE
                                addressAdapter.differ.submitList(it.data)
                            }

                            else -> Unit
                        }
                    }
                }

                launch {
                    orderViewModel.order.collectLatest {
                        when (it) {
                            is Resource.Loading -> {
                                binding.buttonPlaceOrder.startAnimation()
                            }

                            is Resource.Error -> {
                                binding.buttonPlaceOrder.revertAnimation()
                                Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_LONG)
                                    .show()
                            }

                            is Resource.Success -> {
                                binding.buttonPlaceOrder.revertAnimation()
                                findNavController().navigateUp()
                                Snackbar.make(view, "Your order was placed", Snackbar.LENGTH_SHORT).show()
                            }

                            else -> Unit
                        }
                    }
                }
            }
        }

        binding.apply {
            imageAddAddress.setOnClickListener {
                findNavController().navigate(BillingFragmentDirections.actionBillingFragmentToAddressFragment())
            }
            tvTotalPrice.text = String.format("$%.2f", totalPrice)
            buttonPlaceOrder.setOnClickListener {
                if (selectedAddress == null) {
                    Toast.makeText(context, "Please select an address", Toast.LENGTH_LONG).show()
                } else {
                    showOrderConfirmationDialog()
                }
            }
        }

        billingProductAdapter.apply {
            differ.submitList(products)
        }
        addressAdapter.onClick = {
            selectedAddress = it
        }
    }

    private fun showOrderConfirmationDialog() {
        val dialog = AlertDialog.Builder(context).apply {
            setTitle("Order items")
            setMessage("Do you want to order your cart item?")
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton("Yes") { dialog, _ ->
                val order = Order(
                    OrderStatus.Ordered.status,
                    totalPrice,
                    products,
                    selectedAddress!!
                )
                orderViewModel.placeOrder(order)
                dialog.dismiss()
            }
        }
        dialog.create()
        dialog.show()
    }

    private fun setupAddressRv() {
        binding.rvAddress.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = addressAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }

    private fun setupBillingProductRv() {
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = billingProductAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }
}