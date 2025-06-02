package com.example.myauto.room.dto

import com.example.myauto.room.entity.CarBrandInfoEntity

data class BrandJson(
    val id: String,
    val name: String,
    val popular: Boolean,
    val models: List<ModelJson>
) {
    fun toEntity() = CarBrandInfoEntity(
        id = id,
        name = name,
        isPopular = popular
    )
}

