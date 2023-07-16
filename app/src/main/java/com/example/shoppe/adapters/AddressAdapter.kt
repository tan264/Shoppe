package com.example.shoppe.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppe.R
import com.example.shoppe.databinding.AddressRvItemBinding
import com.example.shoppe.models.Address

class AddressAdapter : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {
    private val diffCallback = object : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.addressTitle == newItem.addressTitle && oldItem.fullName == newItem.fullName
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }
    }

    private val _differ = AsyncListDiffer(this, diffCallback)
    val differ = _differ

    var onClick: ((Address) -> Unit)? = null

    private var selectedAddress = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder(
            AddressRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = differ.currentList[position]
        holder.bind(address, position)

        holder.binding.buttonAddress.setOnClickListener {
            if (selectedAddress >= 0) {
                notifyItemChanged(selectedAddress)
            }
            onClick?.invoke(address)
            selectedAddress = holder.adapterPosition
            notifyItemChanged(selectedAddress)
        }
    }

    inner class AddressViewHolder(val binding: AddressRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(address: Address, position: Int) {
            binding.apply {
                buttonAddress.text = address.addressTitle
                if (position == selectedAddress) {
                    buttonAddress.setBackgroundResource(R.drawable.selected_size_border)
                } else {
                    buttonAddress.setBackgroundResource(R.drawable.unselected_button_background)
                }
            }
        }
    }
}
