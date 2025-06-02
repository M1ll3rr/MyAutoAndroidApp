package com.example.myauto.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myauto.room.converters.DateConverter
import com.example.myauto.room.dao.CarInfoDao
import com.example.myauto.room.dao.FuelDao
import com.example.myauto.room.dao.InsuranceDao
import com.example.myauto.room.dao.MaintenanceDao
import com.example.myauto.room.dao.RepairDao
import com.example.myauto.room.dao.UserCarsDao
import com.example.myauto.room.entity.CarBrandInfoEntity
import com.example.myauto.room.entity.CarModelInfoEntity
import com.example.myauto.room.entity.FuelItemEntity
import com.example.myauto.room.entity.InsuranceEntity
import com.example.myauto.room.entity.MaintenanceItemEntity
import com.example.myauto.room.entity.RepairItemEntity
import com.example.myauto.room.entity.UserCarEntity

@Database(entities = [
    CarBrandInfoEntity::class,
    CarModelInfoEntity::class,
    FuelItemEntity::class,
    UserCarEntity::class,
    InsuranceEntity::class,
    MaintenanceItemEntity::class,
    RepairItemEntity::class
    ], version = 1)

@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun carInfoDao(): CarInfoDao
    abstract fun fuelDao(): FuelDao
    abstract fun userCarsDao(): UserCarsDao
    abstract fun insuranceDao(): InsuranceDao
    abstract fun maintenanceDao(): MaintenanceDao
    abstract fun repairDao(): RepairDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build().also { INSTANCE = it }
            }
    }
}
