package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val BlushDarkColorScheme =
  darkColorScheme(
    primary = DarkRosePrimary,
    onPrimary = OnDarkRosePrimary,
    primaryContainer = DarkRosePrimaryContainer,
    onPrimaryContainer = OnDarkRosePrimaryContainer,
    secondary = DarkMauveSecondary,
    onSecondary = OnDarkMauveSecondary,
    secondaryContainer = DarkMauveSecondaryContainer,
    onSecondaryContainer = OnDarkMauveSecondaryContainer,
    tertiary = DarkPeonyTertiary,
    onTertiary = OnDarkPeonyTertiary,
    tertiaryContainer = DarkPeonyTertiaryContainer,
    onTertiaryContainer = OnDarkPeonyTertiaryContainer,
    background = DarkBlushBackground,
    onBackground = OnDarkBlushBackground,
    surface = DarkBlushSurface,
    onSurface = OnDarkBlushSurface,
    surfaceVariant = DarkBlushSurfaceVariant,
    onSurfaceVariant = OnDarkBlushSurfaceVariant,
    surfaceContainerLow = DarkBlushSurfaceContainerLow,
    surfaceContainer = DarkBlushSurfaceContainer,
    surfaceContainerHigh = DarkBlushSurfaceContainerHigh,
    outline = DarkBlushOutline,
    outlineVariant = DarkBlushOutlineVariant,
  )

private val BlushLightColorScheme =
  lightColorScheme(
    primary = RosePrimary,
    onPrimary = OnRosePrimary,
    primaryContainer = RosePrimaryContainer,
    onPrimaryContainer = OnRosePrimaryContainer,
    secondary = MauveSecondary,
    onSecondary = OnMauveSecondary,
    secondaryContainer = MauveSecondaryContainer,
    onSecondaryContainer = OnMauveSecondaryContainer,
    tertiary = PeonyTertiary,
    onTertiary = OnPeonyTertiary,
    tertiaryContainer = PeonyTertiaryContainer,
    onTertiaryContainer = OnPeonyTertiaryContainer,
    background = BlushBackground,
    onBackground = OnBlushBackground,
    surface = BlushSurface,
    onSurface = OnBlushSurface,
    surfaceVariant = BlushSurfaceVariant,
    onSurfaceVariant = OnBlushSurfaceVariant,
    surfaceContainerLowest = BlushSurfaceContainerLowest,
    surfaceContainerLow = BlushSurfaceContainerLow,
    surfaceContainer = BlushSurfaceContainer,
    surfaceContainerHigh = BlushSurfaceContainerHigh,
    surfaceContainerHighest = BlushSurfaceContainerHighest,
    outline = BlushOutline,
    outlineVariant = BlushOutlineVariant,
    error = BlushError,
    onError = OnBlushError,
    errorContainer = BlushErrorContainer,
    onErrorContainer = OnBlushErrorContainer,
  )

val BlushShapes =
  Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp),
  )

@Composable
fun BlushTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) BlushDarkColorScheme else BlushLightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = BlushTypography,
    shapes = BlushShapes,
    content = content,
  )
}
