package com.example.myauto.data

import com.example.myauto.room.entity.CarInfoEntity

data class YearEntity(
    override val id: String,
    override val name: String
) : CarInfoEntity {
    constructor(year: Int) : this(
        id = year.toString(),
        name = year.toString()
    )

    val year: Int get() = id.toInt()
}
