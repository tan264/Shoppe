package com.example.shoppe.dialog

import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppe.R
import com.example.shoppe.adapters.ColorsAdapter
import com.example.shoppe.adapters.SizesAdapter
import com.example.shoppe.fragments.shopping.ProductDetailsFragment
import com.example.shoppe.fragments.shopping.ProductDetailsFragment.Companion.TAG
import com.example.shoppe.models.CartProduct
import com.example.shoppe.models.Product
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

fun ProductDetailsFragment.setupBottomAddToCart(
    product: Product,
    onSendClick: (CartProduct) -> Unit,
) {
    val colors = product.colors
    val sizes = product.sizes
    var selectedColor: Int? = null
    var selectedSize: String? = null
    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
    val view = layoutInflater.inflate(R.layout.add_to_cart_dialog, null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()

    val colorsAdapter = ColorsAdapter() { color ->
        color.let {
            selectedColor = color
            Log.d(TAG, "color: $selectedColor")
        }
    }
    val sizeAdapter = SizesAdapter() {
        selectedSize = it
        Log.d(TAG, "size: $selectedSize")
    }
    colors?.let {
        colorsAdapter.differ.submitList(it)
    }

    sizes?.let {
        sizeAdapter.differ.submitList(it)
    }

    val rvColors = view.findViewById<RecyclerView>(R.id.rv_colors)
    val rvSizes = view.findViewById<RecyclerView>(R.id.rv_sizes)
    val button = view.findViewById<Button>(R.id.button_add_to_cart)
    rvColors.apply {
        adapter = colorsAdapter
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }
    rvSizes.apply {
        adapter = sizeAdapter
        layoutManager = GridLayoutManager(context, 3)
    }
    button.setOnClickListener {
        onSendClick(CartProduct(product, 1, selectedColor, selectedSize))
        dialog.dismiss()
    }
}