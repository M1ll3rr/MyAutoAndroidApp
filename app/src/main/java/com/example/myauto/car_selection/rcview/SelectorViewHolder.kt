package com.example.myauto.car_selection.rcview

import androidx.recyclerview.widget.RecyclerView
import com.example.myauto.databinding.ListItemSelectorBinding
import com.example.myauto.room.entity.CarInfoEntity

class SelectorViewHolder(
    private val binding: ListItemSelectorBinding,
    private val onItemClick: (CarInfoEntity) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: CarInfoEntity) {
        binding.listItem.text = item.name
        itemView.setOnClickListener { onItemClick(item) }
    }
}