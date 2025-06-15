package com.example.railid.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF0D47A1),
    secondary = Color(0xFF1976D2),
    background = Color(0xFFF1F1F1),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
)

@Composable
fun RailIdTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}
