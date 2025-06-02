package com.example.myauto.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myauto.AppRepository
import com.example.myauto.room.entity.UserCarEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserCarViewModel(private val repository: AppRepository) : ViewModel() {
    private val _cars = MutableStateFlow<List<UserCarEntity>>(emptyList())
    val cars: StateFlow<List<UserCarEntity>> = _cars

    val currentCar: StateFlow<UserCarEntity?> = repository.currentCar

    init {
        viewModelScope.launch {
            repository.getAllCars().collect { carsList ->
                _cars.value = carsList
            }
        }
    }

    private fun switchCar(carId: Int) {
        viewModelScope.launch {
            repository.setSelectedCar(carId)
        }
    }


    fun switchToNextCar() {
        viewModelScope.launch {
            val currentIndex = getCurrentCarIndex()
            if (currentIndex < cars.value.lastIndex) {
                switchCar(cars.value[currentIndex + 1].id)
            }
        }
    }

    fun switchToPreviousCar() {
        viewModelScope.launch {
            val currentIndex = getCurrentCarIndex()
            if (currentIndex > 0) {
                switchCar(cars.value[currentIndex - 1].id)
            }
            else if (currentIndex == -1 && getCarsSize() != 0) {
                switchCar(cars.value.last().id)
            }
        }
    }

    fun deleteCar(carId: Int) {
        viewModelScope.launch {
            if (_cars.value.isNotEmpty()) {
                val index = _cars.value.indexOfFirst { it.id == carId }
                val newIndex = when {
                    index < cars.value.size - 1 -> index+1
                    index > 0 -> index - 1
                    else -> -1
                }

                if (newIndex != -1) {
                    repository.setSelectedCar(_cars.value[newIndex].id)
                }
                else {
                    repository.clearSelection()
                }
                repository.deleteCar(carId)
            }
        }
    }

    fun getCurrentCarIndex() : Int {
        return cars.value.indexOfFirst { it == currentCar.value }
    }

    fun getCarsSize() : Int {
        return cars.value.size
    }

    fun clearSelection() {
        viewModelScope.launch {
            repository.clearSelection()
        }
    }

    fun updateCarMileage(newMileage: Int) {
        viewModelScope.launch {
            repository.updateMileage(newMileage)
        }
    }
}