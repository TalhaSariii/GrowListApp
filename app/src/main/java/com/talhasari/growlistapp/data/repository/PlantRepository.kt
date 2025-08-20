package com.talhasari.growlistapp.data.repository

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.talhasari.growlistapp.data.local.db.PlantDao
import com.talhasari.growlistapp.data.local.db.entity.Plant
import com.talhasari.growlistapp.data.remote.PlantType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class PlantRepository(
    private val plantDao: PlantDao,
    application: Application
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


    fun getAllLocalPlants(userId: String): Flow<List<Plant>> {
        return plantDao.getAllPlants(userId)
    }


    suspend fun insertLocalPlant(plant: Plant) {
        plantDao.insertPlant(plant)
    }


    suspend fun deleteLocalPlant(plant: Plant) {
        plantDao.deletePlant(plant)
    }

    suspend fun getPlantById(plantId: Int): Plant? {
        return plantDao.getPlantById(plantId)
    }
}