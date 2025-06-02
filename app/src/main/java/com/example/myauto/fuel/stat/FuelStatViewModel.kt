package com.example.myauto.fuel.stat

import androidx.lifecycle.viewModelScope
import com.example.myauto.AppRepository
import com.example.myauto.data.statistic.DailyFuelCost
import com.example.myauto.data.statistic.StatFuelSummary
import com.example.myauto.unified.BaseStatViewModel
import kotlinx.coroutines.launch

class FuelStatViewModel(private val repository: AppRepository) : BaseStatViewModel<DailyFuelCost, StatFuelSummary>() {
    override fun loadStats(startDate: Long, endDate: Long) {
        viewModelScope.launch {
            _dailyCosts.value = repository.getFuelStatsDaily( startDate, endDate)
            _statSummary.value =  repository.getFuelStatsSummary(startDate, endDate)
        }
    }
}
