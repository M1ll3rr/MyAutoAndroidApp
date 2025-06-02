package com.example.myauto.unified

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myauto.AppRepository
import com.example.myauto.car_selection.vm.AddCarViewModel
import com.example.myauto.fuel.FuelViewModel
import com.example.myauto.fuel.stat.FuelStatViewModel
import com.example.myauto.insurance.InsuranceViewModel
import com.example.myauto.main.UserCarViewModel
import com.example.myauto.maintenance.MaintenanceViewModel
import com.example.myauto.maintenance.stat.MaintenanceStatViewModel
import com.example.myauto.repair.RepairViewModel
import com.example.myauto.repair.stat.RepairStatViewModel

class UnifiedViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UserCarViewModel::class.java) -> UserCarViewModel(repository) as T
            modelClass.isAssignableFrom(AddCarViewModel::class.java) -> AddCarViewModel(repository) as T

            modelClass.isAssignableFrom(FuelViewModel::class.java) -> FuelViewModel(repository) as T
            modelClass.isAssignableFrom(FuelStatViewModel::class.java) -> FuelStatViewModel(repository) as T

            modelClass.isAssignableFrom(RepairViewModel::class.java) -> RepairViewModel(repository) as T
            modelClass.isAssignableFrom(RepairStatViewModel::class.java) -> RepairStatViewModel(repository) as T

            modelClass.isAssignableFrom(MaintenanceViewModel::class.java) -> MaintenanceViewModel(repository) as T
            modelClass.isAssignableFrom(MaintenanceStatViewModel::class.java) -> MaintenanceStatViewModel(repository) as T

            modelClass.isAssignableFrom(InsuranceViewModel::class.java) -> InsuranceViewModel(repository) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}