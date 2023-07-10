package com.example.shoppe.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppe.adapters.BestProductsAdapter
import com.example.shoppe.databinding.FragmentBaseCategoryBinding
import com.example.shoppe.fragments.shopping.HomeFragmentDirections
import com.example.shoppe.utils.showBottomNavigationView

open class BaseCategoryFragment : Fragment() {
    private var _binding: FragmentBaseCategoryBinding? = null
    val binding get() = _binding!!

    protected val offerAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }
    protected val bestProductsAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBaseCategoryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOfferRecycleView()
        setupBestProductsRecycleView()

        bestProductsAdapter.onClick = {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToProductDetailsFragment(
                    it
                )
            )
        }

        offerAdapter.onClick = {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToProductDetailsFragment(
                    it
                )
            )
        }

        binding.rvOffer.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recyclerView.canScrollHorizontally(1) && dx != 0) {
                    onOfferPagingRequest()
                }
            }
        })

        binding.nestedScrollBaseCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY) {
                Log.d(MainCategoryFragment.TAG, "end scroll")
                onBestPagingRequest()
            }
        })
    }

    fun showOfferProgressBar() {
        binding.progressBarOfferBaseCategory.visibility = View.VISIBLE
    }

    fun hideOfferProgressBar() {
        binding.progressBarOfferBaseCategory.visibility = View.GONE
    }

    fun showBestProductsProgressBar() {
        binding.progressBarBestProductsBaseCategory.visibility = View.VISIBLE
    }

    fun hideBestProductsProgressBar() {
        binding.progressBarBestProductsBaseCategory.visibility = View.GONE
    }

    open fun onOfferPagingRequest() {}

    open fun onBestPagingRequest() {}

    private fun setupBestProductsRecycleView() {
        binding.rvBestProducts.apply {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductsAdapter
        }
    }

    private fun setupOfferRecycleView() {
        binding.rvOffer.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = offerAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}