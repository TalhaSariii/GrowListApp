package com.talhasari.growlistapp.ui.theme.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.talhasari.growlistapp.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    splashViewModel: SplashViewModel = viewModel()
) {
    val isUserLoggedIn by splashViewModel.isUserLoggedIn.collectAsState()

    LaunchedEffect(key1 = isUserLoggedIn) {
        if (isUserLoggedIn != null) {
            delay(1500L)
            val destination = if (isUserLoggedIn == true) {
                Screen.Main.route
            } else {
                Screen.Login.route
            }
            navController.navigate(destination) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "GrowList", style = MaterialTheme.typography.headlineLarge, color = Color.White)
    }
}