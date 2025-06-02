package com.example.myauto.unified

import androidx.lifecycle.ViewModel
import com.example.myauto.data.SortOrder
import com.example.myauto.data.SortType
import com.example.myauto.data.reverse
import com.example.myauto.room.entity.UnifiedItemEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseViewModel<T : UnifiedItemEntity> : ViewModel() {
    abstract val currentCarId: Int

    protected val _items = MutableStateFlow<List<T>>(emptyList())
    val items: StateFlow<List<UnifiedItemEntity>> = _items

    protected val _sortType = MutableStateFlow(SortType.DATE)
    val sortType: StateFlow<SortType> = _sortType

    protected val _sortOrder = MutableStateFlow(SortOrder.DESC)
    val sortOrder: StateFlow<SortOrder> = _sortOrder

    protected var _currentCarMileage = 0
    fun getCurrentMileage() = _currentCarMileage

    init {
        loadCurrentMileage()
        loadSortedItems()
    }

    fun setSortType(type: SortType) {
        if (_sortType.value != type) {
            _sortType.value = type
        }
    }

    fun toggleSortOrder() {
        _sortOrder.value = _sortOrder.value.reverse()
    }

    abstract fun loadSortedItems()
    abstract fun loadCurrentMileage()
    abstract fun addItem(item: T)
    abstract fun updateItem(item: T)
    abstract fun deleteItem(itemId: Int)
    abstract fun updateCarMileage(newMileage: Int)
}