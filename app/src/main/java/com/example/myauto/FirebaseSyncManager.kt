package com.example.myauto

import com.example.myauto.room.entity.FuelItemEntity
import com.example.myauto.room.entity.MaintenanceItemEntity
import com.example.myauto.room.entity.RepairItemEntity
import com.example.myauto.room.entity.UserCarEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.util.Date

class FirebaseSyncManager(private val repository: AppRepository, private val auth: FirebaseAuth) {
    private val database = FirebaseDatabase.getInstance().reference

    suspend fun uploadAllData() {
        val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")

        val userCars = repository.getAllCars().first()
        database.child("users").child(userId).child("userCars").removeValue().await()
        userCars.forEach { car ->
            database.child("users").child(userId).child("userCars").child(car.id.toString())
                .setValue(car.toMap()).await()
        }

        val fuelItems = repository.getAllFuelItems().first()
        database.child("users").child(userId).child("fuelItems").removeValue().await()
        fuelItems.forEach { item ->
            database.child("users").child(userId).child("fuelItems").child(item.id.toString())
                .setValue(item.toMap()).await()
        }

        val maintenanceItems = repository.getAllMaintenanceItems().first()
        database.child("users").child(userId).child("maintenance").removeValue().await()
        maintenanceItems.forEach { item ->
            database.child("users").child(userId).child("maintenance").child(item.id.toString())
                .setValue(item.toMap()).await()
        }

        val repairItems = repository.getAllRepairItems().first()
        database.child("users").child(userId).child("repair").removeValue().await()
        repairItems.forEach { item ->
            database.child("users").child(userId).child("repair").child(item.id.toString())
                .setValue(item.toMap()).await()
        }
    }

    suspend fun downloadAllData() {
        val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")

        val userCarsSnapshot = database.child("users").child(userId).child("userCars").get().await()
        val userCars = userCarsSnapshot.children.mapNotNull { it.toUserCarEntity() }
        repository.deleteAllCars()
        repository.addAllCars(userCars)

        val fuelItemsSnapshot = database.child("users").child(userId).child("fuelItems").get().await()
        val fuelItems = fuelItemsSnapshot.children.mapNotNull { it.toFuelItemEntity() }
        repository.deleteAllFuelItems()
        repository.addAllFuelItems(fuelItems)

        val maintenanceSnapshot = database.child("users").child(userId).child("maintenance").get().await()
        val maintenanceItems = maintenanceSnapshot.children.mapNotNull { it.toMaintenanceEntity() }
        repository.deleteAllMaintenanceItems()
        repository.addAllMaintenanceItems(maintenanceItems)

        val repairSnapshot = database.child("users").child(userId).child("repair").get().await()
        val repairItems = repairSnapshot.children.mapNotNull { it.toRepairEntity() }
        repository.deleteAllRepairItems()
        repository.addAllRepairItems(repairItems)
    }


    private fun UserCarEntity.toMap() = mapOf(
        "brand" to brand,
        "model" to model,
        "year" to year,
        "carBodyType" to carBodyType,
        "mileage" to mileage,
        "isSelected" to isSelected
    )

    private fun FuelItemEntity.toMap() = mapOf(
        "carId" to carId,
        "date" to date.time,
        "mileage" to mileage,
        "fuelType" to fuelType,
        "volume" to volume,
        "cost" to totalCost,
        "fuelPrice" to fuelPrice,
        "discount" to discount
    )

    private fun MaintenanceItemEntity.toMap() = mapOf(
        "carId" to carId,
        "date" to date.time,
        "mileage" to mileage,
        "category" to category,
        "subcategory" to subcategory,
        "title" to title,
        "brand" to brand,
        "description" to description,
        "itemCost" to itemCost,
        "workCost" to workCost,
        "totalCost" to totalCost,
        "notificationMileage" to notificationMileage,
        "notificationDate" to notificationDate?.time
    )

