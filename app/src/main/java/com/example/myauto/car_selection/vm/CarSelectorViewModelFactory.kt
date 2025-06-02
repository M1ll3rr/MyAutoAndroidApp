package com.example.myauto.car_selection.vm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myauto.AppRepository

class CarSelectorViewModelFactory(
    private val repository: AppRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CarSelectorViewModel(repository, application) as T
    }
}