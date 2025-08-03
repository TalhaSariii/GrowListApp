package com.talhasari.growlistapp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.talhasari.growlistapp.data.local.db.PlantDao
import com.talhasari.growlistapp.data.local.db.entity.Plant
import com.talhasari.growlistapp.data.remote.PlantType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class PlantRepository(
    private val plantDao: PlantDao
) {


    private val firestore = FirebaseFirestore.getInstance()


    suspend fun getPlantTypes(): List<PlantType> {
        return try {
            val snapshot = firestore.collection("plant_types").get().await()
            snapshot.toObjects(PlantType::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }




    fun getAllLocalPlants(): Flow<List<Plant>> {
        return plantDao.getAllPlants()
    }


    suspend fun insertLocalPlant(plant: Plant) {
        plantDao.insertPlant(plant)
    }


    suspend fun deleteLocalPlant(plant: Plant) {
        plantDao.deletePlant(plant)
    }
}