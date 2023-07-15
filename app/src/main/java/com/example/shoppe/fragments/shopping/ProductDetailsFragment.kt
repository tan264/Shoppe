package com.example.shoppe.fragments.shopping

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
import com.example.shoppe.adapters.ColorsAdapter
import com.example.shoppe.adapters.ViewPagerImagesAdapter
import com.example.shoppe.databinding.FragmentProductDetailsBinding
import com.example.shoppe.dialog.setupBottomAddToCart
import com.example.shoppe.utils.Resource
import com.example.shoppe.utils.hideBottomNavigationView
import com.example.shoppe.viewmodels.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {

    private val args by navArgs<ProductDetailsFragmentArgs>()

    private var _binding: FragmentProductDetailsBinding? = null

    private val viewModel by viewModels<DetailsViewModel>()
    val binding get() = _binding!!

    private val viewPagerImagesAdapter by lazy {
        ViewPagerImagesAdapter()
    }

    private val colorsAdapter by lazy {
        ColorsAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavigationView()
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product

        setupColorsRv()
        setupViewpager()

        binding.apply {
            tvProductName.text = product.name
            tvPriceName.text = "${product.price}"
            tvProductDescription.text = product.description
            if (product.colors.isNullOrEmpty()) {
                tvProductColors.visibility = View.INVISIBLE
            }
            imageClose.setOnClickListener {
                findNavController().navigateUp()
            }
            buttonAddToCart.setOnClickListener {
                setupBottomAddToCart(product) {
                    viewModel.addProductToCart(it)
                }
            }
        }

        viewPagerImagesAdapter.differ.submitList(product.images)
        product.colors?.let {
            colorsAdapter.differ.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addToCart.collectLatest {
                    when (it) {
                        is Resource.Loading ->
                            binding.buttonAddToCart.startAnimation()

                        is Resource.Success ->
                            binding.buttonAddToCart.revertAnimation()

                        is Resource.Error -> {
                            binding.buttonAddToCart.revertAnimation()
                            Toast.makeText(context, it.message.toString(), Toast.LENGTH_LONG).show()
                        }

                        else -> Unit
                    }
                }
            }
        }

    }

    private fun setupViewpager() {
        binding.apply {
            viewPagerProductImages.adapter = viewPagerImagesAdapter
        }
    }

    private fun setupColorsRv() {
        binding.rvColors.apply {
            adapter = colorsAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    companion object {
        const val TAG = "ProductDetailsFragment"
    }
}