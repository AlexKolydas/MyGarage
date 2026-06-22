package com.alexkolydas.mygarage.core.ui.model

import androidx.compose.ui.graphics.Color

data class UrgencyUi(
    val key: String,
    val label: String,
    val fg: Color,
    val bg: Color,
    val remaining: Int?,
)
