package com.example.myauto.room

import android.content.Context
import com.example.myauto.R
import com.example.myauto.room.database.AppDatabase
import com.example.myauto.room.entity.FuelItemEntity
import com.example.myauto.room.entity.MaintenanceItemEntity
import com.example.myauto.room.entity.RepairItemEntity
import com.example.myauto.room.entity.UserCarEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import kotlin.random.Random

class DatabaseInitializer(private val context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val random = Random(System.currentTimeMillis())


    suspend fun populateDatabase() = withContext(Dispatchers.IO) {
        db.userCarsDao().deleteAll()
        db.fuelDao().deleteAll()
        db.maintenanceDao().deleteAll()
        db.repairDao().deleteAll()

        val testCar1 = UserCarEntity(
            brand = "Toyota",
            model = "Camry",
            year = 2020,
            carBodyType = 0,
            mileage = 15000,
            isSelected = true
        )
        val testCar2 = UserCarEntity(
            brand = "Opel",
            model = "Corsa",
            year = 2012,
            carBodyType = 1,
            mileage = 120000,
            isSelected = false
        )
        db.userCarsDao().insertUserCar(testCar1)
        db.userCarsDao().insertUserCar(testCar2)

        generateFuelItems(1, testCar1.mileage)
        generateMaintenanceItems(1, testCar1.mileage)
        generateRepairItems(1, testCar1.mileage)

        generateFuelItems(2, testCar2.mileage)
        generateMaintenanceItems(2, testCar2.mileage)
        generateRepairItems(2, testCar2.mileage)
    }

    private suspend fun generateFuelItems(carId: Int, startMileage: Int) {
        val calendar = Calendar.getInstance()

        for (i in 1..100) {
            calendar.add(Calendar.DAY_OF_YEAR, -5)
            val newFuelPrice = random.nextDouble(63.0, 68.0).toFloat()
            val newVolume = random.nextDouble(5.0, 60.0).toFloat()
            val newCost = newVolume * newFuelPrice
            val newDiscount = random.nextDouble((newCost/100).toDouble(), (newCost/10).toDouble()).toFloat()

            db.fuelDao().insertItem(
                FuelItemEntity(
                    carId = carId,
                    date = Date(calendar.timeInMillis),
                    mileage = startMileage - i * 50,
                    fuelType = "АИ-95+",
                    volume = newVolume,
                    totalCost = newVolume * newFuelPrice,
                    fuelPrice = newFuelPrice,
                    discount = if (i % 10 == 0) newDiscount else 0f
                )
            )
        }
    }

    private suspend fun generateMaintenanceItems(carId: Int, startMileage: Int) {
        val calendar = Calendar.getInstance()
        val categories = withContext(Dispatchers.Main) {
            context.resources.getStringArray(R.array.consumables_categories)
        }

        for (i in 1..100) {
            calendar.add(Calendar.DAY_OF_YEAR, -10)
            val newItemCost = random.nextDouble(100.0, 20000.0).toFloat()
            val newWorkCost = random.nextDouble(1000.0, 15000.0).toFloat()

            val newCategory = categories.random()

            db.maintenanceDao().insertItem(
                MaintenanceItemEntity(
                    carId = carId,
                    date = Date(calendar.timeInMillis),
                    mileage = startMileage - i * 40,
                    category = newCategory,
                    subcategory = null,
                    title = "Обслуживание №$i",
                    brand = if (i % 3 == 0) "Bosch" else null,
                    description = "Регулярное ТО",
                    itemCost = newItemCost,
                    workCost = if (i % 10 == 0) 0f else newWorkCost,
                    totalCost = if (i % 10 != 0) newWorkCost + newItemCost else newItemCost
                )
            )
        }
    }

    private suspend fun generateRepairItems(carId: Int, startMileage: Int) {
        val calendar = Calendar.getInstance()
        val categories = withContext(Dispatchers.Main) {
            context.resources.getStringArray(R.array.parts_categories)
        }


        for (i in 1..100) {
            calendar.add(Calendar.DAY_OF_YEAR, -10)
            val newItemCost = random.nextDouble(100.0, 50000.0).toFloat()
            val newWorkCost = random.nextDouble(1000.0, 25000.0).toFloat()

            val newCategory = categories.random()

            db.repairDao().insertItem(
                RepairItemEntity(
                    carId = carId,
                    date = Date(calendar.timeInMillis),
                    mileage = startMileage + - i * 60,
                    isRepair = i % 2 == 0,
                    category = newCategory,
                    subcategory = null,
                    title = "Ремонт №$i",
                    brand = if (i % 4 == 0) "Mitsubishi" else null,
                    description = "Неисправность узла",
                    itemCost = newItemCost,
                    workCost = if (i % 10 == 0) 0f else newWorkCost,
                    totalCost = if (i % 10 != 0) newWorkCost + newItemCost else newItemCost
                )
            )
        }
    }
}