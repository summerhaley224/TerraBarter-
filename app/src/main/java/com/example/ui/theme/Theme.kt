package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ForestLight,
    onPrimary = EarthDark,
    primaryContainer = ForestDeep,
    onPrimaryContainer = SageMuted,
    secondary = AmberWarm,
    onSecondary = EarthDark,
    secondaryContainer = TerracottaDark,
    onSecondaryContainer = AmberSoft,
    tertiary = MossGreen,
    onTertiary = EarthDark,
    background = EarthDark,
    onBackground = EarthTextPrimaryDark,
    surface = EarthDarkCard,
    onSurface = EarthTextPrimaryDark,
    surfaceVariant = EarthDarkSurfaceVariant,
    onSurfaceVariant = EarthTextSecondaryDark,
    outline = EarthBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = EarthCream,
    primaryContainer = SageMuted,
    onPrimaryContainer = ForestDeep,
    secondary = Terracotta,
    onSecondary = EarthCream,
    secondaryContainer = AmberSoft,
    onSecondaryContainer = TerracottaDark,
    tertiary = MossGreen,
    onTertiary = EarthCream,
    background = EarthCream,
    onBackground = EarthTextPrimary,
    surface = EarthCreamCard,
    onSurface = EarthTextPrimary,
    surfaceVariant = EarthSurfaceVariant,
    onSurfaceVariant = EarthTextSecondary,
    outline = EarthBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve our distinctive warm Earth theme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
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
