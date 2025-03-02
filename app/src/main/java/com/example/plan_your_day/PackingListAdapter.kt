package com.example.plan_your_day

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.plan_your_day.databinding.ItemPackingBinding

class PackingListAdapter(private val viewModel: PackingListViewModel) :
    ListAdapter<PackingItem, PackingListAdapter.ViewHolder>(PackingItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPackingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemPackingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PackingItem) {
            binding.itemTextView.text = item.name
            binding.itemCheckBox.isChecked = item.isPacked
            updateTextStyle(item.isPacked)

            binding.itemCheckBox.setOnCheckedChangeListener { _, isChecked ->
                viewModel.toggleItemPacked(item)
            }
        }

        private fun updateTextStyle(isPacked: Boolean) {
            if (isPacked) {
                binding.itemTextView.setTextColor(binding.root.context.getColor(R.color.gray_400))
                binding.itemTextView.paintFlags = binding.itemTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.itemTextView.setTextColor(binding.root.context.getColor(R.color.white))
                binding.itemTextView.paintFlags = binding.itemTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }
    }
}

class PackingItemDiffCallback : DiffUtil.ItemCallback<PackingItem>() {
    override fun areItemsTheSame(oldItem: PackingItem, newItem: PackingItem): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: PackingItem, newItem: PackingItem): Boolean {
        return oldItem == newItem
    }
}