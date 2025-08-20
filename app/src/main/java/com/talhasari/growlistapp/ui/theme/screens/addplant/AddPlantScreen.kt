package com.talhasari.growlistapp.ui.theme.screens.addplant

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.talhasari.growlistapp.data.remote.PlantType
import com.talhasari.growlistapp.utils.createImageFile
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantScreen(
    navController: NavController,
    addPlantViewModel: AddPlantViewModel = viewModel()
) {
    val uiState by addPlantViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var plantName by remember { mutableStateOf("") }
    var plantLocation by remember { mutableStateOf("") }

    // Seçilen bitki türünü artık bir nesne olarak tutuyoruz
    var selectedPlantType by remember { mutableStateOf<PlantType?>(null) }
    var plantTypeInputText by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (!success) {
                // Fotoğraf çekme iptal edilirse URI'yi temizle
                imageUri = null
            }
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val file = context.createImageFile()
                val uri = FileProvider.getUriForFile(
                    Objects.requireNonNull(context),
                    "com.talhasari.growlistapp.provider",
                    file
                )
                imageUri = uri
                cameraLauncher.launch(uri)
            } else {
                scope.launch { snackbarHostState.showSnackbar("Kamera izni gerekli!") }
            }
        }
    )

    LaunchedEffect(key1 = uiState.userMessage) {
        uiState.userMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
                addPlantViewModel.userMessageShown()
                if (message.contains("başarıyla")) {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Yeni Bitki Ekle") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier.size(160.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    val painter = rememberAsyncImagePainter(
                        model = imageUri ?: com.talhasari.growlistapp.R.drawable.ic_launcher_background // Varsayılan bir resim ekleyebilirsin
                    )
                    if (imageUri != null) {
                        Image(
                            painter = painter,
                            contentDescription = "Çekilen Fotoğraf",
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Yard,
                                contentDescription = "Varsayılan Bitki İkonu",
                                modifier = Modifier.size(60.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(
                        onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Fotoğraf Çek",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = plantName,
                    onValueChange = { plantName = it },
                    label = { Text("Bitkinin Adı (Örn: Yeşil Dostum)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = plantTypeInputText,
                        onValueChange = { newText ->
                            plantTypeInputText = newText
                            addPlantViewModel.onSearchQueryChanged(newText)
                            isDropdownExpanded = true
                            selectedPlantType = null // Kullanıcı yeni arama yaparsa seçimi sıfırla
                        },
                        label = { Text("Bitki Türünü Ara") },
                        trailingIcon = { Icon(Icons.Default.Search, contentDescription = "Ara") },
                        singleLine = true,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    if (uiState.filteredPlantTypes.isNotEmpty()) {
                        ExposedDropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false }
                        ) {
                            uiState.filteredPlantTypes.forEach { plantType ->
                                DropdownMenuItem(
                                    text = { Text("${plantType.name} (${plantType.scientificName})") },
                                    onClick = {
                                        selectedPlantType = plantType
                                        plantTypeInputText = plantType.name
                                        addPlantViewModel.onSearchQueryChanged("")
                                        isDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = plantLocation,
                    onValueChange = { plantLocation = it },
                    label = { Text("Konumu (Örn: Salon Penceresi)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        addPlantViewModel.savePlant(
                            name = plantName,
                            selectedPlantType = selectedPlantType,
                            location = plantLocation,
                            imageUrl = imageUri?.toString()
                        )
                    },

                    enabled = plantName.isNotBlank() && plantLocation.isNotBlank() && selectedPlantType != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    Text(text = "Kaydet", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}