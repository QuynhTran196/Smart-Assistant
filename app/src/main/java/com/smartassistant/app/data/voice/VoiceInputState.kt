package com.smartassistant.app.data.voice

/**
 * VoiceInputState - State for voice input/speech recognition feature.
 *
 * MVVM Role: Part of the MODEL layer (Data State).
 *
 * This data class tracks the current state of voice recognition with Google-style
 * features like partial transcription, confidence scoring, and silence detection.
 *
 * The ViewModel exposes this as a StateFlow, and the UI observes it to show:
 * - Real-time partial transcription
 * - Confidence/volume indicator
 * - Listening state
 * - Silence detection countdown
 *
 * @property isListening Whether microphone is actively recording
 * @property partialText Partial transcription updated in real-time
 * @property finalText Final transcription after recording stops
 * @property confidence Confidence score (0-1) of the recognition
 * @property silenceTimeoutMs Remaining time before auto-stop due to silence
 * @property error Any error message from speech recognizer
 */
data class VoiceInputState(
    /**
     * Indicates if speech recognition is currently active and recording.
     * UI: Shows listening animation/overlay when true.
     */
    val isListening: Boolean = false,

    /**
     * Partial text recognized from user's speech in real-time.
     * Updated continuously during recognition (like Google Assistant).
     * Shown in TextField immediately as user speaks.
     * UI: Display in input field for immediate feedback.
     */
    val partialText: String = "",

    /**
     * Final transcribed text after recording completes or silence detected.
     * Ready to be sent to chat.
     * UI: Can be used for final display if needed.
     */
    val finalText: String = "",

    /**
     * Confidence score of the speech recognition (0.0 to 1.0).
     * Higher = more confident in the transcription.
     * UI: Can display visual confidence indicator.
     */
    val confidence: Float = 0f,

    /**
     * Remaining milliseconds before auto-stop due to silence.
     * Starts at 1500-2000ms when speech ends, counts down.
     * When reaches 0, recording stops and message auto-sends.
     * UI: Can display countdown or visual indicator.
     */
    val silenceTimeoutMs: Long = 0L,

    /**
     * Error message if speech recognition failed.
     * Null when no error has occurred.
     * UI: Display error message to user (e.g., "No speech detected").
     */
    val error: String? = null
)


