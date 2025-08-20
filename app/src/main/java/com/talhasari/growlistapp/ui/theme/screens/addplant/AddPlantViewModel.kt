package com.talhasari.growlistapp.ui.theme.screens.addplant

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.talhasari.growlistapp.data.local.db.PlantDatabase
import com.talhasari.growlistapp.data.local.db.entity.Plant
import com.talhasari.growlistapp.data.remote.PlantType
import com.talhasari.growlistapp.data.repository.AuthRepository
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
    private val authRepository: AuthRepository

    private val _uiState = MutableStateFlow(AddPlantUiState())
    val uiState: StateFlow<AddPlantUiState> = _uiState.asStateFlow()

    init {
        val plantDao = PlantDatabase.getDatabase(application).plantDao()
        plantRepository = PlantRepository(plantDao, application)
        authRepository = AuthRepository(application)
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

                emptyList()
            } else {
                currentState.allPlantTypes.filter { plantType ->
                    plantType.name.contains(query, ignoreCase = true) ||
                            plantType.scientificName.contains(query, ignoreCase = true)
                }
            }
            currentState.copy(searchQuery = query, filteredPlantTypes = filteredList)
        }
    }

    fun savePlant(name: String, selectedPlantType: PlantType?, location: String, imageUrl: String?) {
        val currentUser = authRepository.currentUser
        if (currentUser == null) {
            _uiState.update { it.copy(userMessage = "Bitki eklemek için giriş yapmalısınız.") }
            return
        }

        if (name.isBlank() || location.isBlank()) {
            _uiState.update { it.copy(userMessage = "Lütfen bitki adını ve konumunu doldurun.") }
            return
        }

        if (selectedPlantType == null) {
            _uiState.update { it.copy(userMessage = "Lütfen geçerli bir bitki türü seçin.") }
            return
        }

        viewModelScope.launch {
            // Yeni bitkiyi oluştururken seçilen türün tüm verilerini kopyala
            val newPlant = Plant(
                name = name,
                location = location,
                imageUrl = imageUrl,
                userId = currentUser.uid,
                acquisitionDate = System.currentTimeMillis(),
                lastWateredDate = null, // Yeni eklenen bitki henüz sulanmadı

                // --- Seçilen PlantType'dan gelen özellikler ---
                type = selectedPlantType.name,
                scientificName = selectedPlantType.scientificName,
                generalInfo = selectedPlantType.generalInfo,
                wateringIntervalDays = selectedPlantType.wateringIntervalDays,
                lightRequirement = selectedPlantType.lightRequirement,
                humidityRequirement = selectedPlantType.humidityRequirement,
                temperatureRange = selectedPlantType.temperatureRange,
                difficultyLevel = selectedPlantType.difficultyLevel,
                fertilizationFrequency = selectedPlantType.fertilizationFrequency,
                pruningFrequency = selectedPlantType.pruningFrequency,
                repottingFrequency = selectedPlantType.repottingFrequency
            )
            plantRepository.insertLocalPlant(newPlant)
            _uiState.update { it.copy(userMessage = "Bitki başarıyla kaydedildi!") }
        }
    }

    fun userMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }
}