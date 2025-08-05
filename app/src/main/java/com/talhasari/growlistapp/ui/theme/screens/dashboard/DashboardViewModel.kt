package com.talhasari.growlistapp.ui.theme.screens.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.talhasari.growlistapp.data.local.db.PlantDatabase
import com.talhasari.growlistapp.data.local.db.entity.Plant
import com.talhasari.growlistapp.data.remote.PlantType
import com.talhasari.growlistapp.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


data class DashboardUiState(
    val plantTypes: List<PlantType> = emptyList(),
    val localPlants: List<Plant> = emptyList(),
    val isLoading: Boolean = true
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val plantRepository: PlantRepository


    private val localPlantsFlow: StateFlow<List<Plant>>


    private val plantTypesFlow: StateFlow<List<PlantType>>


    val uiState: StateFlow<DashboardUiState>

    init {
        val plantDao = PlantDatabase.getDatabase(application).plantDao()
        plantRepository = PlantRepository(plantDao)


        localPlantsFlow = plantRepository.getAllLocalPlants()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


        val typesFlow = MutableStateFlow<List<PlantType>>(emptyList())
        viewModelScope.launch {
            typesFlow.value = plantRepository.getPlantTypes()
        }
        plantTypesFlow = typesFlow


        uiState = combine(localPlantsFlow, plantTypesFlow) { local, types ->
            DashboardUiState(
                plantTypes = types,
                localPlants = local,
                isLoading = false
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState(isLoading = true))
    }
}