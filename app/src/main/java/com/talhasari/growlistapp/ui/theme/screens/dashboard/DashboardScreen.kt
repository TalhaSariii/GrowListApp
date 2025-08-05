package com.talhasari.growlistapp.ui.theme.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.talhasari.growlistapp.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    dashboardViewModel: DashboardViewModel = viewModel()
) {
    val uiState by dashboardViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("GrowList") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddPlant.route) }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Yeni Bitki Ekle")
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                item {
                    Text(text = "Benim Bitkilerim", style = MaterialTheme.typography.titleLarge)
                }
                if (uiState.localPlants.isEmpty()) {
                    item {
                        Text(text = "Henüz bir bitki eklemedin. '+' butonuyla ilk bitkini ekle!")
                    }
                } else {
                    items(uiState.localPlants) { plant ->

                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp)) {
                                Text(text = plant.name, style = MaterialTheme.typography.titleMedium)
                                Text(text = "${plant.type} - ${plant.location}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }


                item {
                    Text(text = "Bitki Ansiklopedisi", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 24.dp))
                }
                items(uiState.plantTypes) { plantType ->
                    PlantTypeCard(plantType = plantType) {

                    }
                }
            }
        }
    }
}