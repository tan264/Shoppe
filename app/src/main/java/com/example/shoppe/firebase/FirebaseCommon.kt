package com.example.shoppe.firebase

import android.util.Log
import com.example.shoppe.models.CartProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseCommon(
    private val firestore: FirebaseFirestore,
    auth: FirebaseAuth,
) {

    private val cartCollection =
        firestore.collection("user").document(auth.uid!!).collection("Cart")

    fun addProductToCart(cartProduct: CartProduct, onResult: (CartProduct?, Exception?) -> Unit) {
        cartCollection.document().set(cartProduct).addOnSuccessListener {
            Log.d("FirebaseCommon", cartProduct.toString())
            onResult(cartProduct, null)
        }.addOnFailureListener {
            onResult(cartProduct, it)
            Log.e("FirebaseCommon", it.message.toString())
        }
    }

    fun increaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {
        firestore.runTransaction { transaction ->
            val documentRef = cartCollection.document(documentId)
            val document = transaction.get(documentRef)
            val product = document.toObject(CartProduct::class.java)
            product?.let {
                val newQuantity = it.quantity + 1
                val newProduct = it.copy(quantity = newQuantity)
                transaction.set(documentRef, newProduct)
            }
        }.addOnSuccessListener {
            Log.d("FirebaseCommon", documentId)
            onResult(documentId, null)
        }.addOnFailureListener {
            Log.e("FirebaseCommon", it.message.toString())
            onResult(documentId, it)
        }
    }

    fun decreaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {
        firestore.runTransaction { transaction ->
            val documentRef = cartCollection.document(documentId)
            val document = transaction.get(documentRef)
            val product = document.toObject(CartProduct::class.java)
            product?.let {
                val newQuantity = it.quantity - 1
                val newProduct = it.copy(quantity = newQuantity)
                transaction.set(documentRef, newProduct)
            }
        }.addOnSuccessListener {
            Log.d("FirebaseCommon", documentId)
            onResult(documentId, null)
        }.addOnFailureListener {
            Log.e("FirebaseCommon", it.message.toString())
            onResult(documentId, it)
        }
    }

    enum class QuantityChanging {
        INCREASE, DECREASE
    }
}