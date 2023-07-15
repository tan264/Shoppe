package com.example.shoppe.models

data class CartProduct(
    val product: Product,
    val quantity: Int,
    val selectedColor: Int? = null,
    val selectedSize: String? = null
) {
    constructor() : this(Product(), 1)
}
