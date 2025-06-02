package com.example.myauto.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "fuel_items",
    foreignKeys = [
        ForeignKey(
            entity = UserCarEntity::class,
            parentColumns = ["id"],
            childColumns = ["carId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["carId"], name = "idx_fuel_car_id"),
        Index(value = ["date"], name = "idx_fuel_date"),
        Index(value = ["totalCost"], name = "idx_fuel_total_cost"),
        Index(value = ["volume"], name = "idx_fuel_volume")
    ]
)
data class FuelItemEntity(
    @PrimaryKey(autoGenerate = true) override val id: Int = 0,
    val carId: Int,
    override val date: Date,
    override val mileage: Int?,
    val fuelType: String,
    val volume: Float,
    override val totalCost: Float,
    val fuelPrice: Float,
    val discount: Float
): UnifiedItemEntity
