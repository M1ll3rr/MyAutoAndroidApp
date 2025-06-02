package com.example.myauto.unified

import androidx.core.util.Pair
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseStatViewModel<Daily, Summary> : ViewModel() {
    protected val _dailyCosts = MutableLiveData<List<Daily>>()
    val dailyCosts: LiveData<List<Daily>> = _dailyCosts

    protected val _statSummary = MutableLiveData<Summary>()
    val statSummary: LiveData<Summary> = _statSummary

    abstract fun loadStats(startDate: Long, endDate: Long)

    fun setPeriod(selection: Pair<Long, Long>) {
        loadStats(selection.first, selection.second)
    }
}