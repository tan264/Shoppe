package com.example.shoppe.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.shoppe.databinding.ViewPagerImageItemBinding

class ViewPagerImagesAdapter :
    RecyclerView.Adapter<ViewPagerImagesAdapter.ViewPagerImagesViewHolder>() {

    class ViewPagerImagesViewHolder(val binding: ViewPagerImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(s: String) {
            Glide.with(itemView).load(s)
                .placeholder(CircularProgressDrawable(itemView.context).apply {
                    strokeWidth = 5f
                    centerRadius = 30f
                }.also {
                    it.start()
                }).into(binding.imageProduct)
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    private val _differ = AsyncListDiffer(this, diffCallback)
    val differ = _differ

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerImagesViewHolder {
        return ViewPagerImagesViewHolder(
            ViewPagerImageItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewPagerImagesViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }
}


