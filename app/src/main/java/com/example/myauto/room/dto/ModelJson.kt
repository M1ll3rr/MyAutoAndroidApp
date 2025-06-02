package com.example.myauto.room.dto

import com.example.myauto.room.entity.CarModelInfoEntity
import com.google.gson.annotations.SerializedName

data class ModelJson(
    val id: String,
    val name: String,
    @SerializedName("year-from") val yearFrom: Int?,
    @SerializedName("year-to") val yearTo: Int?
) {
    fun toEntity(brandId: String) = CarModelInfoEntity(
        id = id,
        brandId = brandId,
        name = name,
        yearFrom = yearFrom ?: 2000,
        yearTo = yearTo ?: 2025
    )
}
