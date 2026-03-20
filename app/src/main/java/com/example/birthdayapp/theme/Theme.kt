package com.example.birthdayapp.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Applying the new Soft & Romantic Palette natively to the Material defaults
private val RomanticColorScheme = lightColorScheme(
    primary = SoftCoral,
    onPrimary = PureWhite,
    secondary = SoftPinkShadow,
    onSecondary = DeepWarmBrown,
    background = SoftPearl,
    onBackground = DeepWarmBrown,
    surface = PureWhite,
    onSurface = DeepWarmBrown,
)

@Composable
fun BirthdayAppTheme(
    content: @Composable () -> Unit
) {
    // We strictly use the Light Romantic Theme now, discarding the old dark/sleek looks
    MaterialTheme(
        colorScheme = RomanticColorScheme,
        typography = Typography,
        content = content
    )
}
