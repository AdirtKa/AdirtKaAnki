package com.example.adirtkaanki.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PurpleDarkPrimary,
    onPrimary = PurpleDarkOnPrimary,
    secondary = PurpleDarkSecondary,
    tertiary = PurpleDarkTertiary,

    background = PurpleDarkBackground,
    surface = PurpleDarkSurface,
    surfaceVariant = PurpleDarkSurfaceVariant,

    onBackground = PurpleDarkOnBackground,
    onSurface = PurpleDarkOnSurface
)

private val LightColorScheme = lightColorScheme(
    primary = SoftLightPrimary,
    onPrimary = SoftLightOnPrimary,
    secondary = SoftLightSecondary,
    tertiary = SoftLightTertiary,

    background = SoftLightBackground,
    surface = SoftLightSurface,
    surfaceVariant = SoftLightSurfaceVariant,

    onBackground = SoftLightOnBackground,
    onSurface = SoftLightOnSurface
)

@Composable
fun AdirtKaAnkiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}