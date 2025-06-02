package com.example.myauto.repair

import androidx.lifecycle.viewModelScope
import com.example.myauto.AppRepository
import com.example.myauto.room.entity.RepairItemEntity
import com.example.myauto.unified.BaseViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class RepairViewModel(private val repository: AppRepository) : BaseViewModel<RepairItemEntity>() {
    override val currentCarId = repository.currentCarId.value ?: -1

    override fun loadSortedItems() {
        viewModelScope.launch {
            combine(_sortType, _sortOrder) { type, order ->
                Pair(type, order)
            }.flatMapLatest { (type, order) ->
                repository.getSortedRepairItems(type, order)
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

    override fun addItem(item: RepairItemEntity) {
        viewModelScope.launch {
            repository.addRepairItem(item)
        }
    }

    override fun deleteItem(itemId: Int) {
        viewModelScope.launch {
            repository.deleteRepairItem(itemId)
        }
    }

    override fun updateItem(item: RepairItemEntity) {
        viewModelScope.launch {
            repository.updateRepairItem(item)
        }
    }

    override fun updateCarMileage(newMileage: Int) {
        viewModelScope.launch {
            _currentCarMileage = newMileage
            repository.updateMileage(newMileage)
        }
    }
}