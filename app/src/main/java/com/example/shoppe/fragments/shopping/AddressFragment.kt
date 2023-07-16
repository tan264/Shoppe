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
import com.example.shoppe.databinding.FragmentAddressBinding
import com.example.shoppe.models.Address
import com.example.shoppe.utils.Resource
import com.example.shoppe.viewmodels.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddressFragment : Fragment() {

    private var _binding: FragmentAddressBinding? = null
    val binding get() = _binding!!

    private val viewModel by viewModels<AddressViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddressBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            buttonSave.setOnClickListener {
                val addressTitle = edAddressTitle.text.toString()
                val fullName = edFullName.text.toString()
                val street = edStreet.text.toString()
                val phone = edPhone.text.toString()
                val city = edCity.text.toString()
                val state = edState.text.toString()
                val address = Address(addressTitle, fullName, street, city, phone, state)
                viewModel.addAddress(address)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.addNewAddress.collectLatest {
                        when (it) {
                            is Resource.Loading -> {
                                binding.progressbarAddress.visibility = View.VISIBLE
                            }

                            is Resource.Success -> {
                                binding.progressbarAddress.visibility = View.GONE
                                findNavController().navigateUp()
                            }

                            is Resource.Error -> {
                                binding.progressbarAddress.visibility = View.GONE
                                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                            }

                            else -> Unit
                        }
                    }
                }

                launch {
                    viewModel.error.collectLatest {
                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}