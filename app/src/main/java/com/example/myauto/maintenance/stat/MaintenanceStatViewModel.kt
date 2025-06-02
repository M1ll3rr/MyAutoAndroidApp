package com.example.myauto.maintenance.stat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myauto.AppRepository
import com.example.myauto.data.statistic.CategoryCost
import com.example.myauto.data.statistic.DailyCost
import com.example.myauto.data.statistic.StatSummary
import com.example.myauto.unified.BaseStatViewModel
import kotlinx.coroutines.launch

class MaintenanceStatViewModel(private val repository: AppRepository) : BaseStatViewModel<DailyCost, StatSummary>() {
    private val _categoryCosts = MutableLiveData<List<CategoryCost>>()
    val categoryCosts: LiveData<List<CategoryCost>> = _categoryCosts

    override fun loadStats(startDate: Long, endDate: Long) {
        viewModelScope.launch {
            _dailyCosts.value = repository.getMaintenanceStatsDaily(startDate, endDate)
            _categoryCosts.value = repository.getMaintenanceStatsCategory(startDate, endDate)
            _statSummary.value = repository.getMaintenanceStatsSummary(startDate, endDate)
        }
    }
}

