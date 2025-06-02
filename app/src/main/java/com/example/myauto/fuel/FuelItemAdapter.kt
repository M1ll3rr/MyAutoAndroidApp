package com.example.myauto.fuel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.myauto.databinding.ListItemFuelBinding
import com.example.myauto.room.entity.FuelItemEntity

class FuelItemAdapter(
    private val onClick: (FuelItemEntity) -> Unit,
    private val onLongClick: (Int) -> Unit
) : ListAdapter<FuelItemEntity, FuelItemViewHolder>(
    FuelItemDiffCallback()
) {
    private class FuelItemDiffCallback : DiffUtil.ItemCallback<FuelItemEntity>() {
        override fun areItemsTheSame(oldItem: FuelItemEntity, newItem: FuelItemEntity) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: FuelItemEntity, newItem: FuelItemEntity) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FuelItemViewHolder {
        val binding = ListItemFuelBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FuelItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FuelItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)

        holder.itemView.setOnClickListener {
            onClick(item)
        }

        holder.itemView.setOnLongClickListener {
            onLongClick(item.id)
            true
        }
    }
}