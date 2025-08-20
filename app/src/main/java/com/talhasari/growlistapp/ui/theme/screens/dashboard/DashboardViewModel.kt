package com.talhasari.growlistapp.ui.theme.screens.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.talhasari.growlistapp.data.local.db.PlantDatabase
import com.talhasari.growlistapp.data.local.db.entity.Plant
import com.talhasari.growlistapp.data.local.db.entity.needsWatering
import com.talhasari.growlistapp.data.repository.AuthRepository
import com.talhasari.growlistapp.data.repository.PlantRepository
import kotlinx.coroutines.flow.*

data class DashboardUiState(
    val allPlants: List<Plant> = emptyList(),
    val plantsToWater: List<Plant> = emptyList(),
    val isLoading: Boolean = true
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val plantRepository: PlantRepository
    private val authRepository = AuthRepository(application)
    val uiState: StateFlow<DashboardUiState>

    init {
        val plantDao = PlantDatabase.getDatabase(application).plantDao()
        plantRepository = PlantRepository(plantDao, application)

        val currentUserId = authRepository.currentUser?.uid


        val plantsFlow = currentUserId?.let { userId ->
            plantRepository.getAllLocalPlants(userId)
        } ?: flowOf(emptyList())

        uiState = plantsFlow
            .map { plants ->
                DashboardUiState(
                    allPlants = plants,
                    plantsToWater = plants.filter { it.needsWatering() },
                    isLoading = false
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = DashboardUiState(isLoading = true)
            )
    }
}