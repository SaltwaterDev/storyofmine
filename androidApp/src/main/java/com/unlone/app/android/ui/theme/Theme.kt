package com.unlone.app.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorScheme = darkColors(
    primary = Color(0xFFF8EDE3),
    secondary = PurpleGrey80,
)

private val LightColorScheme = lightColors(
    primary = PrimaryGray,
    secondary = PurpleGrey40,
    background = Color(0xFFDFD3C3),
    surface = Color(0xFFF8EDE3),
    onPrimary = Color.White,
    onSecondary = Color.White,
    secondaryVariant = Color(0xFF6750A4),
//    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
)

@Composable
fun UnloneTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        val systemUiController = rememberSystemUiController()
        SideEffect {
//            (view.context as Activity).window.statusBarColor = colorScheme.primary.toArgb()
//            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkTheme
            if (darkTheme) {
//                systemUiController.setSystemBarsColor(
//                    color = colorScheme.background
//                )
                systemUiController.setNavigationBarColor(color = Color.Transparent)
            } else {
                systemUiController.setStatusBarColor(
                    color = Color.Transparent,
                    darkIcons = true,
                )
                systemUiController.setNavigationBarColor(
                    color = Color.Transparent,
//                    darkIcons = true,
                )
            }
        }
    }

    MaterialTheme(
        colors = colorScheme,
        typography = Typography,
        content = content
    )
}