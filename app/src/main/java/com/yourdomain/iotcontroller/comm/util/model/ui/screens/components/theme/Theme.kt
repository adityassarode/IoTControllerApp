package com.yourdomain.iotcontroller.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = VoXNetBlue,
    onPrimary = OnPrimaryLight,
    primaryContainer = VoXNetBlueVariant,
    onPrimaryContainer = OnPrimaryLight,
    secondary = VoXNetGray,
    onSecondary = OnPrimaryLight,
    background = BackgroundLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    error = SOSRed,
    onError = OnPrimaryLight,
    outline = VoXNetLightGray
)

private val DarkColorScheme = darkColorScheme(
    primary = VoXNetBlue,
    onPrimary = OnPrimaryDark,
    primaryContainer = VoXNetBlueVariant,
    onPrimaryContainer = OnPrimaryDark,
    secondary = VoXNetGray,
    onSecondary = OnPrimaryDark,
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    error = SOSRed,
    onError = OnSurfaceDark,
    outline = VoXNetLightGray
)

@Composable
fun IoTControllerTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,   // reference your Type.kt  // use default or define your own shapes
        content = content
    )
}
