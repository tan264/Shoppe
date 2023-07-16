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
import com.example.shoppe.databinding.BillingProductsRvItemBinding
import com.example.shoppe.helper.getProductPrice
import com.example.shoppe.models.CartProduct

class BillingProductAdapter :
    RecyclerView.Adapter<BillingProductAdapter.BillingProductViewHolder>() {
    private val diffCallback = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product == newItem.product
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }

    private val _differ = AsyncListDiffer(this, diffCallback)
    val differ = _differ

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingProductViewHolder {
        return BillingProductViewHolder(
            BillingProductsRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: BillingProductViewHolder, position: Int) {
        val billingProduct = differ.currentList[position]
        holder.bind(billingProduct)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    class BillingProductViewHolder(private val binding: BillingProductsRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(billingProduct: CartProduct) {
            binding.apply {
                Glide.with(itemView).load(billingProduct.product.images[0]).placeholder(
                    CircularProgressDrawable(itemView.context).apply {
                        strokeWidth = 5f
                        centerRadius = 30f
                    }.also {
                        it.start()
                    }).into(imageCartProduct)
                tvProductCartName.text = billingProduct.product.name
                tvBillingProductQuantity.text = billingProduct.quantity.toString()
                tvProductCartPrice.text =
                    if (billingProduct.product.offerPercentage != null && billingProduct.product.offerPercentage != 0f) {
                        String.format(
                            "$%.2f",
                            billingProduct.product.offerPercentage.getProductPrice(billingProduct.product.price)
                        )
                    } else {
                        String.format(
                            "$%.2f",
                            billingProduct.product.price
                        )
                    }
                imageCartProductColor.setImageDrawable(
                    ColorDrawable(
                        billingProduct.selectedColor ?: Color.TRANSPARENT
                    )
                )
                tvCartProductSize.text = billingProduct.selectedSize ?: ""
            }
        }
    }
}
