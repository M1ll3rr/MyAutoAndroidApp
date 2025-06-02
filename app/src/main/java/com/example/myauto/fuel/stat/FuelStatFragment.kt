package com.example.myauto.fuel.stat

import android.view.View
import androidx.fragment.app.viewModels
import com.example.myauto.R
import com.example.myauto.activity.MainActivity
import com.example.myauto.data.statistic.DailyFuelCost
import com.example.myauto.data.statistic.StatFuelSummary
import com.example.myauto.databinding.FragmentFuelStatBinding
import com.example.myauto.unified.BaseStatFragment
import com.example.myauto.unified.UnifiedViewModelFactory
import com.example.myauto.utils.FormatterHelper
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import dev.androidbroadcast.vbpd.viewBinding
import java.util.Date
import kotlin.math.roundToInt

class FuelStatFragment : BaseStatFragment<DailyFuelCost, StatFuelSummary>() {
    private val binding: FragmentFuelStatBinding by viewBinding(FragmentFuelStatBinding::bind)
    override val viewModel: FuelStatViewModel by viewModels {
        UnifiedViewModelFactory(
            (requireActivity() as MainActivity).getRepository()
        )
    }
    override val layoutResId = R.layout.fragment_fuel_stat

    override fun setupObservers() {
        observeData(viewModel.statSummary) { summary ->
            if (summary.totalVolume == 0f) {
                with(binding) {
                    emptyStateTitle.visibility = View.VISIBLE
                    totalCostET.visibility = View.GONE
                    totalVolumeET.visibility = View.GONE
                    avgCostET.visibility = View.GONE
                    totalDiscountET.visibility = View.GONE
                }
            } else {
                with(binding) {
                    emptyStateTitle.visibility = View.GONE
                    totalCostET.visibility = View.VISIBLE
                    totalVolumeET.visibility = View.VISIBLE
                    avgCostET.visibility = View.VISIBLE
                    totalDiscountET.visibility = View.VISIBLE

                    totalVolumeET.text = buildString {
                        append(getString(R.string.total))
                        append(": ")
                        append(summary.totalVolume)
                        append(" ")
                        append(getString(R.string.liter))
                        }
                    totalCostET.text = buildString {
                        append(getString(R.string.total_cost))
                        append(": ")
                        append(summary.totalCost.roundToInt())
                        append(" ")
                        append(getString(R.string.ruble))
                    }
                    avgCostET.text = buildString {
                        append(getString(R.string.average_cost))
                        append(": ")
                        append(summary.averageCost.roundToInt())
                        append(" ")
                        append(getString(R.string.ruble))
                    }
                    totalDiscountET.text = buildString {
                        append(getString(R.string.total_discount))
                        append(": ")
                        append(summary.totalDiscount.roundToInt())
                        append(" ")
                        append(getString(R.string.ruble))
                    }
                }
            }
        }

        observeData(viewModel.dailyCosts) { dailyCosts ->
            if (dailyCosts.isEmpty()) {
                binding.barChart.visibility = View.GONE
                binding.barChartTitle.visibility = View.GONE
                binding.emptyStateTitle.visibility = View.VISIBLE
            } else {
                binding.barChart.visibility = View.VISIBLE
                binding.barChartTitle.visibility = View.VISIBLE
                binding.emptyStateTitle.visibility = View.GONE
                setupBarChart(dailyCosts)
            }
        }
    }


    private fun setupBarChart(dailyCosts: List<DailyFuelCost>) {
        val costEntries = dailyCosts.mapIndexed { index, item ->
            BarEntry(index.toFloat(), item.cost)
        }

        val costDataSet = BarDataSet(costEntries, "").apply {
            color = resources.getColor(R.color.chart_bar, null)
            valueTextSize = 12f
            axisDependency = YAxis.AxisDependency.LEFT
        }

        val maxVolume = dailyCosts.maxOfOrNull { it.volume } ?: 0f
        val volumeAxisMax = maxVolume * 1.5f
        val barData = BarData(costDataSet)

        binding.barChart.apply {
            setFitBars(true)
            data = barData
            description.isEnabled = false
            legend.isEnabled = false

            xAxis.apply {
                valueFormatter = object : ValueFormatter() {
                    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                        val index = value.toInt()
                        return if (index in dailyCosts.indices) {
                            FormatterHelper.formatDateShort(Date(dailyCosts[index].day))
                        } else ""
                    }
                }
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
            }

            axisLeft.apply {
                axisMinimum = 0f
            }

            axisRight.apply {
                axisMinimum = 0f
                axisMaximum = volumeAxisMax
                isEnabled = false
            }

            animateY(500)
            invalidate()
        }
    }
}
