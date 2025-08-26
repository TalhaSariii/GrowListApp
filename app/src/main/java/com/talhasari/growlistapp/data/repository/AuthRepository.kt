package com.talhasari.growlistapp.data.repository

import android.content.Context
import android.net.Uri
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.talhasari.growlistapp.R
import kotlinx.coroutines.tasks.await
import java.util.UUID

class AuthRepository(
    private val context: Context
) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance() // YENİ EKLENDİ

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


    suspend fun uploadProfileImage(userId: String, imageUri: Uri): Uri? {
        return try {
            val storageRef = storage.reference.child("profile_images/$userId/${UUID.randomUUID()}")
            storageRef.putFile(imageUri).await()
            storageRef.downloadUrl.await()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun updateUserProfile(displayName: String, photoUri: Uri?): Result<Unit> {
        return try {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .apply { photoUri?.let { setPhotoUri(it) } }
                .build()

            currentUser?.updateProfile(profileUpdates)?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserEmail(newEmail: String): Result<Unit> {
        return try {
            currentUser?.updateEmail(newEmail)?.await()
            Result.success(Unit)
        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}