package com.example.myauto.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myauto.room.entity.InsuranceEntity

@Dao
interface InsuranceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(policy: InsuranceEntity)

    @Query("SELECT documentUri FROM insurance_policies WHERE carId = :carId LIMIT 1")
    suspend fun getFileByCar(carId: Int): String?

    @Query("UPDATE insurance_policies SET documentUri = :uri WHERE carId = :carId")
    suspend fun updateFileUri(carId: Int, uri: String)

    @Query("DELETE FROM insurance_policies WHERE carId = :carId")
    suspend fun deleteFile(carId: Int)
}