package com.talhasari.growlistapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val title: String) {
    object Dashboard : BottomNavItem(Screen.Dashboard.route, Icons.Default.Home, "Bitkilerim")
    object Encyclopedia : BottomNavItem(Screen.Encyclopedia.route, Icons.Default.List, "Ansiklopedi")
    object Profile : BottomNavItem(Screen.Profile.route, Icons.Default.AccountCircle, "Profil")
}