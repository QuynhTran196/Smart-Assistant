package com.smartassistant.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * Voice Record Button
 *
 * Animated microphone button for voice input.
 * Uses theme colors for consistent styling.
 */
@Composable
fun VoiceRecordButton(
    modifier: Modifier = Modifier,
    isListening: Boolean,
    onMicClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant

    val scale by animateFloatAsState(
        targetValue = if (isListening) 1.15f else 1f,
        label = "mic-scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isListening) primaryColor else surfaceVariantColor,
        label = "mic-bg"
    )

    Box(
        modifier = modifier
            .size(64.dp)
            .clickable { onMicClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(backgroundColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Voice input",
                tint = if (isListening) onPrimaryColor else primaryColor,
                modifier = Modifier
                    .size(28.dp)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale
                    )
            )
        }
    }
}

