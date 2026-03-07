package com.smartassistant.app.ui.theme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit

/**
 * Theme State Manager
 *
 * Manages the app's dark mode preference with persistence using SharedPreferences.
 * Provides a clean interface for toggling between light and dark themes.
 */

private const val PREFS_NAME = "smart_assistant_theme_prefs"
private const val KEY_DARK_MODE = "dark_mode_enabled"

/**
 * Theme state holder that persists preference
 */
class ThemeState(
    context: Context,
    initialDarkMode: Boolean
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isDarkMode by mutableStateOf(initialDarkMode)
        private set

    fun toggleDarkMode() {
        isDarkMode = !isDarkMode
        saveDarkModePreference(isDarkMode)
    }

    fun updateDarkMode(enabled: Boolean) {
        isDarkMode = enabled
        saveDarkModePreference(enabled)
    }

    private fun saveDarkModePreference(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_DARK_MODE, enabled) }
    }
}

/**
 * CompositionLocal for accessing theme state throughout the app
 */
val LocalThemeState = staticCompositionLocalOf<ThemeState> {
    error("ThemeState not provided. Wrap your app with rememberThemeState.")
}

/**
 * Remember and create a ThemeState that persists dark mode preference
 *
 * @param defaultDarkMode Default value if no preference is saved (false = light mode)
 */
@Composable
fun rememberThemeState(defaultDarkMode: Boolean = false): ThemeState {
    val context = LocalContext.current
    return remember {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedDarkMode = prefs.getBoolean(KEY_DARK_MODE, defaultDarkMode)
        ThemeState(context, savedDarkMode)
    }
}
