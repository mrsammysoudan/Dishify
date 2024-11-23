package com.example.dishifyai.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.ViewCompat

private fun lightColorScheme() = lightColorScheme(
    primary = Color(0xFF004D40), // Teal 800
    onPrimary = Color.White,
    primaryContainer = Color(0xFF80CBC4), // Teal 200
    onPrimaryContainer = Color.Black,
    secondary = Color(0xFF00897B), // Teal 600
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2DFDB), // Teal 100
    onSecondaryContainer = Color.Black,
    tertiary = Color(0xFF004D40), // Teal 800
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF80CBC4), // Teal 200
    onTertiaryContainer = Color.Black,
    background = Color(0xFFEDF5F4), // Teal 50
//    background = Color(0xFF141414),
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    error = Color(0xFFB00020), // Default error color
    onError = Color.White,
    surfaceVariant = Color(0xFFDDE7EC),
    onSurfaceVariant = Color(0xFF40484C),
    errorContainer = Color(0xFF930006),
    onErrorContainer = Color.White,
    outline = Color(0xFF737373),
    inverseOnSurface = Color(0xFF121212),
    inverseSurface = Color(0xFFF4F4F4),
    inversePrimary = Color(0xFFB2DFDB)
)

private fun darkColorScheme() = darkColorScheme(
    primary = Color(0xFF80CBC4), // Lighter shade for dark theme
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF004D40),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFB2DFDB),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF00897B),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFF80CBC4),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF004D40),
    onTertiaryContainer = Color.White,
    background = Color(0xFF004D40),
    onBackground = Color.White,
    surface = Color(0xFF121212),
    onSurface = Color.White,
    error = Color(0xFFCF6679),
    onError = Color.Black,
    surfaceVariant = Color(0xFF121212).copy(alpha = 0.5f),
    onSurfaceVariant = Color(0xFFECEFF1),
    errorContainer = Color(0xFFCF6679).copy(alpha = 0.5f),
    onErrorContainer = Color(0xFF000000),
    outline = Color(0xFF919191),
    inverseOnSurface = Color(0xFFE0E0E0),
    inverseSurface = Color(0xFF212121),
    inversePrimary = Color(0xFF00695C)
)

private val LocalAppDimens = staticCompositionLocalOf {
    normalDimensions
}

@Composable
fun ProvideDimens(
    dimensions: Dimensions,
    content: @Composable () -> Unit
) {
    val dimensionSet = remember { dimensions }
    CompositionLocalProvider(LocalAppDimens provides dimensionSet, content = content)
}

val GrayCustom = Color(0xFF808080)  // Define your custom gray

@Composable
fun DishifyAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) darkColorScheme() else lightColorScheme()
    val activity = (LocalContext.current as? Activity)

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = {
            if (activity != null) {
                SideEffect {
                    // Ensure we're changing the status bar color in a valid context
                    activity.window.statusBarColor = GrayCustom.toArgb()
                    ViewCompat.getWindowInsetsController(activity.window.decorView)?.isAppearanceLightStatusBars = GrayCustom.luminance() > 0.5
                }
            }
            content()
        }
    )
}

object AppTheme {
    val dimens: Dimensions
        @Composable
        get() = LocalAppDimens.current
}
