package com.talhasari.growlistapp.ui.theme.screens.encyclopedia

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.talhasari.growlistapp.data.local.db.PlantDatabase
import com.talhasari.growlistapp.data.remote.PlantType
import com.talhasari.growlistapp.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EncyclopediaUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val allPlants: List<PlantType> = emptyList(),
    val displayedPlants: List<PlantType> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null,
    val endReached: Boolean = false,
)

class EncyclopediaViewModel(application: Application) : AndroidViewModel(application) {

    private val plantRepository: PlantRepository
    private val _uiState = MutableStateFlow(EncyclopediaUiState())
    val uiState = _uiState.asStateFlow()

    private val pageSize = 15L
    init {
        val plantDao = PlantDatabase.getDatabase(application).plantDao()
        plantRepository = PlantRepository(plantDao, application)
        loadPlants()
    }

    fun loadPlants(isInitialLoad: Boolean = true) {
        if (_uiState.value.isLoading || _uiState.value.isLoadingMore || _uiState.value.endReached) return

        viewModelScope.launch {
            if (isInitialLoad) {
                _uiState.update { it.copy(isLoading = true) }
            } else {
                _uiState.update { it.copy(isLoadingMore = true) }
            }

            try {
                val lastVisibleId = if (isInitialLoad) null else _uiState.value.allPlants.lastOrNull()?.id
                val snapshot = plantRepository.getPlantTypes(pageSize, lastVisibleId)
                val newPlants = snapshot.toObjects(PlantType::class.java)

                val currentPlants = if (isInitialLoad) emptyList() else _uiState.value.allPlants
                val combinedList = currentPlants + newPlants

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        allPlants = combinedList,
                        displayedPlants = if (it.searchQuery.isEmpty()) combinedList else it.displayedPlants,
                        endReached = newPlants.size < pageSize
                    )
                }

                if (_uiState.value.searchQuery.isNotEmpty()) {
                    filterPlants(_uiState.value.searchQuery)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, isLoadingMore = false, error = "Bitkiler yüklenemedi: ${e.message}") }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        filterPlants(query)
    }

    private fun filterPlants(query: String) {
        val filtered = if (query.isBlank()) {
            _uiState.value.allPlants
        } else {
            _uiState.value.allPlants.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.scientificName.contains(query, ignoreCase = true)
            }
        }
        _uiState.update { it.copy(displayedPlants = filtered) }
    }
}