package com.alexkolydas.mygarage.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Replace with real TTF font families from res/font/ if desired.
// Download IBM Plex Sans + IBM Plex Mono from fonts.google.com.
val IbmPlexSansFamily: FontFamily = FontFamily.SansSerif
val IbmPlexMonoFamily: FontFamily = FontFamily.Monospace

val GarageTypography = Typography(
    titleLarge  = TextStyle(fontFamily = IbmPlexSansFamily, fontWeight = FontWeight.Bold,     fontSize = 32.sp, letterSpacing = (-0.3).sp),
    titleMedium = TextStyle(fontFamily = IbmPlexSansFamily, fontWeight = FontWeight.Bold,     fontSize = 20.sp, letterSpacing = (-0.2).sp),
    titleSmall  = TextStyle(fontFamily = IbmPlexSansFamily, fontWeight = FontWeight.Bold,     fontSize = 17.sp, letterSpacing = (-0.2).sp),
    bodyLarge   = TextStyle(fontFamily = IbmPlexSansFamily, fontWeight = FontWeight.Normal,   fontSize = 15.sp, lineHeight = 22.sp),
    bodyMedium  = TextStyle(fontFamily = IbmPlexSansFamily, fontWeight = FontWeight.Normal,   fontSize = 13.5.sp, lineHeight = 20.sp),
    bodySmall   = TextStyle(fontFamily = IbmPlexSansFamily, fontWeight = FontWeight.Normal,   fontSize = 13.sp),
    labelLarge  = TextStyle(fontFamily = IbmPlexSansFamily, fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
    labelMedium = TextStyle(fontFamily = IbmPlexSansFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp),
    labelSmall  = TextStyle(fontFamily = IbmPlexSansFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, letterSpacing = 0.5.sp),
)
