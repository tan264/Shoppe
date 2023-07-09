package com.example.shoppe.fragments.authentication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.shoppe.activities.ShoppingActivity
import com.example.shoppe.databinding.FragmentIntroductionBinding
import com.example.shoppe.viewmodels.IntroductionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IntroductionFragment : Fragment() {
    private val viewModel by viewModels<IntroductionViewModel>()

    private var _binding: FragmentIntroductionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIntroductionBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.navigate.collect {
                    when (it) {
                        IntroductionViewModel.SHOPPING_ACTIVITY -> {
                            Intent(
                                requireActivity(),
                                ShoppingActivity::class.java
                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                .also { intent ->
                                    startActivity(intent)
                                }
                        }

                        IntroductionViewModel.ACCOUNT_OPTIONS_FRAGMENT -> {
                            findNavController().navigate(IntroductionFragmentDirections.actionIntroductionFragmentToAccountOptionsFragment())
                        }
                    }
                }
            }
        }

        binding.buttonStart.setOnClickListener {
            viewModel.startButtonClick()
            findNavController().navigate(IntroductionFragmentDirections.actionIntroductionFragmentToAccountOptionsFragment())
        }
    }
}