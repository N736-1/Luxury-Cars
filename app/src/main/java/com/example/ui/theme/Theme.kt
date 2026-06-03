package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = LuxuryGold,
    secondary = LuxuryGoldLight,
    tertiary = RedCalipers,
    background = DeepObsidian,
    surface = BrushedSteel,
    onPrimary = DeepObsidian,
    onSecondary = DeepObsidian,
    onTertiary = ChromeWhite,
    onBackground = SilverSatin,
    onSurface = ChromeWhite,
    error = RedCalipers
)

private val LightColorScheme = lightColorScheme(
    primary = GoldenBronzeMatte,
    secondary = SteelGrayAccent,
    tertiary = RedCalipers,
    background = AlabasterWhite,
    surface = PureChrome,
    onPrimary = PureChrome,
    onSecondary = PureChrome,
    onBackground = CharcoalMatte,
    onSurface = CharcoalMatte,
    error = RedCalipers
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep false to preserve brand-specific gold/obsidian aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
