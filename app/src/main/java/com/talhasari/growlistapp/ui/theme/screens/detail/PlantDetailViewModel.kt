package com.talhasari.growlistapp.ui.theme.screens.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.talhasari.growlistapp.data.local.db.PlantDatabase
import com.talhasari.growlistapp.data.local.db.entity.Plant
import com.talhasari.growlistapp.data.repository.PlantRepository
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
    val userMessage: String? = null
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
        plantRepository = PlantRepository(plantDao)
        fetchPlantDetails()
    }

    private fun fetchPlantDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val plant = plantRepository.getPlantById(plantId)
            if (plant != null) {
                _uiState.update { it.copy(plant = plant, isLoading = false) }
            } else {
                _uiState.update { it.copy(error = "Bitki bulunamadı.", isLoading = false) }
            }
        }
    }


    fun waterPlant() {
        viewModelScope.launch {
            uiState.value.plant?.let { currentPlant ->

                val updatedPlant = currentPlant.copy(
                    lastWateredDate = System.currentTimeMillis()
                )

                plantRepository.insertLocalPlant(updatedPlant)


                _uiState.update { it.copy(plant = updatedPlant, userMessage = "Bitki sulandı!") }
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
                _uiState.update {
                    it.copy(
                        plant = updatedPlant,
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