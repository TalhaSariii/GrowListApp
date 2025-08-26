package com.talhasari.growlistapp.ui.theme.screens.profile

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.talhasari.growlistapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val currentUser: FirebaseUser? = null,
    val isSignedOut: Boolean = false,
    val isLoading: Boolean = false,
    val userMessage: String? = null
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(application)

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {

        _uiState.update { it.copy(currentUser = authRepository.currentUser) }
    }

    fun onDisplayNameChange(newName: String) {
        if (newName.isBlank()) {
            _uiState.update { it.copy(userMessage = "İsim boş olamaz.") }
            return
        }
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = authRepository.updateUserProfile(displayName = newName, photoUri = null)
            if (result.isSuccess) {
                _uiState.update { it.copy(userMessage = "Adın başarıyla güncellendi.", currentUser = authRepository.currentUser) }
            } else {
                _uiState.update { it.copy(userMessage = "Hata: ${result.exceptionOrNull()?.message}") }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onEmailChange(newEmail: String) {
        if (newEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            _uiState.update { it.copy(userMessage = "Geçerli bir e-posta adresi girin.") }
            return
        }
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = authRepository.updateUserEmail(newEmail)
            if (result.isSuccess) {
                _uiState.update { it.copy(userMessage = "E-posta başarıyla güncellendi.", currentUser = authRepository.currentUser) }
            } else {
                _uiState.update { it.copy(userMessage = "Hata: ${result.exceptionOrNull()?.message}. Bu işlem için yeniden giriş yapmanız gerekebilir.") }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onProfilePictureChange(imageUri: Uri) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val downloadUrl = authRepository.uploadProfileImage(
                userId = authRepository.currentUser!!.uid,
                imageUri = imageUri
            )

            if (downloadUrl != null) {
                val result = authRepository.updateUserProfile(
                    displayName = authRepository.currentUser!!.displayName!!,
                    photoUri = downloadUrl
                )
                if (result.isSuccess) {
                    _uiState.update { it.copy(userMessage = "Profil resmi güncellendi.", currentUser = authRepository.currentUser) }
                } else {
                    _uiState.update { it.copy(userMessage = "Hata: Resim güncellenemedi.") }
                }
            } else {
                _uiState.update { it.copy(userMessage = "Hata: Resim yüklenemedi.") }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _uiState.update { it.copy(isSignedOut = true) }
    }

    fun userMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }
}