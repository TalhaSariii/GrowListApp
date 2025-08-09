package com.talhasari.growlistapp.ui.theme.screens.detail

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.talhasari.growlistapp.data.local.db.entity.Plant
import kotlinx.coroutines.launch
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

    LaunchedEffect(key1 = uiState.userMessage) {
        uiState.userMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
                plantDetailViewModel.userMessageShown()
            }
        }
    }

    LaunchedEffect(key1 = uiState.plantDeleted) {
        if (uiState.plantDeleted) {
            navController.popBackStack()
        }
    }

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
    val dateFormatter = remember { SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault()) }
    val acquisitionDateFormatted = dateFormatter.format(Date(plant.acquisitionDate))
    val lastWateredDateFormatted = plant.lastWateredDate?.let {
        dateFormatter.format(Date(it))
    } ?: "Hiç sulanmadı"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(16.dp)),
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
                Icon(Icons.Default.Yard, "Varsayılan Bitki İkonu", modifier = Modifier.size(100.dp), tint = Color.LightGray)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onWaterPlantClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.WaterDrop, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Şimdi Sula")
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isEditMode) {

        } else {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                DetailRow(label = "Bitki Adı", value = plant.name)
                DetailRow(label = "Türü", value = plant.type)
                DetailRow(label = "Konumu", value = plant.location)
                DetailRow(label = "Edinme Tarihi", value = acquisitionDateFormatted)
                DetailRow(label = "Son Sulanma Tarihi", value = lastWateredDateFormatted)
            }
        }
    }
}


@Composable
fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}