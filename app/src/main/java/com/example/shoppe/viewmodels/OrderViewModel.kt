package com.example.shoppe.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppe.models.order.Order
import com.example.shoppe.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : ViewModel() {

    private val _order = MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val order = _order.asStateFlow()

    fun placeOrder(order: Order) {
        viewModelScope.launch {
            _order.emit(Resource.Loading())
        }
        firestore.runBatch { _ ->
            // TODO("Add the order into the user-orders collection")
            // TODO("Add the order into  orders collection")
            // TODO("Delete the products from user-cart collection")
            firestore.collection("user").document(auth.uid!!).collection("orders").document()
                .set(order)

            firestore.collection("orders").document().set(order)

            firestore.collection("user").document(auth.uid!!).collection("Cart").get()
                .addOnSuccessListener {
                    it.documents.forEach { document ->
                        document.reference.delete()
                    }
                }
        }.addOnSuccessListener {
            viewModelScope.launch {
                _order.emit(Resource.Success(order))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _order.emit(Resource.Error(it.message.toString()))
            }
        }
    }
}