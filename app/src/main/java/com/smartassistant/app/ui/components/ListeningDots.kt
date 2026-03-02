package com.smartassistant.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Listening Dots Animation
 *
 * Animated dots indicator shown during voice recognition.
 * Uses white color for visibility on dark overlay.
 */
@Composable
fun ListeningDots(
    modifier: Modifier = Modifier
) {
    var step by remember { mutableStateOf(0) }

    // loop animation
    LaunchedEffect(Unit) {
        while (true) {
            step = (step + 1) % 3
            delay(300)
        }
    }

    val dots = listOf(".", ".", ".")

    Text(
        text = dots.mapIndexed { index, dot ->
            if (index == step) "●" else "•"
        }.joinToString(" "),
        fontSize = 20.sp,
        color = Color.White,  // White for visibility on dark overlay
        modifier = modifier
    )
}