    private fun RepairItemEntity.toMap() = mapOf(
        "carId" to carId,
        "date" to date.time,
        "mileage" to mileage,
        "isRepair" to isRepair,
        "category" to category,
        "subcategory" to subcategory,
        "title" to title,
        "brand" to brand,
        "description" to description,
        "itemCost" to itemCost,
        "workCost" to workCost,
        "totalCost" to totalCost
    )

    private fun DataSnapshot.toUserCarEntity(): UserCarEntity? {
        return try {
            UserCarEntity(
                id = key?.toIntOrNull() ?: return null,
                brand = child("brand").getValue(String::class.java) ?: "",
                model = child("model").getValue(String::class.java) ?: "",
                year = child("year").getValue(Int::class.java) ?: 0,
                carBodyType = child("carBodyType").getValue(Int::class.java) ?: 0,
                mileage = child("mileage").getValue(Int::class.java) ?: 0,
                isSelected = child("isSelected").getValue(Boolean::class.java) ?: false
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun DataSnapshot.toFuelItemEntity(): FuelItemEntity? {
        return try {
            FuelItemEntity(
                id = key?.toIntOrNull() ?: return null,
                carId = child("carId").getValue(Int::class.java) ?: -1,
                date = Date(child("date").getValue(Long::class.java) ?: 0),
                mileage = child("mileage").getValue(Int::class.java),
                fuelType = child("fuelType").getValue(String::class.java) ?: "",
                volume = child("volume").getValue(Float::class.java) ?: 0f,
                totalCost = child("cost").getValue(Float::class.java) ?: 0f,
                fuelPrice = child("fuelPrice").getValue(Float::class.java) ?: 0f,
                discount = child("discount").getValue(Float::class.java) ?: 0f
            )
        } catch (e: Exception) {
            null
        }
    }


    private fun DataSnapshot.toMaintenanceEntity(): MaintenanceItemEntity? {
        return try {
            MaintenanceItemEntity(
                id = key?.toIntOrNull() ?: return null,
                carId = child("carId").getValue(Int::class.java) ?: -1,
                date = Date(child("date").getValue(Long::class.java) ?: 0),
                mileage = child("mileage").getValue(Int::class.java) ?: 0,
                category = child("category").getValue(String::class.java) ?: "",
                subcategory = child("subcategory").getValue(String::class.java),
                title = child("title").getValue(String::class.java),
                brand = child("brand").getValue(String::class.java),
                description = child("description").getValue(String::class.java),
                itemCost = child("itemCost").getValue(Float::class.java) ?: 0f,
                workCost = child("workCost").getValue(Float::class.java) ?: 0f,
                totalCost = child("totalCost").getValue(Float::class.java) ?: 0f,
                notificationMileage = child("notificationMileage").getValue(Int::class.java),
                notificationDate = child("notificationDate").getValue(Long::class.java)?.let { Date(it) }
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun DataSnapshot.toRepairEntity(): RepairItemEntity? {
        return try {
            RepairItemEntity(
                id = key?.toIntOrNull() ?: return null,
                carId = child("carId").getValue(Int::class.java) ?: -1,
                date = Date(child("date").getValue(Long::class.java) ?: 0),
                mileage = child("mileage").getValue(Int::class.java) ?: 0,
                isRepair = child("isRepair").getValue(Boolean::class.java) ?: true,
                category = child("category").getValue(String::class.java) ?: "",
                subcategory = child("subcategory").getValue(String::class.java),
                title = child("title").getValue(String::class.java),
                brand = child("brand").getValue(String::class.java),
                description = child("description").getValue(String::class.java),
                itemCost = child("itemCost").getValue(Float::class.java) ?: 0f,
                workCost = child("workCost").getValue(Float::class.java) ?: 0f,
                totalCost = child("totalCost").getValue(Float::class.java) ?: 0f
            )
        } catch (e: Exception) {
            null
        }
    }
}