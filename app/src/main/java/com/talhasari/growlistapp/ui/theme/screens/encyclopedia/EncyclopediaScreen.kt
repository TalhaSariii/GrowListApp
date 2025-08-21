package com.talhasari.growlistapp.ui.theme.screens.encyclopedia

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.talhasari.growlistapp.navigation.Screen
import com.talhasari.growlistapp.ui.theme.screens.dashboard.PlantTypeCard

@Composable
fun EncyclopediaScreen(
    navController: NavController,
    encyclopediaViewModel: EncyclopediaViewModel = viewModel()
) {
    val uiState by encyclopediaViewModel.uiState.collectAsState()

    when (val state = uiState) {
        is EncyclopediaUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is EncyclopediaUiState.Success -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.plantTypes) { plantType ->

                    PlantTypeCard(plantType = plantType) {
                        navController.navigate(Screen.EncyclopediaDetail.createRoute(plantType.id))
                    }
                }
            }
        }
        is EncyclopediaUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = state.message, color = Color.Red)
            }
        }
    }
}