package com.talhasari.growlistapp.ui.theme.screens.encyclopedia

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.talhasari.growlistapp.data.local.db.PlantDatabase
import com.talhasari.growlistapp.data.remote.PlantType
import com.talhasari.growlistapp.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


sealed interface EncyclopediaUiState {
    object Loading : EncyclopediaUiState
    data class Success(val plantTypes: List<PlantType>) : EncyclopediaUiState
    data class Error(val message: String) : EncyclopediaUiState
}

class EncyclopediaViewModel(application: Application) : AndroidViewModel(application) {

    private val plantRepository: PlantRepository

    private val _uiState = MutableStateFlow<EncyclopediaUiState>(EncyclopediaUiState.Loading)
    val uiState: StateFlow<EncyclopediaUiState> = _uiState.asStateFlow()

    init {

        val plantDao = PlantDatabase.getDatabase(application).plantDao()
        plantRepository = PlantRepository(plantDao, application)
        fetchPlantTypes()
    }

    private fun fetchPlantTypes() {
        viewModelScope.launch {
            _uiState.update { EncyclopediaUiState.Loading }
            try {
                val types = plantRepository.getPlantTypes()
                _uiState.update { EncyclopediaUiState.Success(types) }
            } catch (e: Exception) {
                _uiState.update { EncyclopediaUiState.Error("Bitkiler yüklenemedi.") }
            }
        }
    }
}