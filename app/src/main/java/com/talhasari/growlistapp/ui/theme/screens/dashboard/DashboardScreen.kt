package com.talhasari.growlistapp.ui.theme.screens.dashboard

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.talhasari.growlistapp.data.local.db.entity.Plant
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
            TopAppBar(
                title = { Text("Benim Bitkilerim") },
                // Üst barın arka planını da tema rengiyle uyumlu hale getirelim
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.AddPlant.route) }) {
                Icon(Icons.Filled.Add, contentDescription = "Yeni Bitki Ekle")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (uiState.plantsToWater.isNotEmpty()) {
                SummaryCard(plantsToWater = uiState.plantsToWater)
            }

            if (uiState.allPlants.isEmpty() && !uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Henüz bir bitki eklemedin.\n'+' butonuyla ilk bitkini ekle!",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.allPlants) { plant ->
                        PlantGridItem(plant = plant, navController = navController)
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryCard(plantsToWater: List<Plant>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Bugün ${plantsToWater.size} bitkinin sulanması gerekiyor",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            plantsToWater.forEach { plant ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = "Sulama ikonu",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = plant.name, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }
}

@Composable
fun PlantGridItem(plant: Plant, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(Screen.PlantDetail.createRoute(plant.id)) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (plant.imageUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = Uri.parse(plant.imageUrl)),
                        contentDescription = plant.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Yard,
                            contentDescription = "Varsayılan Bitki İkonu",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        Text(
            text = plant.name,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 4.dp),
            textAlign = TextAlign.Center
        )
    }
}