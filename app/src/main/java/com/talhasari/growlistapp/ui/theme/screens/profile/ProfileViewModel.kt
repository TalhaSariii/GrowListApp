package com.talhasari.growlistapp.ui.theme.screens.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseUser
import com.talhasari.growlistapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


data class ProfileUiState(
    val currentUser: FirebaseUser? = null,
    val isSignedOut: Boolean = false
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(application)

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {

        val user = authRepository.currentUser
        _uiState.update { it.copy(currentUser = user) }
    }


    fun signOut() {
        authRepository.signOut()

        _uiState.update { it.copy(isSignedOut = true) }
    }
}