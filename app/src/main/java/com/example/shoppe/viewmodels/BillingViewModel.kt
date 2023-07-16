package com.example.shoppe.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppe.models.Address
import com.example.shoppe.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : ViewModel() {

    private val _address = MutableStateFlow<Resource<List<Address>>>(Resource.Unspecified())
    val address = _address.asStateFlow()

    init {
        getUserAddress()
    }

    fun getUserAddress() {
        viewModelScope.launch {
            _address.emit(Resource.Loading())
            firestore.collection("user").document(auth.uid!!).collection("Address")
                .addSnapshotListener { value, error ->
                    if (error != null || value == null) {
                        viewModelScope.launch {
                            _address.emit(Resource.Error(error?.message.toString()))
                        }
                    } else {
                        val addresses = value.toObjects<Address>()
                        viewModelScope.launch {
                            _address.emit(Resource.Success(addresses))
                        }
                    }

                }
        }
    }
}