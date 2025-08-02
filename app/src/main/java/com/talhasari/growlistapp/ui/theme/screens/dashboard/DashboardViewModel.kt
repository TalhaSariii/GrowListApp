package com.talhasari.growlistapp.ui.theme.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.talhasari.growlistapp.data.remote.PlantType
import com.talhasari.growlistapp.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed interface PlantTypesUiState {
    object Loading : PlantTypesUiState
    data class Success(val plantTypes: List<PlantType>) : PlantTypesUiState
    data class Error(val message: String) : PlantTypesUiState
}

class DashboardViewModel : ViewModel() {


    private val plantRepository = PlantRepository()


    private val _uiState = MutableStateFlow<PlantTypesUiState>(PlantTypesUiState.Loading)

    val uiState: StateFlow<PlantTypesUiState> = _uiState.asStateFlow()


    init {
        fetchPlantTypes()
    }


    private fun fetchPlantTypes() {

        viewModelScope.launch {
            _uiState.value = PlantTypesUiState.Loading
            try {
                val plantTypesFromRepo = plantRepository.getPlantTypes()
                _uiState.value = PlantTypesUiState.Success(plantTypesFromRepo)
            } catch (e: Exception) {
                _uiState.value = PlantTypesUiState.Error("Bitki türleri alınamadı.")
            }
        }
    }
}