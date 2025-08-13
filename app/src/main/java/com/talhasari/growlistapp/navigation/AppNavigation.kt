package com.talhasari.growlistapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.talhasari.growlistapp.ui.theme.screens.addplant.AddPlantScreen
import com.talhasari.growlistapp.ui.theme.screens.auth.LoginScreen
import com.talhasari.growlistapp.ui.theme.screens.detail.PlantDetailScreen
import com.talhasari.growlistapp.ui.theme.screens.main.MainScreen
import com.talhasari.growlistapp.ui.theme.screens.onboarding.OnboardingScreen
import com.talhasari.growlistapp.ui.theme.screens.splash.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(navController = navController)
        }


        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(navController = navController)
        }

       
        composable(route = Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(route = Screen.Main.route) {
            MainScreen(mainNavController = navController)
        }

        composable(route = Screen.AddPlant.route) {
            AddPlantScreen(navController = navController)
        }

        composable(
            route = Screen.PlantDetail.route,
            arguments = listOf(navArgument("plantId") { type = NavType.IntType })
        ) {
            PlantDetailScreen(navController = navController)
        }
    }
}