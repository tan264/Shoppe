package com.example.shoppe.models.order

import com.example.shoppe.models.Address
import com.example.shoppe.models.CartProduct

data class Order(
    val orderStatus: String,
    val totalPrice: Float,
    val products: List<CartProduct>,
    val address: Address,
)
