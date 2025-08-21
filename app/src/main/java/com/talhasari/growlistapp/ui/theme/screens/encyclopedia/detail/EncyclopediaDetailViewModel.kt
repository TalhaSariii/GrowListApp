package com.talhasari.growlistapp.ui.theme.screens.encyclopedia.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.talhasari.growlistapp.data.remote.PlantType
import com.talhasari.growlistapp.data.repository.AuthRepository
import com.talhasari.growlistapp.data.repository.PlantRepository
import com.talhasari.growlistapp.data.local.db.PlantDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class EncyclopediaDetailUiState(
    val plantType: PlantType? = null,
    val isInWishlist: Boolean = false,
    val isLoading: Boolean = true
)

class EncyclopediaDetailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val plantRepository: PlantRepository
    private val authRepository: AuthRepository
    private val plantTypeId: String = checkNotNull(savedStateHandle["plantTypeId"])
    private val currentUserId: String?

    val uiState: StateFlow<EncyclopediaDetailUiState>

    init {
        val plantDao = PlantDatabase.getDatabase(application).plantDao()
        plantRepository = PlantRepository(plantDao, application)
        authRepository = AuthRepository(application)
        currentUserId = authRepository.currentUser?.uid

        val plantTypeFlow = kotlinx.coroutines.flow.flow { emit(plantRepository.getPlantTypeById(plantTypeId)) }
        val isInWishlistFlow = currentUserId?.let {
            plantRepository.isPlantInWishlist(it, plantTypeId)
        } ?: kotlinx.coroutines.flow.flowOf(false)

        uiState = combine(plantTypeFlow, isInWishlistFlow) { plantType, isInWishlist ->
            EncyclopediaDetailUiState(
                plantType = plantType,
                isInWishlist = isInWishlist,
                isLoading = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = EncyclopediaDetailUiState(isLoading = true)
        )
    }

    fun toggleWishlist() {
        viewModelScope.launch {
            currentUserId?.let { userId ->
                if (uiState.value.isInWishlist) {
                    plantRepository.removeFromWishlist(userId, plantTypeId)
                } else {
                    plantRepository.addToWishlist(userId, plantTypeId)
                }
            }
        }
    }
}