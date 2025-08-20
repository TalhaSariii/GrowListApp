package com.talhasari.growlistapp.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.talhasari.growlistapp.data.local.db.entity.Plant
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: Plant)

    @Delete
    suspend fun deletePlant(plant: Plant)

    @Query("SELECT * FROM plants WHERE id = :plantId")
    suspend fun getPlantById(plantId: Int): Plant?


    @Query("SELECT * FROM plants WHERE userId = :userId ORDER BY name ASC")
    fun getAllPlants(userId: String): Flow<List<Plant>>
}