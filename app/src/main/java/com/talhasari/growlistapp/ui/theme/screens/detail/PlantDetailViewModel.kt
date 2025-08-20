package com.talhasari.growlistapp.ui.theme.screens.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.talhasari.growlistapp.data.local.db.PlantDatabase
import com.talhasari.growlistapp.data.local.db.entity.Plant
import com.talhasari.growlistapp.data.repository.PlantRepository
import com.talhasari.growlistapp.utils.formatTimeRemaining
import com.talhasari.growlistapp.utils.frequencyToDays
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlantDetailUiState(
    val plant: Plant? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val plantDeleted: Boolean = false,
    val isEditMode: Boolean = false,
    val userMessage: String? = null,
    val wateringTimeRemaining: String = "",
    val fertilizingTimeRemaining: String = ""
)

class PlantDetailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val plantRepository: PlantRepository
    private val plantId: Int = checkNotNull(savedStateHandle["plantId"])

    private val _uiState = MutableStateFlow(PlantDetailUiState())
    val uiState: StateFlow<PlantDetailUiState> = _uiState.asStateFlow()

    init {
        val plantDao = PlantDatabase.getDatabase(application).plantDao()
        plantRepository = PlantRepository(plantDao, application)
        fetchPlantDetails()
    }

    private fun fetchPlantDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            plantRepository.getPlantById(plantId)?.let { plant ->
                updateUiWithPlant(plant)
            } ?: _uiState.update { it.copy(error = "Bitki bulunamadı.", isLoading = false) }
        }
    }

    private fun updateUiWithPlant(plant: Plant) {
        val wateringTime = formatTimeRemaining(plant.lastWateredDate, plant.wateringIntervalDays)
        val fertilizingDays = frequencyToDays(plant.fertilizationFrequency)
        val fertilizingTime = formatTimeRemaining(plant.lastFertilizedDate, fertilizingDays)

        _uiState.update {
            it.copy(
                plant = plant,
                isLoading = false,
                wateringTimeRemaining = wateringTime,
                fertilizingTimeRemaining = fertilizingTime
            )
        }
    }

    fun waterPlant() {
        viewModelScope.launch {
            uiState.value.plant?.let { currentPlant ->
                val updatedPlant = currentPlant.copy(lastWateredDate = System.currentTimeMillis())
                plantRepository.insertLocalPlant(updatedPlant)
                updateUiWithPlant(updatedPlant) // UI'ı yeni verilerle güncelle
                _uiState.update { it.copy(userMessage = "Bitki sulandı!") }
            }
        }
    }

    fun fertilizePlant() {
        viewModelScope.launch {
            uiState.value.plant?.let { currentPlant ->
                val updatedPlant = currentPlant.copy(lastFertilizedDate = System.currentTimeMillis())
                plantRepository.insertLocalPlant(updatedPlant)
                updateUiWithPlant(updatedPlant) // UI'ı yeni verilerle güncelle
                _uiState.update { it.copy(userMessage = "Bitki gübrelendi!") }
            }
        }
    }

    fun toggleEditMode() {
        _uiState.update { it.copy(isEditMode = !it.isEditMode) }
    }

    fun updatePlant(newName: String, newLocation: String) {
        if (newName.isBlank() || newLocation.isBlank()) {
            _uiState.update { it.copy(userMessage = "İsim ve konum boş bırakılamaz.") }
            return
        }

        viewModelScope.launch {
            uiState.value.plant?.let { currentPlant ->
                val updatedPlant = currentPlant.copy(
                    name = newName,
                    location = newLocation
                )
                plantRepository.insertLocalPlant(updatedPlant)
                updateUiWithPlant(updatedPlant)
                _uiState.update {
                    it.copy(
                        isEditMode = false,
                        userMessage = "Bitki güncellendi!"
                    )
                }
            }
        }
    }

    fun deletePlant() {
        viewModelScope.launch {
            uiState.value.plant?.let { plantToDelete ->
                plantRepository.deleteLocalPlant(plantToDelete)
                _uiState.update { it.copy(plantDeleted = true) }
            }
        }
    }

    fun userMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }
}