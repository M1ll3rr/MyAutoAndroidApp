package com.example.myauto.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myauto.room.entity.UserCarEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserCarsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserCar(car: UserCarEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cars: List<UserCarEntity>)

    @Query("SELECT * FROM user_cars ORDER BY id ASC")
    fun getAllUserCars(): Flow<List<UserCarEntity>>

    @Query("DELETE FROM user_cars WHERE id = :carId")
    suspend fun deleteUserCar(carId: Int)

    @Query("DELETE FROM user_cars")
    suspend fun deleteAll()

    @Query("UPDATE user_cars SET isSelected = 0")
    suspend fun clearAllSelections()

    @Query("UPDATE user_cars SET mileage = :newMileage WHERE id = :carId")
    suspend fun updateMileage(carId: Int, newMileage: Int)

    @Query("UPDATE user_cars SET isSelected = 1 WHERE id = :carId")
    suspend fun setSelectedCar(carId: Int)

    @Query("SELECT * FROM user_cars WHERE isSelected = 1 LIMIT 1")
    suspend fun getSelectedCar(): UserCarEntity?
}