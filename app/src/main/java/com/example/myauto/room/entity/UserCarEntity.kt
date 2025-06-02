package com.example.myauto.room.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user_cars",
    indices = [
        Index(value = ["isSelected"], name = "idx_user_car_selected")
    ]
)
data class UserCarEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val brand: String,
    val model: String,
    val year: Int,
    val carBodyType: Int,
    val mileage: Int = 0,
    val isSelected: Boolean = false
)