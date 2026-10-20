package com.example.fractionfix.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.fractionfix.data.model.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = SlatePrimaryDark,
    onPrimary = SlateOnPrimaryDark,
    primaryContainer = SlatePrimaryContainerDark,
    onPrimaryContainer = SlateOnPrimaryContainerDark,
    secondary = SlateSecondaryDark,
    onSecondary = SlateOnSecondaryDark,
    secondaryContainer = SlateSecondaryContainerDark,
    onSecondaryContainer = SlateOnSecondaryContainerDark,
    tertiary = SlateTertiaryDark,
    onTertiary = SlateOnTertiaryDark,
    background = SlateBackgroundDark,
    onBackground = SlateOnBackgroundDark,
    surface = SlateSurfaceDark,
    onSurface = SlateOnSurfaceDark,
    surfaceVariant = SlateSurfaceVariantDark,
    onSurfaceVariant = SlateOnSurfaceVariantDark,
    outline = SlateOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = SlatePrimaryLight,
    onPrimary = SlateOnPrimaryLight,
    primaryContainer = SlatePrimaryContainerLight,
    onPrimaryContainer = SlateOnPrimaryContainerLight,
    secondary = SlateSecondaryLight,
    onSecondary = SlateOnSecondaryLight,
    secondaryContainer = SlateSecondaryContainerLight,
    onSecondaryContainer = SlateOnSecondaryContainerLight,
    tertiary = SlateTertiaryLight,
    onTertiary = SlateOnTertiaryLight,
    background = SlateBackgroundLight,
    onBackground = SlateOnBackgroundLight,
    surface = SlateSurfaceLight,
    onSurface = SlateOnSurfaceLight,
    surfaceVariant = SlateSurfaceVariantLight,
    onSurfaceVariant = SlateOnSurfaceVariantLight,
    outline = SlateOutlineLight
)

@Composable
fun FractionFixTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
