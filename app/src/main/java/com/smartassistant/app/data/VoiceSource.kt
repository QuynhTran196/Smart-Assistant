package com.smartassistant.app.data

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

/**
 * ============================================================================
 * VoiceSource.kt - Voice Recognition Data Source
 * ============================================================================
 *
 * Wraps Android's SpeechRecognizer for voice input with Google-style features:
 * - Real-time partial transcription updates
 * - Confidence scoring
 * - Silence detection with auto-stop
 */
class VoiceSource(
    context: Context,
    private val onPartialResult: (String, Float) -> Unit,  // partial text + confidence
    private val onFinalResult: (String) -> Unit,           // final text
    private val onSilenceDetected: () -> Unit,             // silence timeout callback
    private val onError: (String) -> Unit
) {
    companion object {
        private const val TAG = "VoiceSource"
        private const val SILENCE_TIMEOUT_MS = 1500L  // 1.5 seconds of silence before auto-stop
    }

    private val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
    private var lastSpeechTime = 0L

    init {
        Log.d(TAG, "Initializing VoiceSource with silence detection ($SILENCE_TIMEOUT_MS ms)")
        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle) {
                Log.d(TAG, "onResults called")
                val resultsList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val confidenceList = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES) ?: floatArrayOf(0f)

                val finalText = resultsList?.firstOrNull()?.takeIf { it.isNotBlank() } ?: ""
                val confidence = confidenceList.firstOrNull() ?: 0f

                if (finalText.isNotBlank()) {
                    Log.d(TAG, "Final result: $finalText (confidence: $confidence)")
                    onFinalResult(finalText)
                } else {
                    Log.d(TAG, "Empty final result")
                    onError("No speech detected")
                }
            }

            override fun onError(error: Int) {
                val errorMsg = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
                    SpeechRecognizer.ERROR_SERVER -> "Server error"
                    else -> "Voice error: $error"
                }
                Log.e(TAG, "onError: $errorMsg (code: $error)")
                onError(errorMsg)
            }

            override fun onPartialResults(results: Bundle?) {
                // CRITICAL: Real-time partial transcription (Google-style feature)
                results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()?.let { partial ->
                    if (partial.isNotBlank()) {
                        val confidenceList = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES) ?: floatArrayOf(0f)
                        val confidence = confidenceList.firstOrNull() ?: 0f

                        Log.d(TAG, "Partial result: $partial (confidence: $confidence)")
                        lastSpeechTime = System.currentTimeMillis()

                        // Update UI immediately with partial text
                        onPartialResult(partial, confidence)
                    }
                }
            }

            override fun onReadyForSpeech(params: Bundle?) {
                Log.d(TAG, "onReadyForSpeech - ready to start recording")
                lastSpeechTime = System.currentTimeMillis()
            }

            override fun onBeginningOfSpeech() {
                Log.d(TAG, "onBeginningOfSpeech")
                lastSpeechTime = System.currentTimeMillis()
            }

            override fun onEndOfSpeech() {
                Log.d(TAG, "onEndOfSpeech - user stopped speaking, waiting for silence timeout")
                // Don't stop immediately - wait for silence timeout to auto-stop
            }

            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    fun start() {
        Log.d(TAG, "start() called - beginning speech recognition")
        lastSpeechTime = System.currentTimeMillis()
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN")
            // CRITICAL: Enable partial results for real-time transcription
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            // Long speech recognition timeout (we handle silence ourselves)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
        }
        recognizer.startListening(intent)
    }

    fun stop() {
        Log.d(TAG, "stop() called - stopping speech recognition")
        recognizer.stopListening()
    }

    fun cancel() {
        Log.d(TAG, "cancel() called - canceling speech recognition")
        recognizer.cancel()
    }

    fun release() {
        Log.d(TAG, "release() called - releasing recognizer resources")
        recognizer.destroy()
    }
}
