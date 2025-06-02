package com.example.myauto.car_selection.vm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myauto.AppRepository
import com.example.myauto.room.dto.BrandJson
import com.example.myauto.room.entity.CarBrandInfoEntity
import com.example.myauto.room.entity.CarModelInfoEntity
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val CARS_DATA = "cars.json"

class CarSelectorViewModel(private val repository: AppRepository, private val application: Application) : ViewModel() {
    private val _brands = MutableStateFlow<List<CarBrandInfoEntity>>(emptyList())
    val brands: StateFlow<List<CarBrandInfoEntity>> = _brands

    private var _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            _isLoading.value = true
            withContext(Dispatchers.IO) {
                populateDatabase()
                _brands.value = repository.getBrands()
            }
            _isLoading.value = false
        }
    }

    private suspend fun populateDatabase() {
        if (repository.getBrands().isEmpty()) {
            val jsonString = application.assets.open(CARS_DATA)
                .bufferedReader().use { it.readText() }
            val brandsJson = Gson().fromJson(jsonString, Array<BrandJson>::class.java).toList()

            val brands = brandsJson.map { it.toEntity() }
            val models = brandsJson.flatMap { brand ->
                brand.models.map { model ->
                    model.toEntity(brand.id)
                }
            }

            repository.addBrands(brands)
            repository.addModels(models)
        }
    }

    suspend fun getModels(brandId: String): List<CarModelInfoEntity> {
        return withContext(Dispatchers.IO) {
            repository.getModelsByBrand(brandId)
        }
    }

    suspend fun getBrandNameById(brandId: String): String {
        return withContext(Dispatchers.IO) {
            repository.getBrandNameById(brandId)
        }
    }

}
