package com.example.myauto.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myauto.data.statistic.DailyFuelCost
import com.example.myauto.data.statistic.StatFuelSummary
import com.example.myauto.room.entity.FuelItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FuelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(entry: FuelItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FuelItemEntity>)

    @Query("DELETE FROM fuel_items WHERE id = :itemId")
    suspend fun deleteItemById(itemId: Int)

    @Query("DELETE FROM fuel_items")
    suspend fun deleteAll()

    @Update
    suspend fun updateItem(item: FuelItemEntity)

    @Query("SELECT * FROM fuel_items")
    fun getAllItems(): Flow<List<FuelItemEntity>>

    @Query("""
    SELECT * FROM fuel_items 
    WHERE carId = :carId 
    ORDER BY 
        CASE WHEN :sortOrder = 'DESC' THEN 
            CASE :sortType 
                WHEN 'DATE' THEN date 
                WHEN 'COST' THEN totalCost 
                WHEN 'VOLUME' THEN volume 
            END 
        END DESC,
        CASE WHEN :sortOrder = 'ASC' THEN 
            CASE :sortType 
                WHEN 'DATE' THEN date 
                WHEN 'COST' THEN totalCost 
                WHEN 'VOLUME' THEN volume 
            END 
        END ASC,
        CASE WHEN :sortOrder = 'DESC' THEN id END DESC,
        CASE WHEN :sortOrder = 'ASC' THEN id END ASC
""")
    fun getSortedItemsByCar(
        carId: Int,
        sortType: String,
        sortOrder: String
    ): Flow<List<FuelItemEntity>>


    @Query("""
        SELECT 
            date AS day,
            SUM(totalCost) as cost,
            SUM(volume) as volume
        FROM fuel_items 
        WHERE carId = :carId 
            AND date BETWEEN :start AND :end
        GROUP BY (date / 86400000)
        ORDER BY date
    """)
    suspend fun getDailyCosts(carId: Int, start: Long, end: Long): List<DailyFuelCost>

    @Query("""
        SELECT 
            SUM(volume) as totalVolume,
            SUM(totalCost) as totalCost,
            AVG(totalCost/volume) as averageCost,
            SUM(discount) as totalDiscount
        FROM fuel_items 
        WHERE carId = :carId 
            AND date BETWEEN :start AND :end
    """)
    suspend fun getSummary(carId: Int, start: Long, end: Long): StatFuelSummary?
}