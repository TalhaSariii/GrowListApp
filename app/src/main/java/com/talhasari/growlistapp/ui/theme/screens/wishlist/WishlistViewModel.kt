package com.talhasari.growlistapp.ui.theme.screens.wishlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.talhasari.growlistapp.data.remote.PlantType
import com.talhasari.growlistapp.data.repository.AuthRepository
import com.talhasari.growlistapp.data.repository.PlantRepository
import com.talhasari.growlistapp.data.local.db.PlantDatabase
import kotlinx.coroutines.flow.*

data class WishlistUiState(
    val wishlist: List<PlantType> = emptyList(),
    val isLoading: Boolean = true
)

class WishlistViewModel(application: Application) : AndroidViewModel(application) {

    private val plantRepository: PlantRepository
    private val authRepository: AuthRepository

    val uiState: StateFlow<WishlistUiState>

    init {
        val plantDao = PlantDatabase.getDatabase(application).plantDao()
        plantRepository = PlantRepository(plantDao, application)
        authRepository = AuthRepository(application)
        val currentUserId = authRepository.currentUser?.uid

        val wishlistFlow = currentUserId?.let { userId ->
            plantRepository.getWishlist(userId)
        } ?: flowOf(emptyList())

        uiState = wishlistFlow
            .map { WishlistUiState(wishlist = it, isLoading = false) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = WishlistUiState(isLoading = true)
            )
    }
}