package com.talhasari.growlistapp.data.repository

import android.app.Application
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.talhasari.growlistapp.data.local.db.PlantDao
import com.talhasari.growlistapp.data.local.db.entity.Plant
import com.talhasari.growlistapp.data.remote.PlantType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PlantRepository(
    private val plantDao: PlantDao,
    application: Application
) {

    private val firestore = FirebaseFirestore.getInstance()
    private val plantTypesCollection = firestore.collection("plant_types")
    private val usersCollection = firestore.collection("users")



    suspend fun getPlantTypes(limit: Long, lastVisibleId: String? = null): QuerySnapshot {
        val query = plantTypesCollection.orderBy("id").limit(limit)
        return if (lastVisibleId == null) {
            query.get().await()
        } else {
            query.startAfter(lastVisibleId).get().await()
        }
    }


    suspend fun getPlantTypeById(plantTypeId: String): PlantType? {
        return try {
            plantTypesCollection.document(plantTypeId).get().await().toObject(PlantType::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }



    fun isPlantInWishlist(userId: String, plantTypeId: String): Flow<Boolean> = callbackFlow {
        val docRef = usersCollection.document(userId)
        val listener = docRef.addSnapshotListener { snapshot, _ ->
            val wishlist = snapshot?.get("wishlist") as? List<*>
            trySend(wishlist?.contains(plantTypeId) == true)
        }
        awaitClose { listener.remove() }
    }

    suspend fun addToWishlist(userId: String, plantTypeId: String) {
        usersCollection.document(userId).set(mapOf("wishlist" to FieldValue.arrayUnion(plantTypeId)), SetOptions.merge()).await()
    }

    suspend fun removeFromWishlist(userId: String, plantTypeId: String) {
        usersCollection.document(userId).update("wishlist", FieldValue.arrayRemove(plantTypeId)).await()
    }

    fun getWishlist(userId: String): Flow<List<PlantType>> = callbackFlow {
        val userDocRef = usersCollection.document(userId)
        val listener = userDocRef.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null || !snapshot.exists()) {
                trySend(emptyList())
                return@addSnapshotListener
            }

            val plantIds = snapshot.get("wishlist") as? List<String>
            if (plantIds.isNullOrEmpty()) {
                trySend(emptyList())
            } else {
                plantTypesCollection.whereIn("id", plantIds).get().addOnSuccessListener { plantSnapshots ->
                    trySend(plantSnapshots.toObjects(PlantType::class.java))
                }
            }
        }
        awaitClose { listener.remove() }
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