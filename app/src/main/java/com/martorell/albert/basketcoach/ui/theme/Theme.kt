package com.martorell.albert.basketcoach.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val PiaLightColorScheme = lightColorScheme(
    primary = PiaNavy,
    onPrimary = PiaWhite,
    primaryContainer = PiaNavy,
    onPrimaryContainer = PiaWhite,
    secondary = PiaNavy,
    onSecondary = PiaWhite,
    secondaryContainer = PiaSurfaceVariant,
    onSecondaryContainer = PiaTextPrimary,
    tertiary = PiaTacticalOrange,
    onTertiary = PiaWhite,
    background = PiaBackground,
    onBackground = PiaTextPrimary,
    surface = PiaSurface,
    onSurface = PiaTextPrimary,
    surfaceVariant = PiaSurfaceVariant,
    onSurfaceVariant = PiaTextMuted,
    outline = PiaOutline,
    outlineVariant = PiaSlate,
    error = PiaDangerRed,
    onError = PiaWhite
)

@Composable
fun BasketCoachTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PiaLightColorScheme,
        typography = Typography,
        content = content
    )
}
