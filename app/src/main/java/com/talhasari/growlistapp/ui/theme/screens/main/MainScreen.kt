package com.talhasari.growlistapp.ui.theme.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.talhasari.growlistapp.navigation.BottomNavItem
import com.talhasari.growlistapp.ui.theme.screens.dashboard.DashboardScreen
import com.talhasari.growlistapp.ui.theme.screens.encyclopedia.EncyclopediaScreen
import com.talhasari.growlistapp.ui.theme.screens.profile.ProfileScreen
import com.talhasari.growlistapp.ui.theme.screens.wishlist.WishlistScreen

@Composable
fun MainScreen(mainNavController: NavHostController) {
    val bottomBarNavController = rememberNavController()
    val navBackStackEntry by bottomBarNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Navigasyon menüsüne Dilek Listesi eklendi
    val items = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Encyclopedia,
        BottomNavItem.Wishlist,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            bottomBarNavController.navigate(screen.route) {
                                popUpTo(bottomBarNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomBarNavController,
            startDestination = BottomNavItem.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Dashboard.route) { DashboardScreen(navController = mainNavController) }
            composable(BottomNavItem.Encyclopedia.route) { EncyclopediaScreen(navController = mainNavController) }
            composable(BottomNavItem.Profile.route) { ProfileScreen(navController = mainNavController) }

            composable(BottomNavItem.Wishlist.route) { WishlistScreen(navController = mainNavController) }
        }
    }
}