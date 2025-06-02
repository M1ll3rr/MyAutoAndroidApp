package com.example.myauto.room.entity

interface MaintenanceRepairItem: UnifiedItemEntity {
    val category: String
    val title: String?
    val brand: String?
}