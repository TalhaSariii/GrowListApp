package com.talhasari.growlistapp.ui.theme.screens.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.talhasari.growlistapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class RegisterUiState(
    val isLoading: Boolean = false,
    val registrationError: String? = null,
    val isRegistrationSuccessful: Boolean = false
)

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(application)

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()


    fun createUserWithEmailAndPassword(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = RegisterUiState(registrationError = "E-posta ve şifre boş bırakılamaz.")
            return
        }


        viewModelScope.launch {
            _uiState.value = RegisterUiState(isLoading = true)
            val result = authRepository.createUserWithEmailAndPassword(email, password)
            _uiState.value = RegisterUiState(
                isRegistrationSuccessful = result != null,
                registrationError = if (result == null) "Kayıt başarısız oldu. E-posta kullanılıyor olabilir veya şifre zayıf." else null,
                isLoading = false
            )
        }
    }


    fun errorShown() {
        _uiState.value = _uiState.value.copy(registrationError = null)
    }
}