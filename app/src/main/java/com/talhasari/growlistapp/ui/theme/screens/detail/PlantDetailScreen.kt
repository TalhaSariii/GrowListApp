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
    val scope = rememberCoroutineScope()

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
                    IconButton(onClick = { /* TODO: Edit Screen Navigation */ }) {
                        Icon(Icons.Default.Edit, "Bitkiyi Düzenle")
                    }
                    IconButton(onClick = { plantDetailViewModel.deletePlant() }) {
                        Icon(Icons.Default.Delete, "Bitkiyi Sil")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()) // Sadece üst padding'i uygula
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                uiState.error != null -> Text(
                    text = uiState.error!!,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
                uiState.plant != null -> {
                    PlantDetails(uiState, plantDetailViewModel)
                }
            }
        }
    }
}

@Composable
fun PlantDetails(
    uiState: PlantDetailUiState,
    viewModel: PlantDetailViewModel
) {
    val plant = uiState.plant!!
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.verticalScroll(scrollState)) {
        Header(plant = plant)
        InfoTabs(uiState = uiState, viewModel = viewModel)
    }
}

@Composable
fun Header(plant: Plant) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        AsyncImage(
            model = plant.imageUrl,
            contentDescription = plant.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Yazının okunabilirliği için gradient
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

    Column {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            InfoChip(icon = Icons.Default.Thermostat, label = "Sıcaklık", value = plant.temperatureRange)
            InfoChip(icon = Icons.Default.WbSunny, label = "Işık", value = plant.lightRequirement)
            InfoChip(icon = Icons.Default.Opacity, label = "Nem", value = plant.humidityRequirement)
        }
        Divider()
        InfoDetailRow(icon = Icons.Default.Star, label = "Zorluk", value = plant.difficultyLevel)
        InfoDetailRow(icon = Icons.Default.WaterDrop, label = "Sulama Sıklığı", value = "${plant.wateringIntervalDays} günde bir")
        InfoDetailRow(icon = Icons.Default.FilterVintage, label = "Gübreleme", value = plant.fertilizationFrequency)
        InfoDetailRow(icon = Icons.Default.ContentCut, label = "Budama", value = plant.pruningFrequency)
        InfoDetailRow(icon = Icons.Default.Yard, label = "Saksı Değişimi", value = plant.repottingFrequency)
        Divider()
        Text(
            text = "Genel Bilgi",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = plant.generalInfo,
            style = MaterialTheme.typography.bodyLarge
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
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
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
fun InfoChip(icon: ImageVector, label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun InfoDetailRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelLarge)
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
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