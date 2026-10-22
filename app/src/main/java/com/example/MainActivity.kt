package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ui.theme.LivlinkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = getSharedPreferences("livlink_prefs", Context.MODE_PRIVATE)
        val savedRole = prefs.getString("selected_role", "صاحب محل") ?: "صاحب محل"

        setContent {
            val systemDark = isSystemInDarkTheme()
            // الوضع الليلي كيتحفظ: إلا ما بدلتيهش، كنتبعو وضع الهاتف
            var isDarkMode by remember {
                mutableStateOf(if (prefs.contains("dark_mode")) prefs.getBoolean("dark_mode", systemDark) else systemDark)
            }
            var currentRole by remember { mutableStateOf(savedRole) }

            val isDriver = currentRole == "ليفرور"

            LivlinkTheme(
                darkTheme = isDarkMode,
                isDriverRole = isDriver
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NativeHomeScreenNew(
                        isDarkMode = isDarkMode,
                        onToggleDarkMode = {
                            isDarkMode = !isDarkMode
                            prefs.edit().putBoolean("dark_mode", isDarkMode).apply()
                        },
                        currentRole = currentRole,
                        onRoleChange = { newRole ->
                            currentRole = newRole
                            prefs.edit().putString("selected_role", newRole).apply()
                        }
                    )
                }
            }
        }
    }
}
