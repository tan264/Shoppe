package com.example.shoppe.fragments.settings

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.shoppe.R
import com.example.shoppe.databinding.FragmentUserAccountBinding
import com.example.shoppe.models.User
import com.example.shoppe.utils.Resource
import com.example.shoppe.viewmodels.UserAccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.FileNotFoundException

@AndroidEntryPoint
class UserAccountFragment : Fragment() {

    private var _binding: FragmentUserAccountBinding? = null
    val binding get() = _binding!!

    private val viewModel by viewModels<UserAccountViewModel>()

    private var imageUri: Uri? = null
    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            try {
                imageUri = uri
            } catch (e: FileNotFoundException) {
                Log.e("UserAccountFragment", e.message.toString())
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserAccountBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.user.collectLatest {
                        when (it) {
                            is Resource.Success -> {
                                hideUserLoading()
                                setUserInformation(it.data!!)
                            }

                            is Resource.Error -> {
                                hideUserLoading()
                                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                            }

                            is Resource.Loading -> {
                                showUserLoading()
                            }

                            else -> Unit
                        }
                    }
                }

                launch {
                    viewModel.updateInfo.collectLatest {
                        when (it) {
                            is Resource.Loading -> {
                                binding.buttonSave.startAnimation()
                            }

                            is Resource.Success -> {
                                binding.buttonSave.revertAnimation()
                                findNavController().navigateUp()
                            }

                            is Resource.Error -> {
                                binding.buttonSave.revertAnimation()
                                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                            }

                            else -> Unit
                        }
                    }
                }
            }
        }

        binding.buttonSave.setOnClickListener {
            binding.apply {
                val firstName = edFirstName.text.toString()
                val lastName = edLastName.text.toString()
                val email = edEmail.text.toString()
                val user = User(firstName, lastName, email)
                viewModel.updateUser(user, imageUri)
            }
        }

        binding.imageEdit.setOnClickListener {
            getContent.launch("image/*")
        }
    }

    private fun setUserInformation(user: User) {
        binding.apply {
            if (imageUri == null) {
                if (user.imagePath.isNotEmpty()) {
                    Glide.with(this@UserAccountFragment).load(user.imagePath)
                        .error(R.drawable.baseline_broken_image_24)
                        .into(imageUser)
                } else {
                    imageUser.setImageResource(R.drawable.ic_launcher_background)
                }
            } else {
                Glide.with(this@UserAccountFragment).load(imageUri).into(imageUser)
            }
            edFirstName.setText(user.firstName)
            edLastName.setText(user.lastName)
            edEmail.setText(user.email)
        }
    }

    private fun hideUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.GONE
            imageUser.visibility = View.VISIBLE
            imageEdit.visibility = View.VISIBLE
            edEmail.visibility = View.VISIBLE
            edFirstName.visibility = View.VISIBLE
            edLastName.visibility = View.VISIBLE
            tvUpdatePassword.visibility = View.VISIBLE
            buttonSave.visibility = View.VISIBLE
        }
    }

    private fun showUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.VISIBLE
            imageUser.visibility = View.INVISIBLE
            imageEdit.visibility = View.INVISIBLE
            edEmail.visibility = View.INVISIBLE
            edFirstName.visibility = View.INVISIBLE
            edLastName.visibility = View.INVISIBLE
            tvUpdatePassword.visibility = View.INVISIBLE
            buttonSave.visibility = View.INVISIBLE
        }
    }

    companion object {
        const val TAG = "UserAccountFragment"
    }
}