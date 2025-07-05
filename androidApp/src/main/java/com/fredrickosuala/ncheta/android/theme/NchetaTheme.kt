package com.fredrickosuala.ncheta.android.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


private val NchetaLightColorScheme = lightColorScheme(
    primary = NearBlack,
    onPrimary = PureWhite,
    secondary = MediumGray,
    onSecondary = PureWhite,
    secondaryContainer = LightGray,
    onSecondaryContainer = NearBlack,
    tertiary = LightGray,
    onTertiary = NearBlack,
    background = SubtleOffWhite,
    onBackground = NearBlack,
    surface = SubtleOffWhite,
    onSurface = NearBlack,
    surfaceVariant = LighterGray,
    onSurfaceVariant = MediumGray,
    error = Color(0xFFB00020),
    onError = Color.White,
    outline = LightGray
)

private val NchetaDarkColorScheme = darkColorScheme(
    primary = LightGray,
    onPrimary = NearBlack,
    secondary = MediumGray,
    onSecondary = NearBlack,
    tertiary = NearBlack,
    onTertiary = LightGray,
    background = NearBlack,
    onBackground = PureWhite,
    surface = Color(0xFF2F2F2F),
    onSurface = PureWhite,
    surfaceVariant = Color(0xFF3A3A3A),
    onSurfaceVariant = LightGray,
    error = Color(0xFFCF6679),
    onError = Color.Black,
    outline = MediumGray
)

@Composable
fun NchetaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colorScheme = NchetaLightColorScheme
    /*
    // Future dark theme implementation:
    val colorScheme = if (darkTheme) {
        NchetaDarkColorScheme
    } else {
        NchetaLightColorScheme
    }
    */

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb() // Or specific color
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            // Similarly for navigation bar if needed
            // window.navigationBarColor = colorScheme.background.toArgb()
            // WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NchetaTypography,
        shapes = MaterialTheme.shapes,
        content = content
    )
}