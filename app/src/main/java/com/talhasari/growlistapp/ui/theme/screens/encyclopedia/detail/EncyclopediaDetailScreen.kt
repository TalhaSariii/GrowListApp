package com.talhasari.growlistapp.ui.theme.screens.encyclopedia.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.talhasari.growlistapp.data.remote.PlantType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncyclopediaDetailScreen(
    navController: NavController,
    viewModel: EncyclopediaDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // DEĞİŞTİRİLDİ: Scaffold'un padding'i artık manuel yönetiliyor.
    Scaffold { innerPadding ->
        // Bu Box, Scaffold'un padding'ini yok sayarak tüm ekranı kaplar.
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.plantType != null) {
                PlantTypeDetails(
                    plantType = uiState.plantType!!,
                    isInWishlist = uiState.isInWishlist,
                    onWishlistToggle = { viewModel.toggleWishlist() },
                    onBackClick = { navController.popBackStack() },
                    // Sadece alt navigasyon çubuğu için gerekli boşluğu ekliyoruz.
                    modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                )
            } else {
                Text("Bitki türü bulunamadı.")
            }
        }
    }
}

@Composable
private fun PlantTypeDetails(
    plantType: PlantType,
    isInWishlist: Boolean,
    onWishlistToggle: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Dışarıdan gelen modifier (bottom padding içeren) buraya uygulanıyor.
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        PlantTypeHeader(
            plantType = plantType,
            isInWishlist = isInWishlist,
            onWishlistToggle = onWishlistToggle,
            onBackClick = onBackClick
        )
        Description(plantType = plantType)
    }
}


@Composable
private fun PlantTypeHeader(
    plantType: PlantType,
    isInWishlist: Boolean,
    onWishlistToggle: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
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

        IconButton(
            onClick = onWishlistToggle,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding() // Bu padding, butonu status bar'ın altına iter
                .padding(end = 16.dp, top = 16.dp)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
        ) {
            Icon(
                imageVector = if (isInWishlist) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Dilek Listesine Ekle",
                tint = if (isInWishlist) Color(0xFFE53935) else Color.White
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
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
private fun Description(plantType: PlantType) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        InfoDetailRow(icon = Icons.Default.Thermostat, label = "Sıcaklık", value = plantType.temperatureRange)
        InfoDetailRow(icon = Icons.Default.WbSunny, label = "Işık İhtiyacı", value = plantType.lightRequirement)
        InfoDetailRow(icon = Icons.Default.Opacity, label = "Nem İhtiyacı", value = plantType.humidityRequirement)
        Divider()
        InfoDetailRow(icon = Icons.Default.Star, label = "Zorluk Seviyesi", value = plantType.difficultyLevel)
        InfoDetailRow(icon = Icons.Default.WaterDrop, label = "Sulama Sıklığı", value = "${plantType.wateringIntervalDays} günde bir")
        InfoDetailRow(icon = Icons.Default.FilterVintage, label = "Gübreleme", value = plantType.fertilizationFrequency)
        InfoDetailRow(icon = Icons.Default.ContentCut, label = "Budama", value = plantType.pruningFrequency)
        InfoDetailRow(icon = Icons.Default.Yard, label = "Saksı Değişimi", value = plantType.repottingFrequency)

        Text(
            text = "Genel Bilgi",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = plantType.generalInfo,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun InfoDetailRow(icon: ImageVector, label: String, value: String) {
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