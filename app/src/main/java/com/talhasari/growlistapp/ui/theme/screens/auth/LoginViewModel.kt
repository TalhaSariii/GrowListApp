package com.talhasari.growlistapp.ui.theme.screens.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.talhasari.growlistapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class LoginUiState(
    val isLoading: Boolean = false,
    val signInError: String? = null,
    val isSignInSuccessful: Boolean = false
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()


    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            val firebaseUser = authRepository.signInWithGoogle(idToken)
            _uiState.value = LoginUiState(
                isSignInSuccessful = firebaseUser != null,
                signInError = if (firebaseUser == null) "Giriş yapılamadı." else null,
                isLoading = false
            )
        }
    }


    fun errorShown() {
        _uiState.value = _uiState.value.copy(signInError = null)
    }
}