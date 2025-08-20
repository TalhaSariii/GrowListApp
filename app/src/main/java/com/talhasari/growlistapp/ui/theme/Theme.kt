package com.talhasari.growlistapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


private val LightColorScheme = lightColorScheme(
    primary = VibrantGreen,
    secondary = VibrantGreen, // FAB butonu gibi ikincil elemanlar da canlı yeşil olsun
    background = LightGreenBackground,
    surface = CardBackground, // Kartların arka planı
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = DarkText,
    onSurface = DarkText,
    onSurfaceVariant = LightText,
    primaryContainer = SurfaceVariantGreen, // Sulama özet kartının arka planı
    onPrimaryContainer = DarkText,
    surfaceVariant = SurfaceVariantGreen, // Varsayılan resim arka planı gibi yüzeyler için

    // Navigasyon Bar Renkleri
    secondaryContainer = VibrantGreen.copy(alpha = 0.2f), // Seçili sekmenin arka planı
    onSecondaryContainer = VibrantGreen // Seçili sekmenin ikonu
)

@Composable
fun GrowListAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Durum çubuğunun rengini de ana arka plan rengimizle aynı yapalım
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}