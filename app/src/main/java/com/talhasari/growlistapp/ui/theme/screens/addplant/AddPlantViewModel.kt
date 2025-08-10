package com.talhasari.growlistapp.ui.theme.screens.addplant

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.talhasari.growlistapp.data.local.db.PlantDatabase
import com.talhasari.growlistapp.data.local.db.entity.Plant
import com.talhasari.growlistapp.data.remote.PlantType
import com.talhasari.growlistapp.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddPlantUiState(
    val allPlantTypes: List<PlantType> = emptyList(),
    val filteredPlantTypes: List<PlantType> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val userMessage: String? = null
)

class AddPlantViewModel(application: Application) : AndroidViewModel(application) {

    private val plantRepository: PlantRepository

    private val _uiState = MutableStateFlow(AddPlantUiState())
    val uiState: StateFlow<AddPlantUiState> = _uiState.asStateFlow()

    init {
        val plantDao = PlantDatabase.getDatabase(application).plantDao()
        plantRepository = PlantRepository(plantDao)
        fetchPlantTypes()
    }

    private fun fetchPlantTypes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val types = plantRepository.getPlantTypes()
            _uiState.update { it.copy(allPlantTypes = types, filteredPlantTypes = types, isLoading = false) }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { currentState ->
            val filteredList = if (query.isBlank()) {
                currentState.allPlantTypes
            } else {
                currentState.allPlantTypes.filter { plantType ->
                    plantType.name.contains(query, ignoreCase = true)
                }
            }
            currentState.copy(searchQuery = query, filteredPlantTypes = filteredList)
        }
    }

    fun savePlant(name: String, type: String, location: String, imageUrl: String?) {
        if (name.isBlank() || type.isBlank() || location.isBlank()) {
            _uiState.update { it.copy(userMessage = "Lütfen tüm alanları doldurun.") }
            return
        }

        viewModelScope.launch {
            val selectedPlantType = uiState.value.allPlantTypes.find { it.name == type }
            val interval = selectedPlantType?.wateringIntervalDays ?: 7

            val newPlant = Plant(
                name = name,
                type = type,
                location = location,
                acquisitionDate = System.currentTimeMillis(),
                imageUrl = imageUrl,
                wateringIntervalDays = interval
            )
            plantRepository.insertLocalPlant(newPlant)
            _uiState.update { it.copy(userMessage = "Bitki başarıyla kaydedildi!") }
        }
    }

    fun userMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }
}