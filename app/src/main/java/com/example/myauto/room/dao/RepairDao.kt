package com.example.myauto.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myauto.data.statistic.CategoryCost
import com.example.myauto.data.statistic.DailyCost
import com.example.myauto.data.statistic.StatSummary
import com.example.myauto.room.entity.RepairItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RepairDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: RepairItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<RepairItemEntity>)

    @Update
    suspend fun updateItem(item: RepairItemEntity)

    @Query("DELETE FROM repair_items WHERE id = :itemId")
    suspend fun deleteItemById(itemId: Int)

    @Query("DELETE FROM repair_items")
    suspend fun deleteAll()

    @Query("SELECT * FROM repair_items")
    fun getAllItems(): Flow<List<RepairItemEntity>>

    @Query("""
    SELECT * FROM repair_items 
    WHERE carId = :carId 
    ORDER BY 
        CASE WHEN :sortOrder = 'DESC' THEN 
            CASE :sortType 
                WHEN 'DATE' THEN date 
                WHEN 'COST' THEN totalCost 
                WHEN 'CATEGORY' THEN category 
            END 
        END DESC,
        CASE WHEN :sortOrder = 'ASC' THEN 
            CASE :sortType 
                WHEN 'DATE' THEN date 
                WHEN 'COST' THEN totalCost 
                WHEN 'CATEGORY' THEN category 
            END 
        END ASC,
        CASE WHEN :sortOrder = 'DESC' THEN id END DESC,
        CASE WHEN :sortOrder = 'ASC' THEN id END ASC
""")
    fun getSortedItemsByCar(
        carId: Int,
        sortType: String,
        sortOrder: String
    ): Flow<List<RepairItemEntity>>

    @Query("""
        SELECT 
            date AS day,
            SUM(totalCost) AS total 
        FROM repair_items 
        WHERE carId = :carId 
            AND date BETWEEN :startDate AND :endDate 
        GROUP BY (date / 86400000)
        ORDER BY date ASC
    """)
    suspend fun getDailyCosts(carId: Int, startDate: Long, endDate: Long): List<DailyCost>

    @Query("""
        SELECT 
            category, 
            SUM(totalCost) AS total 
        FROM repair_items 
        WHERE carId = :carId 
            AND date BETWEEN :startDate AND :endDate 
        GROUP BY category
    """)
    suspend fun getCategoryCosts(carId: Int, startDate: Long, endDate: Long): List<CategoryCost>

    @Query("""
        SELECT 
            SUM(totalCost) AS totalCost, 
            COUNT(*) AS count, 
            AVG(totalCost) AS averageCost 
        FROM repair_items 
        WHERE carId = :carId 
            AND date BETWEEN :startDate AND :endDate
    """)
    suspend fun getSummary(carId: Int, startDate: Long, endDate: Long): StatSummary?
}