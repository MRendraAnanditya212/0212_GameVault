package com.example.gamevault.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val GameVaultDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = OnNeonCyan,
    primaryContainer = NeonCyanContainer,
    onPrimaryContainer = OnNeonCyanContainer,
    secondary = CyberViolet,
    onSecondary = OnCyberViolet,
    secondaryContainer = CyberVioletContainer,
    onSecondaryContainer = OnCyberVioletContainer,
    tertiary = RatingGold,
    onTertiary = OnRatingGold,
    tertiaryContainer = RatingGoldContainer,
    onTertiaryContainer = TextPrimary,
    background = VaultObsidian,
    onBackground = TextPrimary,
    surface = VaultSurface,
    onSurface = TextPrimary,
    surfaceVariant = VaultSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    surfaceContainerLowest = VaultObsidian,
    surfaceContainerLow = VaultSurface,
    surfaceContainer = VaultSurfaceElevated,
    surfaceContainerHigh = VaultSurfaceHigh,
    surfaceContainerHighest = VaultSurfaceHigh,
    outline = VaultOutline,
    outlineVariant = VaultOutlineVariant,
    error = CrimsonError,
    onError = OnCrimsonError,
    errorContainer = CrimsonErrorContainer,
    onErrorContainer = TextPrimary
)

private val GameVaultLightColorScheme = lightColorScheme(
    primary = NeonCyanDark,
    onPrimary = LightSurface,
    primaryContainer = OnNeonCyanContainer,
    onPrimaryContainer = OnNeonCyan,
    secondary = CyberViolet,
    onSecondary = LightSurface,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    error = CrimsonError,
    onError = LightSurface
)

@Composable
fun GameVaultTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        GameVaultDarkColorScheme
    } else {
        GameVaultLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = GameVaultTypography,
        content = content
    )
}