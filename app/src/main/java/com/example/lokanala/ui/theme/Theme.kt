package com.example.lokanala.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.ui.graphics.Color

//LOKANALA LIGHT THEME
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

    surface = Color(0xFFF9F4F6),
    onSurface = Color(0xFF1A1A1A),

    surfaceVariant = Color(0xFFF3E9EC),
    onSurfaceVariant = Color(0xFF3B3B3B),

    tertiary = IconPink,
    onTertiary = WhiteText
)

//LOKANALA DARK THEME
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF80AB),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFFAD1457),
    onPrimaryContainer = Color.White,

    secondary = Color(0xFFFFCA28),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFFFB300),

    background = Color(0xFF121212),
    onBackground = Color(0xFFEAEAEA),

    surface = Color(0xFF2B1E23),
    onSurface = Color(0xFFF5F5F5),

    surfaceVariant = Color(0xFF3A2C31),
    onSurfaceVariant = Color(0xFFD7D7D7),

    tertiary = Color(0xFFFF8A80),
    onTertiary = Color.Black
)

//FINAL LOKANALA THEME
@Composable
fun LokanalaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = !darkTheme
        )
        systemUiController.setNavigationBarColor(
            color = Color.Transparent,
            darkIcons = !darkTheme
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
