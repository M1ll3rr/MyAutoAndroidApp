package com.example.myauto.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myauto.room.entity.CarBrandInfoEntity
import com.example.myauto.room.entity.CarModelInfoEntity

@Dao
interface CarInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrands(brands: List<CarBrandInfoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModels(models: List<CarModelInfoEntity>)

    @Query("SELECT name FROM car_brands WHERE id = :brandId")
    suspend fun getBrandNameById(brandId: String): String

    @Query("SELECT * FROM car_brands ORDER BY isPopular DESC, name ASC")
    suspend fun getBrands(): List<CarBrandInfoEntity>

    @Query("SELECT * FROM car_models WHERE brandId = :brandId ORDER BY name ASC")
    suspend fun getModelsByBrand(brandId: String): List<CarModelInfoEntity>
}
