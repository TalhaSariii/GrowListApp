package com.talhasari.growlistapp.ui.theme.screens.encyclopedia.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.talhasari.growlistapp.data.remote.PlantType
import com.talhasari.growlistapp.ui.theme.screens.detail.InfoChip
import com.talhasari.growlistapp.ui.theme.screens.detail.InfoDetailRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncyclopediaDetailScreen(
    navController: NavController,
    viewModel: EncyclopediaDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.plantType?.name ?: "...") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    if (uiState.plantType != null) {
                        IconButton(onClick = { viewModel.toggleWishlist() }) {
                            Icon(
                                imageVector = if (uiState.isInWishlist) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Dilek Listesine Ekle",
                                tint = if (uiState.isInWishlist) MaterialTheme.colorScheme.error else LocalContentColor.current
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.plantType != null) {
                PlantTypeDetails(plantType = uiState.plantType!!)
            } else {
                Text("Bitki türü bulunamadı.")
            }
        }
    }
}

@Composable
fun PlantTypeDetails(plantType: PlantType, modifier: Modifier = Modifier) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        // GÜNCELLENDİ: Artık kendi Header'ı var.
        PlantTypeHeader(plantType = plantType)
        Description(plantType = plantType)
    }
}


@Composable
fun PlantTypeHeader(plantType: PlantType) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        AsyncImage(
            model = plantType.imageUrl,
            contentDescription = plantType.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                        startY = 300f
                    )
                )
        )
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = plantType.name,
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = plantType.scientificName,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun Description(plantType: PlantType) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            InfoChip(icon = Icons.Default.Thermostat, label = "Sıcaklık", value = plantType.temperatureRange)
            InfoChip(icon = Icons.Default.WbSunny, label = "Işık", value = plantType.lightRequirement)
            InfoChip(icon = Icons.Default.Opacity, label = "Nem", value = plantType.humidityRequirement)
        }
        Divider()
        InfoDetailRow(icon = Icons.Default.Star, label = "Zorluk", value = plantType.difficultyLevel)
        InfoDetailRow(icon = Icons.Default.WaterDrop, label = "Sulama Sıklığı", value = "${plantType.wateringIntervalDays} günde bir")
        InfoDetailRow(icon = Icons.Default.FilterVintage, label = "Gübreleme", value = plantType.fertilizationFrequency)
        InfoDetailRow(icon = Icons.Default.ContentCut, label = "Budama", value = plantType.pruningFrequency)
        InfoDetailRow(icon = Icons.Default.Yard, label = "Saksı Değişimi", value = plantType.repottingFrequency)
        Divider()
        Text(
            text = "Genel Bilgi",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = plantType.generalInfo,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}