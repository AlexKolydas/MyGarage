package com.alexkolydas.mygarage.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GarageColorScheme = darkColorScheme(
    primary          = GarageAccent,
    onPrimary        = GarageBackground,
    secondary        = GarageTextSecond,
    onSecondary      = GarageBackground,
    background       = GarageBackground,
    onBackground     = GarageTextPrimary,
    surface          = GarageSurface,
    onSurface        = GarageTextPrimary,
    surfaceVariant   = GarageInputBg,
    onSurfaceVariant = GarageTextSecond,
    outline          = Color.White.copy(alpha = 0.08f),
    outlineVariant   = Color.White.copy(alpha = 0.07f),
)

@Composable
fun MyGarageTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GarageColorScheme,
        typography  = GarageTypography,
        content     = content,
    )
}
