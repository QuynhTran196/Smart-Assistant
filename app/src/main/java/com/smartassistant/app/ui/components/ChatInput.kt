package com.smartassistant.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import android.util.Log
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.smartassistant.app.data.voice.VoiceInputState
import com.smartassistant.app.ui.theme.BarlowFontFamily
import com.smartassistant.app.ui.theme.SmartAssistantTheme
import com.smartassistant.app.ui.theme.InputFieldShape

@Composable
fun ChatInput(
    modifier: Modifier = Modifier,
    isGenerating: Boolean,
    voiceState: VoiceInputState,
    voiceText: String?,
    onVoiceConsumed: () -> Unit,
    onStop: () -> Unit,
    onSendMessage: (String) -> Unit,
    onAddClicked: () -> Unit,
    onStartVoice: () -> Unit,
    onStopVoice: () -> Unit,
    onCancelVoice: () -> Unit,
) {
    var inputText by rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(voiceState.partialText, voiceState.finalText) {
        if (voiceState.partialText.isNotBlank()) {
            inputText = voiceState.partialText
        } else if (voiceState.finalText.isNotBlank()) {
            inputText = voiceState.finalText
            onVoiceConsumed()
        }
    }

    LaunchedEffect(voiceState.finalText, voiceState.isListening) {
        if (!voiceState.isListening && voiceState.finalText.isBlank() && inputText.isNotBlank()) {
            Log.d("ChatInput", "Clearing TextField after voice message send")
            inputText = ""
            keyboardController?.hide()
        }
    }

    val inputAction = when {
        isGenerating -> InputAction.STOP
        voiceState.isListening -> InputAction.MIC
        inputText.isNotBlank() -> InputAction.SEND
        else -> InputAction.MIC
    }

    val extendedColors = SmartAssistantTheme.extendedColors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = InputFieldShape,
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
                .semantics(mergeDescendants = false) {
                    contentDescription = "chat_input_field"
                },
            shape = InputFieldShape,
            tonalElevation = 2.dp,
            color = extendedColors.inputBarBackground
        ) {

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics(mergeDescendants = false) {
                        contentDescription = "text_input"
                    },
                value = inputText,
                onValueChange = { newValue ->
                    // Direct assignment - state update isolated to this component
                    inputText = newValue
                },
                placeholder = {
                    Text(
                        text = "Ask me anything…",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontFamily = BarlowFontFamily,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                singleLine = true,
                shape = InputFieldShape,

                leadingIcon = {
                    IconButton(
                        onClick = onAddClicked,
                        modifier = Modifier
                            .size(40.dp)
                            .semantics(mergeDescendants = true) {
                                contentDescription = "chat_add_button"
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = extendedColors.iconTint,
                            modifier = Modifier
                                .size(22.dp)
                        )
                    }
                },

                trailingIcon = {
                    when (inputAction) {

                        InputAction.MIC -> {
                            VoiceRecordButton(
                                isListening = voiceState.isListening,
                                onMicClick = {
                                    if (voiceState.isListening) onStopVoice()
                                    else onStartVoice()
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .semantics(mergeDescendants = true) {
                                        contentDescription = "chat_mic_button"
                                    },
                            )
                        }

                        InputAction.SEND -> {
                            IconButton(
                                onClick = {
                                    if (inputText.isNotBlank()) {
                                        onSendMessage(inputText)
                                        inputText = ""
                                        keyboardController?.hide()
                                    }
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .semantics(mergeDescendants = true) {
                                        contentDescription = "send_button"
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        InputAction.STOP -> {
                            IconButton(
                                onClick = onStop,
                                modifier = Modifier
                                    .size(36.dp)
                                    .semantics(mergeDescendants = true) {
                                        contentDescription = "stop_button"
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Stop,
                                    contentDescription = "Stop",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                },

                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                textStyle = androidx.compose.material3.LocalTextStyle.current.copy(
                    fontSize = 15.sp,
                    fontFamily = BarlowFontFamily,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}


