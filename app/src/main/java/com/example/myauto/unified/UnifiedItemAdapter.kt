package com.example.myauto.unified

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myauto.databinding.ListItemBinding
import com.example.myauto.databinding.ListItemFuelBinding
import com.example.myauto.fuel.FuelItemViewHolder
import com.example.myauto.room.entity.FuelItemEntity
import com.example.myauto.room.entity.MaintenanceItemEntity
import com.example.myauto.room.entity.MaintenanceRepairItem
import com.example.myauto.room.entity.RepairItemEntity
import com.example.myauto.room.entity.UnifiedItemEntity

class UnifiedItemAdapter(
    private val onClick: (UnifiedItemEntity) -> Unit,
    private val onLongClick: (Int) -> Unit
) : ListAdapter<UnifiedItemEntity, RecyclerView.ViewHolder>(UnifiedItemDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_FUEL = 0
        private const val VIEW_TYPE_MAINTENANCE = 1
        private const val VIEW_TYPE_REPAIR = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = getItem(position)) {
            is FuelItemEntity -> VIEW_TYPE_FUEL
            is MaintenanceItemEntity -> VIEW_TYPE_MAINTENANCE
            is RepairItemEntity -> VIEW_TYPE_REPAIR
            else -> throw IllegalArgumentException("Unknown item type: ${item::class.simpleName}")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_FUEL -> {
                val binding = ListItemFuelBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                FuelItemViewHolder(binding)
            }
            VIEW_TYPE_MAINTENANCE, VIEW_TYPE_REPAIR -> {
                val binding = ListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                UnifiedItemViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)

        when (holder) {
            is FuelItemViewHolder -> holder.bind(item as FuelItemEntity)
            is UnifiedItemViewHolder -> holder.bind(item as MaintenanceRepairItem)
        }

        holder.itemView.setOnClickListener { onClick(item) }
        holder.itemView.setOnLongClickListener {
            onLongClick(item.id)
            true
        }
    }

    private class UnifiedItemDiffCallback : DiffUtil.ItemCallback<UnifiedItemEntity>() {
        override fun areItemsTheSame(oldItem: UnifiedItemEntity, newItem: UnifiedItemEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UnifiedItemEntity, newItem: UnifiedItemEntity): Boolean {
            return when {
                oldItem is FuelItemEntity && newItem is FuelItemEntity -> oldItem as FuelItemEntity == newItem as FuelItemEntity
                oldItem is MaintenanceItemEntity && newItem is MaintenanceItemEntity -> oldItem as MaintenanceItemEntity == newItem as MaintenanceItemEntity
                oldItem is RepairItemEntity && newItem is RepairItemEntity -> oldItem as RepairItemEntity == newItem as RepairItemEntity
                else -> false
            }
        }
    }
}