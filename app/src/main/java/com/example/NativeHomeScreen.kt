package com.example

import androidx.compose.runtime.Composable

/**
 * NativeHomeScreen forwards directly to NativeHomeScreenNew to preserve backward compatibility
 * and eliminate duplicate code while unifying the LIVLINK experience.
 */
@Composable
fun NativeHomeScreen(
    isDarkMode: Boolean = false,
    onToggleDarkMode: () -> Unit = {}
) {
    NativeHomeScreenNew(
        isDarkMode = isDarkMode,
        onToggleDarkMode = onToggleDarkMode
    )
}
