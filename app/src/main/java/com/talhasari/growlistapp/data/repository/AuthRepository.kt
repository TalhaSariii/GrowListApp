package com.talhasari.growlistapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository {


    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()


    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser


    suspend fun signInWithGoogle(idToken: String): FirebaseUser? {
        return try {
            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).await().user
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}