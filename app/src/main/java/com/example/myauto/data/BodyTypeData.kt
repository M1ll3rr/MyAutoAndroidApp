package com.example.myauto.data

import android.content.Context
import com.example.myauto.R

enum class BodyTypeData(private val nameId: Int, val imageId: Int) {
    SEDAN(R.string.sedan, R.drawable.car_sedan),
    HATCHBACK(R.string.hatchback, R.drawable.car_hatchback),
    LIFTBACK(R.string.liftback, R.drawable.car_liftback),
    CROSSOVER(R.string.crossover, R.drawable.car_crossover),
    SUV(R.string.suv, R.drawable.car_suv),
    UNIVERSAL(R.string.universal, R.drawable.car_universal),
    MINIVAN(R.string.minivan, R.drawable.car_minivan),
    PICKUP(R.string.pickup, R.drawable.car_pickup),
    COUPE(R.string.coupe, R.drawable.car_coupe),
    CABRIOLET(R.string.cabriolet, R.drawable.car_cabriolet);

    fun getLocalName(context: Context) = context.getString(nameId)
    fun toEntity(context: Context) = BodyTypeEntity(
        id = this.ordinal.toString(),
        name = this.getLocalName(context)
    )

    companion object {
        fun fromEntity(entity: BodyTypeEntity) = entries[entity.id.toInt()]
    }
}