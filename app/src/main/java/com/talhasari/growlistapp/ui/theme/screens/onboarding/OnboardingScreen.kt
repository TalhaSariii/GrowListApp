package com.talhasari.growlistapp.ui.theme.screens.onboarding




import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.talhasari.growlistapp.navigation.Screen

@Composable
fun OnboardingScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "GrowList'e Hoş Geldin!")

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {

            navController.navigate(Screen.Main.route) {
                popUpTo(Screen.Onboarding.route) {
                    inclusive = true
                }
            }
        }) {
            Text(text = "Hadi Başlayalım!")
        }
    }
}