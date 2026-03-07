package com.smartassistant.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.smartassistant.app.data.AiSource
import com.smartassistant.app.ui.ChatScreen
import com.smartassistant.app.ui.theme.LocalThemeState
import com.smartassistant.app.ui.theme.SmartAssistantTheme
import com.smartassistant.app.ui.theme.rememberThemeState

/**
 * ============================================================================
 * MainActivity.kt - App Entry Point
 * ============================================================================
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeState = rememberThemeState()

            CompositionLocalProvider(LocalThemeState provides themeState) {
                SmartAssistantTheme(darkTheme = themeState.isDarkMode) {
                    App()
                }
            }
        }
    }
}

/**
 * App - Main composable that sets up AI source and displays chat screen
 * Uses LaunchedEffect to initialize AI in background, not blocking UI startup
 */
@Composable
fun App() {
    // Create AiSource immediately (lightweight object creation)
    val aiSource = remember { AiSource() }

    // Initialize AI backend in background coroutine - does not block UI
    // This is the key fix: AI initialization happens AFTER first frame renders
    LaunchedEffect(Unit) {
        // Run heavy initialization on background thread
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            aiSource.initialize()
            aiSource.loadProfile("guest")
        }
    }

    // Show chat screen immediately - it handles loading states internally
    ChatScreen(aiSource = aiSource)
}
