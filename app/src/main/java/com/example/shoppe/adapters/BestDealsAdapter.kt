package com.example.shoppe.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.shoppe.databinding.BestDealsRvItemBinding
import com.example.shoppe.models.Product

class BestDealsAdapter : RecyclerView.Adapter<BestDealsAdapter.BestDealsViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    private val _differ = AsyncListDiffer(this, diffCallback)
    val differ = _differ

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealsViewHolder {
        return BestDealsViewHolder(
            BestDealsRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BestDealsViewHolder, position: Int) {
        val product = _differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    override fun getItemCount(): Int {
        return _differ.currentList.size
    }

    var onClick: ((Product) -> Unit)? = null

    class BestDealsViewHolder(private val binding: BestDealsRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView).load(product.images[0])
                    .placeholder(CircularProgressDrawable(itemView.context).apply {
                        strokeWidth = 5f
                        centerRadius = 30f
                    }.also {
                        it.start()
                    }).into(imgBestDeal)
                tvDealProductName.text = product.name
                tvOldPrice.text = product.price.toString()
                product.offerPercentage?.let {
                    if (it != 0f) {
                        tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                        tvNewPrice.text =
                            String.format("%.2f", product.price * (1f - it))
                    }
                }
            }
        }
    }
}