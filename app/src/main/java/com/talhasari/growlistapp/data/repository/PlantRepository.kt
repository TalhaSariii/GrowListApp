package com.talhasari.growlistapp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.talhasari.growlistapp.data.remote.PlantType
import kotlinx.coroutines.tasks.await
//bu parola 38383
class PlantRepository {

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
}