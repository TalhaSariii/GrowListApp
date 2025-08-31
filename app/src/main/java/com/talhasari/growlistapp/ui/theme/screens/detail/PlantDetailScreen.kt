package com.talhasari.growlistapp.ui.theme.screens.detail

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.talhasari.growlistapp.data.local.db.entity.Plant
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailScreen(
    navController: NavController,
    plantDetailViewModel: PlantDetailViewModel = viewModel()
) {
    val uiState by plantDetailViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = uiState.userMessage) {
        uiState.userMessage?.let {
            snackbarHostState.showSnackbar(it)
            plantDetailViewModel.userMessageShown()
        }
    }

    LaunchedEffect(key1 = uiState.plantDeleted) {
        if (uiState.plantDeleted) {
            navController.popBackStack()
        }
    }

    // DEĞİŞTİRİLDİ: Scaffold'un padding'i artık manuel olarak yönetiliyor.
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        // Bu Box, Scaffold'un verdiği tüm padding'leri yok sayar ve ekranı kaplar.
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                uiState.error != null -> Text(
                    text = uiState.error!!,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
                uiState.plant != null -> {
                    // Ana içeriğimiz artık alt navigasyon çubuğu için gerekli boşluğu alıyor.
                    PlantDetails(
                        uiState = uiState,
                        viewModel = plantDetailViewModel,
                        navController = navController,
                        modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                    )
                }
            }
        }
    }
}

@Composable
fun PlantDetails(
    uiState: PlantDetailUiState,
    viewModel: PlantDetailViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val plant = uiState.plant!!
    val scrollState = rememberScrollState()

    if (uiState.isEditDialogOpen) {
        EditPlantDialog(
            plant = plant,
            onDismiss = { viewModel.closeEditDialog() },
            onSave = { newName, newLocation ->
                viewModel.updatePlant(newName, newLocation)
            }
        )
    }

    // Dışarıdan gelen modifier (bottom padding içeren) buraya uygulanıyor.
    Column(modifier = modifier.verticalScroll(scrollState)) {
        HeaderWithActions(
            plant = plant,
            onBackClick = { navController.navigateUp() },
            onEditClick = { viewModel.openEditDialog() },
            onDeleteClick = { viewModel.deletePlant() }
        )
        InfoTabs(uiState = uiState, viewModel = viewModel)
    }
}

@Composable
fun HeaderWithActions(
    plant: Plant,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
    ) {
        AsyncImage(
            model = plant.imageUrl,
            contentDescription = plant.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent, Color.Black.copy(alpha = 0.8f))
                    )
                )
        )

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding() // Bu padding, butonu status bar'ın altına iter
                .padding(start = 16.dp, top = 16.dp)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Color.White)
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(end = 16.dp, top = 16.dp)
        ) {
            IconButton(
                onClick = { menuExpanded = true },
                modifier = Modifier.background(Color.Black.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(Icons.Default.MoreVert, contentDescription = "Ayarlar", tint = Color.White)
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Bitkiyi Düzenle") },
                    onClick = {
                        onEditClick()
                        menuExpanded = false
                    },
                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text("Bitkiyi Sil") },
                    onClick = {
                        onDeleteClick()
                        menuExpanded = false
                    },
                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) }
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = plant.name,
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = plant.scientificName,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}


@Composable
fun InfoTabs(uiState: PlantDetailUiState, viewModel: PlantDetailViewModel) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Açıklama", "Bakım")

    Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        Crossfade(targetState = selectedTabIndex, label = "tab-crossfade") { tabIndex ->
            when (tabIndex) {
                0 -> DescriptionTab(plant = uiState.plant!!)
                1 -> CareTab(uiState = uiState, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun DescriptionTab(plant: Plant) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        InfoDetailRow(icon = Icons.Default.Thermostat, label = "Sıcaklık", value = plant.temperatureRange)
        InfoDetailRow(icon = Icons.Default.WbSunny, label = "Işık İhtiyacı", value = plant.lightRequirement)
        InfoDetailRow(icon = Icons.Default.Opacity, label = "Nem İhtiyacı", value = plant.humidityRequirement)
        Divider()
        InfoDetailRow(icon = Icons.Default.Star, label = "Zorluk Seviyesi", value = plant.difficultyLevel)
        InfoDetailRow(icon = Icons.Default.WaterDrop, label = "Sulama Sıklığı", value = "${plant.wateringIntervalDays} günde bir")
        InfoDetailRow(icon = Icons.Default.FilterVintage, label = "Gübreleme", value = plant.fertilizationFrequency)
        InfoDetailRow(icon = Icons.Default.ContentCut, label = "Budama", value = plant.pruningFrequency)
        InfoDetailRow(icon = Icons.Default.Yard, label = "Saksı Değişimi", value = plant.repottingFrequency)

        Text(
            text = "Genel Bilgi",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = plant.generalInfo,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CareTab(uiState: PlantDetailUiState, viewModel: PlantDetailViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CareActionCard(
            title = "Sulama",
            timeRemaining = uiState.wateringTimeRemaining,
            onActionClick = { viewModel.waterPlant() },
            icon = Icons.Default.WaterDrop,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
        CareActionCard(
            title = "Gübreleme",
            timeRemaining = uiState.fertilizingTimeRemaining,
            onActionClick = { viewModel.fertilizePlant() },
            icon = Icons.Default.FilterVintage,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
fun InfoDetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EditPlantDialog(plant: Plant, onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var name by remember(plant.name) { mutableStateOf(plant.name) }
    var location by remember(plant.location) { mutableStateOf(plant.location) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Bitkiyi Düzenle") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Bitkinin Adı") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Konumu") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, location) },
                enabled = name.isNotBlank() && location.isNotBlank()
            ) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

@Composable
fun CareActionCard(
    title: String,
    timeRemaining: String,
    onActionClick: () -> Unit,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = timeRemaining,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
            Button(
                onClick = onActionClick,
                modifier = Modifier.size(60.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}