package com.example.myauto.unified

import androidx.recyclerview.widget.RecyclerView
import com.example.myauto.R
import com.example.myauto.databinding.ListItemBinding
import com.example.myauto.room.entity.MaintenanceRepairItem
import com.example.myauto.utils.FormatterHelper
import kotlin.math.roundToInt

class UnifiedItemViewHolder(private val binding: ListItemBinding)
    : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: MaintenanceRepairItem) {
        with(binding) {
            val context = root.context
            dateTextView.text = FormatterHelper.formatDate(item.date)

            mileageTV.text = buildString {
                append(item.mileage)
                append(" ")
                append(context.getString(R.string.km))
            }

            categoryTextView.text = item.category
            titleTextView.text = item.title ?: item.brand ?: ""

            costTextView.text = buildString {
                append(item.totalCost.roundToInt())
                append(" ")
                append(context.getString(R.string.ruble))
            }
        }
    }
}