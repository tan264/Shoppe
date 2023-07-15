package com.example.shoppe.helper

fun Float.getProductPrice(price: Float): Float {
    val remainPricePercentage = 1f - this
    return remainPricePercentage * price
}