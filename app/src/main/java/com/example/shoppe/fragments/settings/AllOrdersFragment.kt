package com.example.shoppe.fragments.settings

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppe.adapters.OrderAdapter
import com.example.shoppe.databinding.FragmentAllOrdersBinding
import com.example.shoppe.utils.Resource
import com.example.shoppe.viewmodels.AllOrdersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllOrdersFragment : Fragment() {
    private var _binding: FragmentAllOrdersBinding? = null
    val binding get() = _binding!!

    private val viewModel by viewModels<AllOrdersViewModel>()

    private val orderAdapter by lazy {
        OrderAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAllOrdersBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOrdersRv()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allOrders.collectLatest {
                    when (it) {
                        is Resource.Success -> {
                            binding.progressbarAllOrders.visibility = View.GONE
                            if (it.data.isNullOrEmpty()) {
                                binding.tvEmptyOrders.visibility = view.visibility
                            } else {
                                orderAdapter.differ.submitList(it.data)
                            }
                        }

                        is Resource.Error -> {
                            binding.progressbarAllOrders.visibility = View.GONE
                            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                        }

                        is Resource.Loading -> {
                            binding.progressbarAllOrders.visibility = View.VISIBLE
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun setupOrdersRv() {
        binding.rvAllOrders.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = orderAdapter
        }
    }
}