package com.example.app.android.ui.theme

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
    onPrimary = Color.White,
    onSecondary = Color.White,
    secondaryVariant = Color(0xFF6750A4),
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
)

@Composable
fun MyStoriesTheme(
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
            if (darkTheme) {
//                )
                systemUiController.setNavigationBarColor(color = Color.Transparent)
            } else {
                systemUiController.setNavigationBarColor(
                    color = Color.Transparent,
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