package com.example.myauto.maintenance

import androidx.lifecycle.viewModelScope
import com.example.myauto.AppRepository
import com.example.myauto.room.entity.MaintenanceItemEntity
import com.example.myauto.unified.BaseViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class MaintenanceViewModel(private val repository: AppRepository) : BaseViewModel<MaintenanceItemEntity>() {
    override val currentCarId = repository.currentCarId.value ?: -1

    override fun loadSortedItems() {
        viewModelScope.launch {
            combine(_sortType, _sortOrder) { type, order ->
                Pair(type, order)
            }.flatMapLatest { (type, order) ->
                repository.getSortedMaintenanceItems(type, order)
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

    override fun addItem(item: MaintenanceItemEntity) {
        viewModelScope.launch {
            repository.addMaintenanceItem(item)
        }
    }

    override fun deleteItem(itemId: Int) {
        viewModelScope.launch {
            repository.deleteMaintenanceItem(itemId)
        }
    }

    override fun updateItem(item: MaintenanceItemEntity) {
        viewModelScope.launch {
            repository.updateMaintenanceItem(item)
        }
    }

    override fun updateCarMileage(newMileage: Int) {
        viewModelScope.launch {
            _currentCarMileage = newMileage
            repository.updateMileage(newMileage)
        }
    }
}