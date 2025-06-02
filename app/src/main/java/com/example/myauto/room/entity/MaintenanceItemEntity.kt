package com.example.myauto.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "maintenance_items",
    foreignKeys = [
        ForeignKey(
            entity = UserCarEntity::class,
            parentColumns = ["id"],
            childColumns = ["carId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["carId"], name = "idx_maintenance_car_id"),
        Index(value = ["date"], name = "idx_maintenance_date"),
        Index(value = ["category"], name = "idx_maintenance_category"),
        Index(value = ["totalCost"], name = "idx_maintenance_total_cost")
    ]
)
data class MaintenanceItemEntity(
    @PrimaryKey(autoGenerate = true) override val id: Int = 0,
    val carId: Int,
    override val date: Date,
    override val mileage: Int,
    override val category: String,
    val subcategory: String?,
    override val title: String?,
    override val brand: String?,
    val description: String?,
    val itemCost: Float,
    val workCost: Float,
    override val totalCost: Float,
    val notificationMileage: Int? = null,
    val notificationDate: Date? = null
) : MaintenanceRepairItem, UnifiedItemEntity