package com.example.myauto.fuel

import androidx.lifecycle.viewModelScope
import com.example.myauto.AppRepository
import com.example.myauto.room.entity.FuelItemEntity
import com.example.myauto.unified.BaseViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class FuelViewModel(private val repository: AppRepository) : BaseViewModel<FuelItemEntity>() {
    override val currentCarId = repository.currentCarId.value ?: -1

    override fun loadSortedItems() {
        viewModelScope.launch {
            combine(_sortType, _sortOrder) { type, order ->
                Pair(type, order)
            }.flatMapLatest { (type, order) ->
                repository.getSortedFuelItems(type, order)
            }.collect { newItems ->
                _items.value = newItems
            }
        }
    }

    override fun loadCurrentMileage() {
        viewModelScope.launch {
            _currentCarMileage = repository.getCurrentMileage()
        }
    }

    override fun addItem(item: FuelItemEntity) {
        viewModelScope.launch {
            repository.addFuelItem(item)
        }
    }

    override fun deleteItem(itemId: Int) {
        viewModelScope.launch {
            repository.deleteFuelItem(itemId)
        }
    }

    override fun updateItem(item: FuelItemEntity) {
        viewModelScope.launch {
            repository.updateFuelItem(item)
        }
    }

    override fun updateCarMileage(newMileage: Int) {
        viewModelScope.launch {
            _currentCarMileage = newMileage
            repository.updateMileage(newMileage)
        }
    }
}