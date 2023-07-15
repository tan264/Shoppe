package com.example.shoppe.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppe.firebase.FirebaseCommon
import com.example.shoppe.models.CartProduct
import com.example.shoppe.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon,
) : ViewModel() {

    private val _addToCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val addToCart = _addToCart.asStateFlow()

    fun addProductToCart(cartProduct: CartProduct) {
        viewModelScope.launch {
            _addToCart.emit(Resource.Loading())
        }
        firestore.collection("user").document(auth.uid!!).collection("Cart").whereEqualTo(
            "product.id", cartProduct.product.id
        ).get().addOnSuccessListener {
            it.documents.let { products ->
                if (products.isEmpty()) {
                    addNewProduct(cartProduct)
                } else {
                    val product = products.first().toObject(CartProduct::class.java)
                    if (product == cartProduct) {
                        increaseQuantity(products.first().id, cartProduct)
                    } else {
                        addNewProduct(cartProduct)
                    }
                }
            }
        }.addOnFailureListener {
            viewModelScope.launch { _addToCart.emit(Resource.Error(it.message.toString())) }
        }
    }

    private fun addNewProduct(cartProduct: CartProduct) {
        firebaseCommon.addProductToCart(cartProduct) { addedProduct, exception ->
            viewModelScope.launch {
                if (exception == null) {
                    _addToCart.emit(Resource.Success(addedProduct))
                } else {
                    _addToCart.emit(Resource.Error(exception.message.toString()))
                }
            }
        }
    }

    private fun increaseQuantity(documentId: String, cartProduct: CartProduct) {
        firebaseCommon.increaseQuantity(documentId) { _, exception ->
            if (exception == null) {
                viewModelScope.launch {
                    _addToCart.emit(Resource.Success(cartProduct))
                }
            } else {
                viewModelScope.launch {
                    _addToCart.emit(Resource.Error(exception.message.toString()))
                }
            }
        }
    }
}