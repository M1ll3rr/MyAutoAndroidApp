package com.example.myauto.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "insurance_policies",
    foreignKeys = [
        ForeignKey(
            entity = UserCarEntity::class,
            parentColumns = ["id"],
            childColumns = ["carId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["carId"], name = "idx_insurance_car_id", unique = true)
    ]
)
data class InsuranceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val carId: Int,
    val documentUri: String
)