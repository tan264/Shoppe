package com.example.shoppe.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppe.firebase.FirebaseCommon
import com.example.shoppe.helper.getProductPrice
import com.example.shoppe.models.CartProduct
import com.example.shoppe.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon,
) : ViewModel() {

    private val _cartProducts =
        MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    val cartProducts = _cartProducts.asStateFlow()

    val productsPrice = cartProducts.map {
        when (it) {
            is Resource.Success -> {
                calculate(it.data!!)
            }

            else -> null
        }
    }

    private fun calculate(data: List<CartProduct>): Float {
        return data.sumOf {
            if (it.product.offerPercentage != null && it.product.offerPercentage != 0f) {
                (it.product.offerPercentage.getProductPrice(it.product.price)).toDouble()
            } else {
                it.product.price.toDouble()
            }
        }.toFloat()
    }

    private var cartProductDocuments = emptyList<DocumentSnapshot>()

    private var _deleteDialog = MutableSharedFlow<CartProduct>()
    val deleteDialog = _deleteDialog.asSharedFlow()


    init {
        getCartProducts()
    }

    private fun getCartProducts() {
        viewModelScope.launch {
            _cartProducts.emit(Resource.Loading())
        }
        firestore.collection("user").document(auth.uid!!).collection("Cart")
            .addSnapshotListener { value, error ->
                Log.d("CartViewModel", "get list")
                if (error != null || value == null) {
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Error(error?.message.toString()))
                    }
                } else {
                    cartProductDocuments = value.documents
                    val cartProducts = value.toObjects(CartProduct::class.java)
                    Log.d("Cart", cartProducts.size.toString())
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Success(cartProducts))
                    }
                }
            }
    }

    fun changeQuantity(
        cartProduct: CartProduct,
        quantityChanging: FirebaseCommon.QuantityChanging
    ) {
        val index = cartProducts.value.data?.indexOf(cartProduct)

        if (index != null && index != -1) {
            val documentId = cartProductDocuments[index].id
            when (quantityChanging) {
                FirebaseCommon.QuantityChanging.INCREASE -> {
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Loading())
                    }
                    increaseQuantity(documentId)
                }

                FirebaseCommon.QuantityChanging.DECREASE -> {
                    if (cartProduct.quantity == 1) {
                        viewModelScope.launch {
                            _deleteDialog.emit(cartProduct)
                        }
                        return
                    }
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Loading())
                    }
                    decreaseQuantity(documentId)
                }
            }
        }

    }

    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId) { _, exception ->
            if (exception != null) {
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Error(exception.message.toString()))
                }
            }
        }
    }

    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId) { _, exception ->
            if (exception != null) {
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Error(exception.message.toString()))
                }
            }
        }
    }

    fun deleteCartProduct(cartProduct: CartProduct) {
        val index = cartProducts.value.data?.indexOf(cartProduct)
        if (index != null && index != -1) {
            Log.d("TAG", "delete")
            val documentId = cartProductDocuments[index].id
            firestore.collection("user").document(auth.uid!!).collection("Cart")
                .document(documentId).delete()
        }
    }

}