package com.example.shoppe.fragments.shopping

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppe.R
import com.example.shoppe.activities.ShoppingActivity
import com.example.shoppe.adapters.ColorsAdapter
import com.example.shoppe.adapters.ViewPagerImagesAdapter
import com.example.shoppe.databinding.FragmentProductDetailsBinding
import com.example.shoppe.utils.hideBottomNavigationView
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProductDetailsFragment : Fragment() {

    private val args by navArgs<ProductDetailsFragmentArgs>()

    private var _binding: FragmentProductDetailsBinding? = null
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
    ): View? {
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
            if(product.colors.isNullOrEmpty()) {
                tvProductColors.visibility = View.INVISIBLE
            }
            imageClose.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        viewPagerImagesAdapter.differ.submitList(product.images)
        product.colors?.let {
            colorsAdapter.differ.submitList(it)
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
}