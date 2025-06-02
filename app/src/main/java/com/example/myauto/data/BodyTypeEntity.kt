package com.example.myauto.data

import android.content.Context
import com.example.myauto.room.entity.CarInfoEntity

data class BodyTypeEntity(
    override val id: String,
    override val name: String
) : CarInfoEntity {
    companion object {
        fun fromBodyType(bodyType: BodyTypeData, context: Context) = BodyTypeEntity(
            id = bodyType.ordinal.toString(),
            name = bodyType.getLocalName(context)
        )
    }
}