package com.example.myauto.fuel

import androidx.recyclerview.widget.RecyclerView
import com.example.myauto.R
import com.example.myauto.databinding.ListItemFuelBinding
import com.example.myauto.room.entity.FuelItemEntity
import com.example.myauto.utils.FormatterHelper
import kotlin.math.roundToInt

class FuelItemViewHolder(private val binding: ListItemFuelBinding)
    : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: FuelItemEntity) {
        val context = binding.root.context
        with(binding) {
            dateTextView.text = FormatterHelper.formatDate(item.date)
            volumeTextView.text = buildString {
            append(item.volume.roundToInt())
            append(" ")
            append(context.getString(R.string.liter))
            }
            costTextView.text = buildString {
            append(item.totalCost.roundToInt())
            append(" ₽")
            }
        }
    }
}