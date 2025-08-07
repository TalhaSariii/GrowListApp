package com.talhasari.growlistapp.ui.theme.screens.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.talhasari.growlistapp.data.local.db.PlantDatabase
import com.talhasari.growlistapp.data.local.db.entity.Plant
import com.talhasari.growlistapp.data.repository.PlantRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


data class DashboardUiState(
    val localPlants: List<Plant> = emptyList(),
    val isLoading: Boolean = true
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val plantRepository: PlantRepository


    val uiState: StateFlow<DashboardUiState>

    init {
        val plantDao = PlantDatabase.getDatabase(application).plantDao()
        plantRepository = PlantRepository(plantDao)

        // Repository'den gelen canlı bitki listesi akışını dinliyoruz
        // ve bunu doğrudan DashboardUiState'e dönüştürüyoruz.
        uiState = plantRepository.getAllLocalPlants()
            .map { plants -> DashboardUiState(localPlants = plants, isLoading = false) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = DashboardUiState(isLoading = true)
            )
    }
}