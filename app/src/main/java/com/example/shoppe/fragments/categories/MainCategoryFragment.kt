package com.example.shoppe.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppe.R
import com.example.shoppe.adapters.BestDealsAdapter
import com.example.shoppe.adapters.BestProductsAdapter
import com.example.shoppe.adapters.SpecialProductsAdapter
import com.example.shoppe.databinding.FragmentMainCategoryBinding
import com.example.shoppe.fragments.shopping.HomeFragmentDirections
import com.example.shoppe.utils.Resource
import com.example.shoppe.utils.showBottomNavigationView
import com.example.shoppe.viewmodels.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainCategoryFragment : Fragment() {
    private var _binding: FragmentMainCategoryBinding? = null
    private val binding get() = _binding!!

    //    private val viewModel by viewModels<MainCategoryViewModel>() // make viewModelScope.launch{} behave incorrectly, fix in line 35 and 47
    private lateinit var viewModel: MainCategoryViewModel

    private lateinit var specialProductAdapter: SpecialProductsAdapter
    private lateinit var bestDealsAdapter: BestDealsAdapter
    private lateinit var bestProductsAdapter: BestProductsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainCategoryBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[MainCategoryViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpecialProductRecycleView()
        setupBestDealsRecycleView()
        setupBestProductsRecycleView()

        specialProductAdapter.onClick = {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToProductDetailsFragment(
                    it
                )
            )
        }

        bestDealsAdapter.onClick = {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToProductDetailsFragment(
                    it
                )
            )
        }

        bestProductsAdapter.onClick = {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToProductDetailsFragment(
                    it
                )
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                Log.d(TAG, "repeat Resumed")
                launch {
                    Log.d(TAG, "collect special")
                    viewModel.specialProducts.collectLatest {
                        when (it) {
                            is Resource.Loading -> {
                                showLoading()
                            }

                            is Resource.Success -> {
                                specialProductAdapter.differ.submitList(it.data)
                                hideLoading()
                            }

                            is Resource.Error -> {
                                hideLoading()
                                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                            }

                            else -> Unit
                        }
                    }
                }

                launch {
                    Log.d(TAG, "collect best deals")
                    viewModel.bestDeals.collectLatest {
                        when (it) {
                            is Resource.Loading -> {
                                showLoading()
                            }

                            is Resource.Success -> {
                                bestDealsAdapter.differ.submitList(it.data)
                                hideLoading()
                            }

                            is Resource.Error -> {
                                hideLoading()
                                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                            }

                            else -> Unit
                        }
                    }
                }

                launch {
                    viewModel.bestProducts.collectLatest {
                        Log.d(TAG, "collect best product")
                        when (it) {
                            is Resource.Loading -> {
                                binding.progressBarBestProducts.visibility = View.VISIBLE
                            }

                            is Resource.Success -> {
                                bestProductsAdapter.differ.submitList(it.data)
                                binding.progressBarBestProducts.visibility = View.GONE
                            }

                            is Resource.Error -> {
                                hideLoading()
                                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                                binding.progressBarBestProducts.visibility = View.GONE
                            }

                            else -> Unit
                        }
                    }
                }
            }
        }

        binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY) {
                Log.d(TAG, "end scroll")
                viewModel.fetchBestProducts()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        showBottomNavigationView()
    }

    private fun hideLoading() {
        binding.progressBarMainCategory.visibility = View.GONE
    }

    private fun showLoading() {
        binding.progressBarMainCategory.visibility = View.VISIBLE
    }

    private fun setupSpecialProductRecycleView() {
        specialProductAdapter = SpecialProductsAdapter()
        binding.rvSpecialProducts.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = specialProductAdapter
        }
    }

    private fun setupBestDealsRecycleView() {
        bestDealsAdapter = BestDealsAdapter()
        binding.rvBestDeals.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = bestDealsAdapter
        }
    }

    private fun setupBestProductsRecycleView() {
        bestProductsAdapter = BestProductsAdapter()
        binding.rvBestProducts.apply {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductsAdapter
        }
    }

    companion object {
        const val TAG = "MainCategoryFragment"
    }
}