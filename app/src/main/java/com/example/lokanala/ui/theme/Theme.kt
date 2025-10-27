package com.example.lokanala.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/* =======================================
   🎨 LOKANALA LIGHT THEME COLOR SCHEME
   ======================================= */
private val LightColorScheme = lightColorScheme(
    primary = PrimaryPink,
    onPrimary = WhiteText,
    primaryContainer = LightPink,
    onPrimaryContainer = DarkText,

    secondary = StarYellow,
    onSecondary = DarkText,
    secondaryContainer = StarYellowDark,

    background = BackgroundWhite,
    onBackground = DarkText,

    surface = SurfaceCard,
    onSurface = RegularText,
    surfaceVariant = LightGrayBackground,
    onSurfaceVariant = LightText,

    tertiary = IconPink,
    onTertiary = WhiteText
)

/* =======================================
   🌈 FINAL LOKANALA THEME (Light Only)
   ======================================= */
@Composable
fun LokanalaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography, // pastikan sudah ada di Type.kt
        content = content
    )
}
