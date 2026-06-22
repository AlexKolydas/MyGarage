package com.alexkolydas.mygarage.core.data.local

import androidx.room.*
import com.alexkolydas.mygarage.core.data.model.Vehicle
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles ORDER BY id ASC")
    fun getAll(): Flow<List<Vehicle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vehicle: Vehicle): Long

    @Update
    suspend fun update(vehicle: Vehicle)

    @Query("UPDATE vehicles SET photoUri = :uri WHERE id = :id")
    suspend fun updatePhoto(id: Long, uri: String)

    @Query("DELETE FROM vehicles WHERE id = :id")
    suspend fun deleteById(id: Long)
}
