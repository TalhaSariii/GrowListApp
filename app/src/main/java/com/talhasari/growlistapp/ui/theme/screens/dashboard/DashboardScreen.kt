package com.talhasari.growlistapp.ui.theme.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.talhasari.growlistapp.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GrowList Ana Ekran") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Bitkilerin burada listelenecek.")

            Spacer(modifier = Modifier.height(24.dp))


            Button(onClick = {
                navController.navigate(Screen.AddPlant.route)
            }) {
                Text(text = "Yeni Bitki Ekle")
            }

            Spacer(modifier = Modifier.height(16.dp))


            Button(onClick = {

                navController.navigate(Screen.PlantDetail.createRoute(plantId = 123))
            }) {
                Text(text = "Örnek Bitki Detayına Git (Test)")
            }
        }
    }
}