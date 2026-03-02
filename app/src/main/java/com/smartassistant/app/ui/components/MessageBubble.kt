package com.smartassistant.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartassistant.app.data.Message
import com.smartassistant.app.data.MessageState
import com.smartassistant.app.data.Sender
import com.smartassistant.app.ui.theme.SmartAssistantTheme
import com.smartassistant.app.ui.theme.UserBubbleShape
import com.smartassistant.app.ui.theme.AiBubbleShape

/**
 * Message Bubble Component
 *
 * Displays a single chat message with:
 * - Asymmetrical rounded corners (flat corner toward sender side)
 * - Distinct colors for User vs AI messages
 * - Proper spacing and typography
 */
@Composable
fun MessageBubble(
    message: Message
) {
    val isUser = message.sender == Sender.USER
    val extendedColors = SmartAssistantTheme.extendedColors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = if (isUser) UserBubbleShape else AiBubbleShape,
            color = if (isUser) extendedColors.userBubble else extendedColors.aiBubble,
            tonalElevation = if (isUser) 0.dp else 1.dp,
            shadowElevation = if (isUser) 2.dp else 1.dp,
            modifier = Modifier
                .widthIn(max = 300.dp)
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                )
            ) {
                when (message.state) {
                    // AI is thinking → animated dots
                    MessageState.TYPING -> {
                        AiTypingIndicator()
                    }

                    // AI is streaming or message is complete
                    MessageState.STREAMING,
                    MessageState.DONE -> {
                        Text(
                            text = message.text,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 15.sp,
                                lineHeight = 22.sp
                            ),
                            color = if (isUser)
                                extendedColors.userBubbleText
                            else
                                extendedColors.aiBubbleText
                        )
                    }

                    // Fallback for other states
                    else -> {
                        Text(
                            text = message.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isUser)
                                extendedColors.userBubbleText
                            else
                                extendedColors.aiBubbleText,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
