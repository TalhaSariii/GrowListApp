package com.talhasari.growlistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.talhasari.growlistapp.navigation.AppNavigation
import com.talhasari.growlistapp.ui.theme.GrowListAppTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GrowListAppTheme {
                AppNavigation()
            }
        }
    }
}

