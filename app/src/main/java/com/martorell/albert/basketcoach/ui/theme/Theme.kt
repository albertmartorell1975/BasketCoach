package com.martorell.albert.basketcoach.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PiaNavy,
    onPrimary = Color.White,
    primaryContainer = PiaNavyDark,
    onPrimaryContainer = Color.White,
    secondary = PiaTacticalOrange,
    onSecondary = Color.White,
    tertiary = PiaSuccessGreen,
    background = Color(0xFF121826),
    onBackground = Color(0xFFE2E8F0),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = PiaOutline
)

private val LightColorScheme = lightColorScheme(
    primary = PiaNavy,
    onPrimary = Color.White,
    primaryContainer = PiaSurfaceVariant,
    onPrimaryContainer = PiaNavy,
    secondary = PiaTacticalOrange,
    onSecondary = Color.White,
    tertiary = PiaSuccessGreen,
    onTertiary = Color.White,
    background = PiaBackground,
    onBackground = PiaTextPrimary,
    surface = PiaSurface,
    onSurface = PiaTextPrimary,
    surfaceVariant = PiaSurfaceVariant,
    onSurfaceVariant = PiaTextMuted,
    outline = PiaOutline
)

@Composable
fun BasketCoachTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
