package com.talhasari.growlistapp.ui.theme.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.talhasari.growlistapp.R
import com.talhasari.growlistapp.navigation.Screen

@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val uiState by profileViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showNameDialog by remember { mutableStateOf(false) }
    var showEmailDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = uiState.isSignedOut) {
        if (uiState.isSignedOut) {
            navController.navigate(Screen.Login.route) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    LaunchedEffect(key1 = uiState.userMessage) {
        uiState.userMessage?.let {
            snackbarHostState.showSnackbar(it)
            profileViewModel.userMessageShown()
        }
    }

    if (showNameDialog) {
        EditDialog(
            title = "Ad Soyad Değiştir",
            initialValue = uiState.currentUser?.displayName ?: "",
            onDismiss = { showNameDialog = false },
            onSave = { newName ->
                profileViewModel.onDisplayNameChange(newName)
                showNameDialog = false
            }
        )
    }

    if (showEmailDialog) {
        EditDialog(
            title = "E-posta Adresi Değiştir",
            initialValue = uiState.currentUser?.email ?: "",
            onDismiss = { showEmailDialog = false },
            onSave = { newEmail ->
                profileViewModel.onEmailChange(newEmail)
                showEmailDialog = false
            }
        )
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            Spacer(modifier = Modifier.height(32.dp))
            ProfileHeader(
                displayName = uiState.currentUser?.displayName,
                email = uiState.currentUser?.email,
                photoUrl = uiState.currentUser?.photoUrl,
                onImageChange = { uri ->
                    uri?.let { profileViewModel.onProfilePictureChange(it) }
                }
            )
            Spacer(modifier = Modifier.height(32.dp))
            SettingsSection(
                onNameClick = { showNameDialog = true },
                onEmailClick = { showEmailDialog = true },
                onSignOutClick = { profileViewModel.signOut() }
            )
        }
    }
}

@Composable
fun ProfileHeader(
    displayName: String?,
    email: String?,
    photoUrl: Uri?,
    onImageChange: (Uri?) -> Unit
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = onImageChange
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.BottomEnd) {
            AsyncImage(
                model = photoUrl,
                contentDescription = "Profil Fotoğrafı",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentScale = ContentScale.Crop,
                fallback = painterResource(id = R.drawable.ic_launcher_background)
            )
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Resmi Değiştir",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = displayName ?: "İsim Yok",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = email ?: "E-posta Yok",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SettingsSection(
    onNameClick: () -> Unit,
    onEmailClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SettingsCard(title = "Hesap Ayarları") {
            SettingsItem(icon = Icons.Default.Person, text = "Ad Soyad Değiştir", onClick = onNameClick)
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            SettingsItem(icon = Icons.Default.Email, text = "E-posta Değiştir", onClick = onEmailClick)
        }
        Spacer(modifier = Modifier.height(16.dp))
        SettingsCard(title = "Uygulama Ayarları") {
            SettingsItem(icon = Icons.Default.Language, text = "Dil", onClick = { /* TODO */ }) {
                Text("Türkçe", color = MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onSignOutClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text("Çıkış Yap")
        }
    }
}

@Composable
fun SettingsCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            content = content
        )
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    trailingContent: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
    }
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = text, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, modifier = Modifier.weight(1f))
        trailingContent()
    }
}

@Composable
fun EditDialog(
    title: String,
    initialValue: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialValue) }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("İptal")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onSave(text) }) {
                        Text("Kaydet")
                    }
                }
            }
        }
    }
}