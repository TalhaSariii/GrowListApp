package com.talhasari.growlistapp.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Onboarding: Screen("onboarding_screen")
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object Main : Screen("main_screen")
    object Dashboard: Screen("dashboard_screen")
    object Encyclopedia : Screen("encyclopedia_screen")
    object Profile : Screen("profile_screen")
    object AddPlant: Screen("add_plant_screen")
    object Wishlist : Screen("wishlist_screen")

    object PlantDetail: Screen("plant_detail_screen/{plantId}") {
        fun createRoute(plantId: Int) = "plant_detail_screen/$plantId"
    }


    object EncyclopediaDetail: Screen("encyclopedia_detail_screen/{plantTypeId}") {
        fun createRoute(plantTypeId: String) = "encyclopedia_detail_screen/$plantTypeId"
    }
}