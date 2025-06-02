package com.example.myauto.car_selection.vm

import androidx.lifecycle.ViewModel
import com.example.myauto.AppRepository
import com.example.myauto.data.BodyTypeData
import com.example.myauto.room.entity.UserCarEntity

class AddCarViewModel(private val repository: AppRepository) : ViewModel() {
    suspend fun addCar(brandId: String, modelId: String, year: Int, bodyType: BodyTypeData) {
        val newCar = UserCarEntity(
            brand = brandId,
            model = modelId,
            year = year,
            carBodyType = bodyType.ordinal,
            isSelected = true
        )
        repository.addCar(newCar)
    }
}