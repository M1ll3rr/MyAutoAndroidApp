package com.example.myauto

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myauto.data.SortOrder
import com.example.myauto.data.SortType
import com.example.myauto.notification.NotificationHelper
import com.example.myauto.room.database.AppDatabase
import com.example.myauto.room.entity.CarBrandInfoEntity
import com.example.myauto.room.entity.CarModelInfoEntity
import com.example.myauto.room.entity.FuelItemEntity
import com.example.myauto.room.entity.InsuranceEntity
import com.example.myauto.room.entity.MaintenanceItemEntity
import com.example.myauto.room.entity.RepairItemEntity
import com.example.myauto.room.entity.UserCarEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AppRepository(applicationContext: Context) {
    private val db = AppDatabase.getDatabase(applicationContext)
    private val context = applicationContext

    private val _currentCarId = MutableLiveData<Int?>(null)
    val currentCarId: LiveData<Int?> = _currentCarId

    private val _currentCar = MutableStateFlow<UserCarEntity?>(null)
    val currentCar: StateFlow<UserCarEntity?> = _currentCar

    private val carInfoDao = db.carInfoDao()
    private val userCarsDao = db.userCarsDao()
    private val fuelDao = db.fuelDao()
    private val repairDao = db.repairDao()
    private val maintenanceDao = db.maintenanceDao()
    private val insuranceDao = db.insuranceDao()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val selectedCar = userCarsDao.getSelectedCar()
            if (selectedCar != null) {
                _currentCarId.postValue(selectedCar.id)
                _currentCar.value = selectedCar
            } else {
                val cars = userCarsDao.getAllUserCars().first()
                if (cars.isNotEmpty()) {
                    setSelectedCar(cars.first().id)
                }
            }
        }
    }

    fun getAllCars() = userCarsDao.getAllUserCars()
    suspend fun addCar(car: UserCarEntity) {
        userCarsDao.clearAllSelections()
        userCarsDao.insertUserCar(car)
        _currentCar.value = userCarsDao.getSelectedCar()
        _currentCarId.value = _currentCar.value?.id
    }
    suspend fun addAllCars(cars: List<UserCarEntity>) = userCarsDao.insertAll(cars)
    suspend fun deleteCar(carId: Int) = userCarsDao.deleteUserCar(carId)
    suspend fun deleteAllCars() = userCarsDao.deleteAll()
    suspend fun clearSelection() {
        userCarsDao.clearAllSelections()
        _currentCarId.postValue(null)
        _currentCar.value = null
    }
    suspend fun setSelectedCar(carId: Int) {
        userCarsDao.clearAllSelections()
        userCarsDao.setSelectedCar(carId)
        _currentCarId.postValue(carId)
        _currentCar.value = userCarsDao.getSelectedCar()
    }

    suspend fun updateMileage(newMileage: Int) {
        userCarsDao.updateMileage(currentCarId.value ?: -1, newMileage)
        _currentCar.value = userCarsDao.getSelectedCar()
        checkForNotification(newMileage)
    }
    suspend fun getCurrentMileage(): Int {
        val car = userCarsDao.getSelectedCar()
        return car?.mileage ?: 0
    }

    private suspend fun checkForNotification(newMileage: Int) {
        val items = getMaintenanceItemsForMileageNotification(newMileage)
        items.forEach {
            NotificationHelper.showNotification(context, it)
        }
    }

    suspend fun getBrands() = carInfoDao.getBrands()
    suspend fun getModelsByBrand(brandId: String) = carInfoDao.getModelsByBrand(brandId)
    suspend fun getBrandNameById(brandId: String) = carInfoDao.getBrandNameById(brandId)
    suspend fun addBrands(brands: List<CarBrandInfoEntity>) = carInfoDao.insertBrands(brands)
    suspend fun addModels(models: List<CarModelInfoEntity>) = carInfoDao.insertModels(models)


    suspend fun getPolicyFile() = insuranceDao.getFileByCar(currentCarId.value ?: -1)
    suspend fun addPolicyFile(uri: String) = insuranceDao.insert(InsuranceEntity(carId = currentCarId.value ?: -1, documentUri = uri))
    suspend fun updatePolicyFile(uri: String) = insuranceDao.updateFileUri(currentCarId.value ?: -1, uri)
    suspend fun deletePolicyFile() = insuranceDao.deleteFile(currentCarId.value ?: -1)


    fun getAllFuelItems() = fuelDao.getAllItems()
    fun getSortedFuelItems(sortType: SortType, sortOrder: SortOrder) = fuelDao.getSortedItemsByCar(currentCarId.value ?: -1, sortType.name, sortOrder.name)
    suspend fun addFuelItem(item: FuelItemEntity) = fuelDao.insertItem(item)
    suspend fun addAllFuelItems(items: List<FuelItemEntity>) = fuelDao.insertAll(items)
    suspend fun updateFuelItem(item: FuelItemEntity) = fuelDao.updateItem(item)
    suspend fun deleteFuelItem(itemId: Int) = fuelDao.deleteItemById(itemId)
    suspend fun deleteAllFuelItems() = fuelDao.deleteAll()
    suspend fun getFuelStatsDaily(startDate: Long, endDate: Long) = fuelDao.getDailyCosts(currentCarId.value ?: -1, startDate, endDate)
    suspend fun getFuelStatsSummary(startDate: Long, endDate: Long) = fuelDao.getSummary(currentCarId.value ?: -1, startDate, endDate)


    fun getAllMaintenanceItems() = maintenanceDao.getAllItems()
    fun getSortedMaintenanceItems(sortType: SortType, sortOrder: SortOrder) = maintenanceDao.getSortedItemsByCar(currentCarId.value ?: -1, sortType.name, sortOrder.name)
    suspend fun addMaintenanceItem(item: MaintenanceItemEntity) = maintenanceDao.insertItem(item)
    suspend fun addAllMaintenanceItems(items: List<MaintenanceItemEntity>) = maintenanceDao.insertAll(items)
    suspend fun updateMaintenanceItem(item: MaintenanceItemEntity) = maintenanceDao.updateItem(item)
    suspend fun deleteMaintenanceItem(itemId: Int) = maintenanceDao.deleteItemById(itemId)
    suspend fun deleteAllMaintenanceItems() = maintenanceDao.deleteAll()
    suspend fun getMaintenanceStatsDaily(startDate: Long, endDate: Long) = maintenanceDao.getDailyCosts(currentCarId.value ?: -1, startDate, endDate)
    suspend fun getMaintenanceStatsCategory(startDate: Long, endDate: Long) = maintenanceDao.getCategoryCosts(currentCarId.value ?: -1, startDate, endDate)
    suspend fun getMaintenanceStatsSummary(startDate: Long, endDate: Long) = maintenanceDao.getSummary(currentCarId.value ?: -1, startDate, endDate)
    suspend fun getMaintenanceItemsForDateNotification(currentDate: Long) = maintenanceDao.getItemsForDateNotification(currentDate)
    suspend fun getMaintenanceItemById(id: Int) = maintenanceDao.getItemById(id)
    suspend fun updateMaintenanceNotificationStatus(id: Int, mileage: Int?, date: Long?) = maintenanceDao.updateNotificationStatus(id, mileage, date)
    private suspend fun getMaintenanceItemsForMileageNotification(currentMileage: Int) = maintenanceDao.getItemsForMileageNotification(currentMileage)


    fun getAllRepairItems() = repairDao.getAllItems()
    fun getSortedRepairItems(sortType: SortType, sortOrder: SortOrder) = repairDao.getSortedItemsByCar(currentCarId.value ?: -1, sortType.name, sortOrder.name)
    suspend fun addRepairItem(item: RepairItemEntity) = repairDao.insertItem(item)
    suspend fun addAllRepairItems(items: List<RepairItemEntity>) = repairDao.insertAll(items)
    suspend fun updateRepairItem(item: RepairItemEntity) = repairDao.updateItem(item)
    suspend fun deleteRepairItem(itemId: Int) = repairDao.deleteItemById(itemId)
    suspend fun deleteAllRepairItems() = repairDao.deleteAll()
    suspend fun getRepairStatsDaily(startDate: Long, endDate: Long) = repairDao.getDailyCosts(currentCarId.value ?: -1, startDate, endDate)
    suspend fun getRepairStatsCategory(startDate: Long, endDate: Long) = repairDao.getCategoryCosts(currentCarId.value ?: -1, startDate, endDate)
    suspend fun getRepairStatsSummary(startDate: Long, endDate: Long) = repairDao.getSummary(currentCarId.value ?: -1, startDate, endDate)
}