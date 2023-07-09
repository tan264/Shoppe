package com.example.shoppe.fragments.categories

import android.os.Bundle
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.shoppe.models.Category
import com.example.shoppe.utils.Resource
import com.example.shoppe.viewmodels.CategoryViewModel
import com.example.shoppe.viewmodels.factory.BaseCategoryViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChairFragment : BaseCategoryFragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore

    private val viewModel by viewModels<CategoryViewModel> {
        BaseCategoryViewModelFactory(firestore, Category.Chair)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.offerProducts.collectLatest {
                        when (it) {
                            is Resource.Loading -> {
                                showOfferProgressBar()
                            }

                            is Resource.Success -> {
                                offerAdapter.differ.submitList(it.data)
                                hideOfferProgressBar()
                            }

                            is Resource.Error -> {
                                hideOfferProgressBar()
                                Snackbar.make(
                                    requireView(),
                                    it.message.toString(),
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }

                            else -> Unit
                        }
                    }
                }

                launch {
                    viewModel.bestProducts.collectLatest {
                        when (it) {
                            is Resource.Loading -> {
                                showBestProductsProgressBar()
                            }

                            is Resource.Success -> {
                                bestProductsAdapter.differ.submitList(it.data)
                                hideBestProductsProgressBar()
                            }

                            is Resource.Error -> {
                                hideBestProductsProgressBar()
                                Snackbar.make(
                                    requireView(),
                                    it.message.toString(),
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }

                            else -> Unit
                        }
                    }
                }
            }
        }

        binding.nestedScrollBaseCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY) {
                onBestPagingRequest()
            }
        })
    }

    override fun onOfferPagingRequest() {
    }

    override fun onBestPagingRequest() {
        viewModel.fetchBestProducts()
    }
}