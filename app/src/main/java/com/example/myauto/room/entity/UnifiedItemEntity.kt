package com.example.myauto.room.entity

import java.util.Date

interface UnifiedItemEntity {
    val id: Int
    val date: Date
    val totalCost: Float
    val mileage: Int?
}