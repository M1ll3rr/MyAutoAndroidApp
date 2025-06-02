package com.example.myauto.maintenance.stat

import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.myauto.R
import com.example.myauto.activity.MainActivity
import com.example.myauto.data.statistic.CategoryCost
import com.example.myauto.data.statistic.DailyCost
import com.example.myauto.data.statistic.StatSummary
import com.example.myauto.databinding.FragmentMaintenanceStatBinding
import com.example.myauto.unified.BaseStatFragment
import com.example.myauto.unified.UnifiedViewModelFactory
import com.example.myauto.utils.FormatterHelper
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import dev.androidbroadcast.vbpd.viewBinding
import java.util.Date
import kotlin.math.roundToInt

class MaintenanceStatFragment : BaseStatFragment<DailyCost, StatSummary>() {
    private val binding: FragmentMaintenanceStatBinding by viewBinding(
        FragmentMaintenanceStatBinding::bind
    )
    override val viewModel: MaintenanceStatViewModel by viewModels {
        UnifiedViewModelFactory(
            (requireActivity() as MainActivity).getRepository()
        )
    }
    override val layoutResId = R.layout.fragment_maintenance_stat

    override fun setupObservers() {
        observeData(viewModel.statSummary) { summary ->
            if (summary.count == 0) {
                with(binding) {
                    emptyStateTitle.visibility = View.VISIBLE
                    totalCostET.visibility = View.GONE
                    countET.visibility = View.GONE
                    avgCostET.visibility = View.GONE
                }
            } else {
                with(binding) {
                    emptyStateTitle.visibility = View.GONE
                    totalCostET.visibility = View.VISIBLE
                    countET.visibility = View.VISIBLE
                    avgCostET.visibility = View.VISIBLE

                    totalCostET.text = buildString {
                        append(getString(R.string.total_cost))
                        append(": ")
                        append(summary.totalCost.roundToInt())
                        append(" ")
                        append(getString(R.string.ruble))
                    }
                    countET.text = buildString {
                        append(getString(R.string.total))
                        append(": ")
                        append(summary.count)
                    }
                    avgCostET.text = buildString {
                        append(getString(R.string.average_cost))
                        append(": ")
                        append(summary.averageCost.roundToInt())
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
            } else {
                binding.barChart.visibility = View.VISIBLE
                binding.barChartTitle.visibility = View.VISIBLE
                setupBarChart(dailyCosts)
            }
        }

        observeData(viewModel.categoryCosts) { categoryCosts ->
            if (categoryCosts.isEmpty()) {
                binding.radarChart.visibility = View.GONE
                binding.radarChartTitle.visibility = View.GONE
            } else {
                binding.radarChart.visibility = View.VISIBLE
                binding.radarChartTitle.visibility = View.VISIBLE
                setupRadarChart(categoryCosts)
            }
        }
    }

    private fun setupBarChart(dailyCosts: List<DailyCost>) {
        val entries = dailyCosts.mapIndexed { index, cost ->
            BarEntry(index.toFloat(), cost.total)
        }

        val dataSet = BarDataSet(entries, "").apply {
            color = ContextCompat.getColor(requireContext(), R.color.chart_bar)
            valueTextSize = 12f
            setDrawValues(true)
        }

        val xAxisFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                val index = value.toInt()
                return if (index in dailyCosts.indices) {
                    FormatterHelper.formatDateShort(Date(dailyCosts[index].day))
                } else {
                    ""
                }
            }
        }

        binding.barChart.apply {
            data = BarData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            setFitBars(true)

            xAxis.apply {
                valueFormatter = xAxisFormatter
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setLabelCount(dailyCosts.size, false)
            }

            axisLeft.axisMinimum = 0f
            axisRight.isEnabled = false

            animateY(500)
            invalidate()
        }
    }

    private fun setupRadarChart(categoryCosts: List<CategoryCost>) {
        val entries = categoryCosts.mapIndexed { index, cost ->
            RadarEntry(cost.total, cost.category)
        }

        val dataSet = RadarDataSet(entries, "").apply {
            color = ContextCompat.getColor(requireContext(), R.color.chart_radar_line)
            fillColor = ContextCompat.getColor(requireContext(), R.color.chart_radar_fill)
            setDrawFilled(true)
            fillAlpha = 50
            lineWidth = 2f
            valueTextSize = 12f
        }


        binding.radarChart.apply {
            data = RadarData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false

            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return categoryCosts.getOrNull(value.toInt())?.category ?: ""
                }
            }

            yAxis.axisMinimum = 0f
            yAxis.setDrawLabels(false)
            setTouchEnabled(true)
            animateY(500)
            invalidate()
        }
    }
}

