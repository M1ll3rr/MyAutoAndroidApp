package com.example.myauto.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "car_models",
    foreignKeys = [
        ForeignKey(
            entity = CarBrandInfoEntity::class,
            parentColumns = ["id"],
            childColumns = ["brandId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["brandId", "name"], name = "idx_model_brand_name")
    ]
)
data class CarModelInfoEntity(
    @PrimaryKey override val id: String,
    val brandId: String,
    override val name: String,
    val yearFrom: Int,
    val yearTo: Int
) : CarInfoEntity
