package com.example.myauto.car_selection.rcview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.myauto.databinding.ListItemSelectorBinding
import com.example.myauto.room.entity.CarInfoEntity

class SelectorAdapter(
    private val onItemClick: (CarInfoEntity) -> Unit
) : ListAdapter<CarInfoEntity, SelectorViewHolder>(DiffCallback()) {

    private class DiffCallback : DiffUtil.ItemCallback<CarInfoEntity>() {
        override fun areItemsTheSame(oldItem: CarInfoEntity, newItem: CarInfoEntity): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: CarInfoEntity, newItem: CarInfoEntity): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectorViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemSelectorBinding.inflate(inflater, parent, false)
        return SelectorViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: SelectorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}