package com.example.myauto.room.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "car_brands",
    indices = [
        Index(value = ["isPopular", "name"], name = "idx_brand_popular_name"),
        Index(value = ["name"], name = "idx_brand_name")
    ])
data class CarBrandInfoEntity(
    @PrimaryKey override val id: String,
    override val name: String,
    val isPopular: Boolean
) : CarInfoEntity
