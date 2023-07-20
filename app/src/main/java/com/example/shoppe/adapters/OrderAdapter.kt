package com.example.shoppe.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppe.R
import com.example.shoppe.databinding.OrderItemBinding
import com.example.shoppe.models.order.Order
import com.example.shoppe.models.order.OrderStatus
import com.example.shoppe.models.order.getOrderStatus

class OrderAdapter : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
    private val diffCallback = object : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.products == newItem.products
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }

    private val _differ = AsyncListDiffer(this, diffCallback)
    val differ = _differ

    var onClick: ((Order) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        return OrderViewHolder(
            OrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = differ.currentList[position]

        holder.bind(order)
    }

    class OrderViewHolder(private val binding: OrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.apply {
                tvOrderId.text = order.orderId.toString()
                tvOrderDate.text = order.date
                val resource = itemView.resources
                val colorDrawable = when (getOrderStatus(order.orderStatus)) {
                    is OrderStatus.Ordered -> ColorDrawable(resource.getColor(R.color.g_orange_yellow))
                    is OrderStatus.Confirmed -> ColorDrawable(resource.getColor(R.color.g_green))
                    is OrderStatus.Delivered -> ColorDrawable(resource.getColor(R.color.g_green))
                    is OrderStatus.Shipped -> ColorDrawable(resource.getColor(R.color.g_green))
                    is OrderStatus.Canceled -> ColorDrawable(resource.getColor(R.color.g_red))
                    is OrderStatus.Returned -> ColorDrawable(resource.getColor(R.color.g_red))
                }

                imageOrderState.setImageDrawable(colorDrawable)
            }
        }
    }
}
