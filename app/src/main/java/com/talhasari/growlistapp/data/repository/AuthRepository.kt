package com.talhasari.growlistapp.data.repository

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.talhasari.growlistapp.R
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val context: Context
) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    private val googleSignInClient = GoogleSignIn.getClient(context, gso)

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


    suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthResult? {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult? {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    fun signOut() {
        firebaseAuth.signOut()
        googleSignInClient.signOut()
    }
}