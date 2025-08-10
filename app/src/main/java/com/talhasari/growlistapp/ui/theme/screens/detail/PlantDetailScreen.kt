package com.talhasari.growlistapp.ui.theme.screens.detail


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.talhasari.growlistapp.data.local.db.entity.Plant
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailScreen(
    navController: NavController,
    plantDetailViewModel: PlantDetailViewModel = viewModel()
) {

    val uiState by plantDetailViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = uiState.userMessage) { /*...*/ }
    LaunchedEffect(key1 = uiState.plantDeleted) { /*...*/ }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(uiState.plant?.name ?: "Detaylar") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri")
                    }
                },
                actions = {
                    IconButton(onClick = { plantDetailViewModel.toggleEditMode() }) {
                        Icon(Icons.Default.Edit, "Bitkiyi Düzenle")
                    }
                    IconButton(onClick = { plantDetailViewModel.deletePlant() }) {
                        Icon(Icons.Default.Delete, "Bitkiyi Sil")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Text(text = uiState.error!!, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                }
                uiState.plant != null -> {
                    // PlantDetails'e ViewModel'i de gönderiyoruz
                    PlantDetails(
                        plant = uiState.plant!!,
                        isEditMode = uiState.isEditMode,
                        onUpdateClick = { newName, newLocation ->
                            plantDetailViewModel.updatePlant(newName, newLocation)
                        },
                        onWaterPlantClick = { plantDetailViewModel.waterPlant() }
                    )
                }
            }
        }
    }
}

@Composable
fun PlantDetails(
    plant: Plant,
    isEditMode: Boolean,
    onUpdateClick: (String, String) -> Unit,
    onWaterPlantClick: () -> Unit
) {
    val acquisitionDateFormatted = remember(plant.acquisitionDate) {
        SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(plant.acquisitionDate))
    }
    val lastWateredDateFormatted = remember(plant.lastWateredDate) {
        plant.lastWateredDate?.let {
            SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault()).format(Date(it))
        } ?: "Hiç sulanmadı"
    }


    var editedName by remember(plant.name) { mutableStateOf(plant.name) }
    var editedLocation by remember(plant.location) { mutableStateOf(plant.location) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp), // Dikey boşluk
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Spacer(modifier = Modifier.height(24.dp))


        Column(
            modifier = Modifier.padding(horizontal = 24.dp), // Yatay boşluk
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isEditMode) {

                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("Bitki Adı") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = editedLocation,
                    onValueChange = { editedLocation = it },
                    label = { Text("Konumu") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onUpdateClick(editedName, editedLocation) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Değişiklikleri Kaydet")
                }
            } else {

                InfoCard(icon = Icons.Default.Yard, label = "Türü", value = plant.type)
                InfoCard(icon = Icons.Default.LocationOn, label = "Konumu", value = plant.location)
                InfoCard(icon = Icons.Default.CalendarToday, label = "Edinme Tarihi", value = acquisitionDateFormatted)
                InfoCard(icon = Icons.Default.WaterDrop, label = "Son Sulanma", value = lastWateredDateFormatted)
            }
        }


        if (!isEditMode) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onWaterPlantClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Icon(Icons.Default.WaterDrop, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Şimdi Sula")
            }
        }
    }
}


@Composable
fun InfoCard(icon: ImageVector, label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}