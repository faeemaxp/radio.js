package com.robco.pipboyradio.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PhosphorPrimary,
    onPrimary = Color.Black,
    primaryContainer = PhosphorDim,
    onPrimaryContainer = PhosphorBright,
    secondary = PhosphorSecondary,
    onSecondary = Color.Black,
    background = ScreenBackground,
    onBackground = PhosphorPrimary,
    surface = SurfaceCard,
    onSurface = PhosphorPrimary,
    surfaceVariant = SurfaceCardElevated,
    onSurfaceVariant = PhosphorSecondary,
    outline = SurfaceBorder,
    error = AccentRed,
    onError = Color.Black
)

@Composable
fun PipBoyTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = PipBoyTypography,
        content = content
    )
}
