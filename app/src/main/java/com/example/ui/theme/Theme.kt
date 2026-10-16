package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun LivlinkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isDriverRole: Boolean = false,
    content: @Composable () -> Unit
) {
    val primaryColor = if (isDriverRole) DriverOrangePrimary else ShopBluePrimary
    val primaryDark = if (isDriverRole) DriverOrangeDark else ShopBlueDark
    val primaryLight = if (isDriverRole) DriverOrangeLight else ShopBlueLight

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = primaryColor,
            onPrimary = Color.White,
            primaryContainer = primaryDark,
            onPrimaryContainer = Color.White,
            secondary = NavySecondary,
            onSecondary = TextPrimaryDark,
            background = BackgroundDark,
            surface = SurfaceDark,
            onBackground = TextPrimaryDark,
            onSurface = TextPrimaryDark,
            surfaceVariant = NavySecondary,
            onSurfaceVariant = TextSecondaryDark,
            outline = Color(0xFF334155),
            outlineVariant = Color(0xFF1E293B)
        )
    } else {
        lightColorScheme(
            primary = primaryColor,
            onPrimary = Color.White,
            primaryContainer = primaryLight,
            onPrimaryContainer = if (isDriverRole) DriverOrangeDark else ShopBlueDark,
            secondary = NavyPrimary,
            onSecondary = Color.White,
            background = BackgroundLight,
            surface = SurfaceLight,
            onBackground = TextPrimaryLight,
            onSurface = TextPrimaryLight,
            surfaceVariant = primaryLight,
            onSurfaceVariant = TextSecondaryLight,
            outline = Color(0xFFCBD5E1),
            outlineVariant = Color(0xFFE2E8F0)
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = NavyPrimary.toArgb()
            window.navigationBarColor = NavyPrimary.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = false
            insetsController.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
