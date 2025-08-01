package com.talhasari.growlistapp.navigation

sealed class Screen(val route: String) {
    object  Splash : Screen("splash_screen")

    object Onboarding: Screen("onboarding_screen")

    object Dashboard: Screen("dashboard_screen")

    object AddPlant: Screen("add_plant_screen")

    object PlantDetail: Screen("plant_detail_screen/{plantId}")
    {
        fun createRoute(plantId: Int) = "plant_detail_screen/$plantId"
    }
}