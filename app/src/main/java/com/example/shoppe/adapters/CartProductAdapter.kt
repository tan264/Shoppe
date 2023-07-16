package com.example.shoppe.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.shoppe.databinding.CartProductItemBinding
import com.example.shoppe.helper.getProductPrice
import com.example.shoppe.models.CartProduct

class CartProductAdapter :
    RecyclerView.Adapter<CartProductAdapter.CartProductsViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }

    private val _differ = AsyncListDiffer(this, diffCallback)
    val differ = _differ

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductsViewHolder {
        return CartProductsViewHolder(
            CartProductItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CartProductsViewHolder, position: Int) {
        val cartProduct = _differ.currentList[position]
        holder.bind(cartProduct)

        holder.itemView.setOnClickListener {
            onCartProductClick?.invoke(cartProduct)
        }

        holder.binding.imagePlus.setOnClickListener {
            onPlusClick?.invoke(cartProduct)
        }

        holder.binding.imageMinus.setOnClickListener {
            onMinusClick?.invoke(cartProduct)
        }
    }

    override fun getItemCount(): Int {
        return _differ.currentList.size
    }

    var onCartProductClick: ((CartProduct) -> Unit)? = null
    var onPlusClick: ((CartProduct) -> Unit)? = null
    var onMinusClick: ((CartProduct) -> Unit)? = null

    class CartProductsViewHolder(val binding: CartProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cartProduct: CartProduct) {
            binding.apply {
                Glide.with(itemView).load(cartProduct.product.images[0])
                    .placeholder(CircularProgressDrawable(itemView.context).apply {
                        strokeWidth = 5f
                        centerRadius = 30f
                    }.also {
                        it.start()
                    }).into(imageCartProduct)
                tvProductCartName.text = cartProduct.product.name
                tvProductCartQuantity.text = cartProduct.quantity.toString()
                if (cartProduct.product.offerPercentage != null && cartProduct.product.offerPercentage != 0f) {
                    tvProductCartPrice.text =
                        String.format(
                            "$%.2f",
                            cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price)
                        )
                }
                tvProductCartPrice.text =
                    if (cartProduct.product.offerPercentage != null && cartProduct.product.offerPercentage != 0f) {
                        String.format(
                            "$%.2f",
                            cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price)
                        )
                    } else {
                        String.format(
                            "$%.2f",
                            cartProduct.product.price
                        )
                    }
                imageCartProductColor.setImageDrawable(
                    ColorDrawable(
                        cartProduct.selectedColor ?: Color.TRANSPARENT
                    )
                )
                tvCartProductSize.text = cartProduct.selectedSize ?: ""
            }
        }
    }
}