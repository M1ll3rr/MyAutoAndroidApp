package com.example.myauto.repair

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myauto.AppRepository
import com.example.myauto.repair.stat.RepairStatViewModel

class RepairViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RepairViewModel::class.java) -> RepairViewModel(repository) as T
            modelClass.isAssignableFrom(RepairStatViewModel::class.java) -> RepairStatViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}