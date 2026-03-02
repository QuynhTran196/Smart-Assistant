package com.smartassistant.app.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.smartassistant.app.data.*
import com.smartassistant.app.data.voice.VoiceInputState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ============================================================================
 * ChatViewModel.kt - Main Chat ViewModel
 * ============================================================================
 *
 * Handles all chat business logic: sessions, messages, voice, streaming.
 */
class ChatViewModel(private val repository: ChatRepository) : ViewModel() {

    companion object {
        private const val TAG = "ChatViewModel"
        private const val STREAM_FINISH_DELAY = 700L

        // ==================== VOICE CONFIGURATION ====================
        // Configurable silence timeout - 2.5 seconds allows natural pauses
        private const val SILENCE_TIMEOUT_MS = 2500L
        private const val SILENCE_CHECK_INTERVAL_MS = 50L
    }

    // UI State
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _voiceState = MutableStateFlow(VoiceInputState())
    val voiceState: StateFlow<VoiceInputState> = _voiceState.asStateFlow()

    // Internal
    private var streamingMessageId: String? = null
    private var finishJob: Job? = null
    private var voiceSource: VoiceSource? = null
    private var silenceDetectionJob: Job? = null  // For silence timeout handling
    private var lastSpeechTime: Long = 0L  // Track last speech time for silence detection

    init {
        // Load saved sessions into history (for drawer) but don't display them
        loadHistoryInBackground()
        // Always start with a fresh empty chat screen
        createFreshChat()
    }

    /**
     * Load saved sessions for history drawer (doesn't affect current view)
     */
    private fun loadHistoryInBackground() {
        val localSessions = repository.loadSessionsFromLocal()
        if (localSessions.isNotEmpty()) {
            Log.d(TAG, "Loaded ${localSessions.size} sessions into history")
            // Mark old LLM session IDs as invalid (-1) since they won't be valid after app restart
            val sessionsWithInvalidLlm = localSessions.map { session ->
                session.copy(llmSessionId = -1)
            }
            _uiState.update { it.copy(sessions = sessionsWithInvalidLlm) }
        }
    }

    /**
     * Create a fresh empty chat screen (no LLM session created yet - lazy initialization)
     * This is a "pending" session that only becomes real when user sends first message
     */
    private fun createFreshChat() {
        val pendingSession = ChatSessionState(
            id = "pending_${System.currentTimeMillis()}",
            title = "Smart Assistant",
            llmSessionId = -1,  // Not created yet - will be created on first message
            messages = emptyList(),
            showWelcome = true
        )
        _uiState.update { state ->
            // Don't add pending session to saved sessions list, just make it current
            state.copy(
                sessions = state.sessions.filterNot { it.id.startsWith("pending_") } + pendingSession,
                currentSessionId = pendingSession.id
            )
        }
        Log.d(TAG, "Created fresh chat screen: ${pendingSession.id}")
    }

    /**
     * Save current sessions to local storage for persistence
     * Only saves sessions that have actual messages (not pending/empty ones)
     */
    private fun saveToLocalStorage() {
        // Filter out pending/empty sessions before saving
        val sessionsToSave = _uiState.value.sessions.filter {
            !it.id.startsWith("pending_") && it.messages.isNotEmpty()
        }
        repository.saveSessionsToLocal(sessionsToSave)
        Log.d(TAG, "Saved ${sessionsToSave.size} sessions to local storage")
    }

    // ==================== VOICE - GOOGLE STYLE WITH SILENCE DETECTION ====================

    fun initVoice(context: Context) {
        voiceSource = VoiceSource(
            context = context,
            // Partial results callback - update UI in real-time with single state update
            onPartialResult = { partial, confidence ->
                // Batch state update to avoid multiple recompositions
                _voiceState.update {
                    it.copy(
                        partialText = partial,
                        confidence = confidence,
                        silenceTimeoutMs = SILENCE_TIMEOUT_MS  // Reset countdown display
                    )
                }
                // Reset silence timer for auto-stop detection
                lastSpeechTime = System.currentTimeMillis()
            },
            // Final result callback - called when recognition completes
            onFinalResult = { finalText ->
                if (finalText.isNotBlank()) {
                    // Auto-send on silence detection (no manual send needed)
                    autoSendVoiceMessage(finalText)
                }
            },
            // Silence detection callback - user stopped speaking
            onSilenceDetected = {
                stopVoice()
            },
            onError = { error ->
                _voiceState.update { it.copy(error = error, isListening = false) }
                stopVoice()
            }
        )
    }

    fun startVoice() {
        if (_voiceState.value.isListening) return
        if (voiceSource == null) return

        // Batch all state updates into single operation to avoid multiple recompositions
        _voiceState.update {
            it.copy(
                isListening = true,
                error = null,
                partialText = "",
                finalText = "",
                confidence = 0f,
                silenceTimeoutMs = 0L
            )
        }
        lastSpeechTime = System.currentTimeMillis()
        voiceSource?.start()

        // Start silence detection timeout monitoring
        startSilenceDetectionMonitor()
    }

    fun stopVoice() {
        if (!_voiceState.value.isListening) return

        silenceDetectionJob?.cancel()
        silenceDetectionJob = null

        _voiceState.update { it.copy(isListening = false) }
        voiceSource?.stop()
    }

    fun cancelVoice() {
        silenceDetectionJob?.cancel()
        silenceDetectionJob = null

        _voiceState.update { it.copy(isListening = false) }
        voiceSource?.cancel()
    }

    /**
     * Start monitoring for silence to auto-stop recording
     * 2.5 seconds of silence = natural pause point in conversation
     */
    private fun startSilenceDetectionMonitor() {
        silenceDetectionJob?.cancel()
        silenceDetectionJob = viewModelScope.launch {
            while (_voiceState.value.isListening) {
                delay(SILENCE_CHECK_INTERVAL_MS)

                val timeSinceLastSpeech = System.currentTimeMillis() - lastSpeechTime
                val remainingTimeMs = (SILENCE_TIMEOUT_MS - timeSinceLastSpeech).coerceAtLeast(0L)

                // Update UI with countdown - shows user how long before auto-stop
                _voiceState.update { it.copy(silenceTimeoutMs = remainingTimeMs) }

                // Auto-stop when silence timeout reached
                if (timeSinceLastSpeech >= SILENCE_TIMEOUT_MS && _voiceState.value.isListening) {
                    stopVoice()
                    break
                }
            }
        }
    }

    /**
     * Auto-send voice message without requiring manual send action (Google-style)
     * Optimized for smooth performance with minimal state updates
     */
    private fun autoSendVoiceMessage(text: String) {
        // CRITICAL: Combine all state updates into ONE to avoid multiple recompositions
        _voiceState.update {
            it.copy(
                finalText = text,
                partialText = text,
                confidence = 0f,
                silenceTimeoutMs = 0L
            )
        }

        // Auto-send message immediately (no delays for better responsiveness)
        sendMessage(text)

        // Clear state after send (no additional delays)
        viewModelScope.launch {
            // Minimal delay for state reset
            delay(50)  // Reduced from 400ms total
            _voiceState.update {
                it.copy(
                    finalText = "",
                    partialText = "",
                    confidence = 0f,
                    silenceTimeoutMs = 0L
                )
            }
        }
    }

    // ==================== SESSION ====================

    /**
     * Ensure current session has a valid LLM session ID.
     * If the session was created before AI was ready (llmSessionId = -1),
     * this will try to create a valid session now.
     * Returns true if session is valid, false otherwise.
     */
    private fun ensureValidSession(): Boolean {
        val session = _uiState.value.currentSession ?: return false

        // If session has invalid ID (-1), try to recreate it now
        if (session.llmSessionId < 0) {
            if (!repository.isReady()) {
                Log.w(TAG, "AI not ready yet, cannot validate session")
                return false
            }

            // Create a new valid session
            val newLlmId = repository.createSession()
            if (newLlmId < 0) {
                Log.e(TAG, "Failed to create valid session")
                return false
            }

            // Update the session with valid LLM ID
            repository.registerTokenListener(newLlmId, ::onToken)
            _uiState.update { state ->
                state.copy(
                    sessions = state.sessions.map {
                        if (it.id == session.id) it.copy(llmSessionId = newLlmId) else it
                    }
                )
            }
            Log.d(TAG, "Re-validated session ${session.id} with new LLM ID: $newLlmId")
        }
        return true
    }

    fun createNewChat() {
        stopGeneration()
        // Just create a fresh empty chat - LLM session created on first message
        createFreshChat()
    }

    fun removeChat(sessionId: String) {
        stopGeneration()
        val session = _uiState.value.sessions.find { it.id == sessionId }

        // Only destroy LLM session if it was actually created (not pending)
        if (session != null && session.llmSessionId >= 0) {
            repository.destroySession(session.llmSessionId)
        }

        val remaining = _uiState.value.sessions.filterNot { it.id == sessionId }

        _uiState.update { it.copy(sessions = remaining, currentSessionId = null) }
        repository.deleteSession(sessionId)

        // Save to local storage after removing chat
        saveToLocalStorage()

        // Always create a fresh chat after removing
        createFreshChat()
    }

    fun selectSession(sessionId: String) {
        stopGeneration()
        _uiState.update { it.copy(currentSessionId = sessionId) }
    }

    // ==================== MESSAGING ====================

    fun sendMessage(text: String) {
        var session = _uiState.value.currentSession ?: return

        // If this is a pending session, convert it to a real session now
        if (session.id.startsWith("pending_")) {
            val newSessionId = convertPendingToRealSession(session)
            if (newSessionId == null) {
                Log.w(TAG, "Cannot send message: failed to create session")
                return
            }
            // Re-fetch the updated session
            session = _uiState.value.currentSession ?: return
        }

        // Ensure session has valid LLM ID
        if (!ensureValidSession()) {
            Log.w(TAG, "Cannot send message: session not valid and AI not ready")
            return
        }

        // Re-fetch session after possible update from ensureValidSession
        session = _uiState.value.currentSession ?: return

        val userMsg = repository.createUserMessage(text)
        val aiMsg = repository.createAiPlaceholder()
        streamingMessageId = aiMsg.id

        _uiState.update { s ->
            s.copy(
                sessions = s.sessions.map {
                    if (it.id == session.id) it.copy(
                        showWelcome = false,
                        messages = it.messages + userMsg + aiMsg
                    ) else it
                },
                isGenerating = true
            )
        }

        repository.saveSession(Session(session.id, session.title, text))
        repository.sendMessage(session.llmSessionId, text)

        // Save to local storage for persistence
        saveToLocalStorage()
    }

    /**
     * Convert a pending session to a real session with LLM backend
     * Returns the new session ID or null if failed
     */
    private fun convertPendingToRealSession(pendingSession: ChatSessionState): String? {
        val llmId = repository.createSession()
        if (llmId < 0 && !repository.isReady()) {
            Log.w(TAG, "AI not ready, cannot create session")
            return null
        }

        val newId = if (llmId >= 0) llmId.toString() else java.util.UUID.randomUUID().toString()

        if (llmId >= 0) {
            repository.registerTokenListener(llmId, ::onToken)
        }

        // Replace the pending session with the real one
        _uiState.update { state ->
            state.copy(
                sessions = state.sessions.map {
                    if (it.id == pendingSession.id) it.copy(id = newId, llmSessionId = llmId) else it
                },
                currentSessionId = newId
            )
        }

        Log.d(TAG, "Converted pending session to real: $newId, llmId: $llmId")
        return newId
    }

    fun stopGeneration() {
        val session = _uiState.value.currentSession ?: return
        repository.stopGeneration(session.llmSessionId)
        streamingMessageId = null
        _uiState.update { it.copy(isGenerating = false) }
    }

    private fun onToken(token: String) {
        val msgId = streamingMessageId ?: return
        finishJob?.cancel()

        _uiState.update { state ->
            state.copy(sessions = state.sessions.map { session ->
                if (session.id == state.currentSessionId) {
                    session.copy(messages = session.messages.map { msg ->
                        if (msg.id == msgId) msg.copy(text = msg.text + token, state = MessageState.STREAMING)
                        else msg
                    })
                } else session
            })
        }

        finishJob = viewModelScope.launch {
            delay(STREAM_FINISH_DELAY)
            val finishedId = streamingMessageId
            streamingMessageId = null
            _uiState.update { state ->
                state.copy(
                    isGenerating = false,
                    sessions = state.sessions.map { session ->
                        if (session.id == state.currentSessionId) {
                            session.copy(messages = session.messages.map { msg ->
                                if (msg.id == finishedId) msg.copy(state = MessageState.DONE, isStreaming = false)
                                else msg
                            })
                        } else session
                    }
                )
            }
            // Save completed response to local storage
            saveToLocalStorage()
        }
    }

    // ==================== UI ====================

    fun showAddDialog() = _uiState.update { it.copy(showAddDialog = true) }
    fun hideAddDialog() = _uiState.update { it.copy(showAddDialog = false) }

    // ==================== FILE ATTACHMENTS ====================

    /**
     * Add a file attachment to pending attachments
     */
    fun addAttachment(attachment: FileAttachment) {
        Log.d(TAG, "Adding attachment: ${attachment.name}")
        val session = _uiState.value.currentSession ?: return

        _uiState.update { state ->
            state.copy(
                sessions = state.sessions.map {
                    if (it.id == session.id) {
                        it.copy(pendingAttachments = it.pendingAttachments + attachment)
                    } else it
                }
            )
        }
    }

    /**
     * Remove a pending attachment
     */
    fun removeAttachment(attachmentId: String) {
        Log.d(TAG, "Removing attachment: $attachmentId")
        val session = _uiState.value.currentSession ?: return

        _uiState.update { state ->
            state.copy(
                sessions = state.sessions.map {
                    if (it.id == session.id) {
                        it.copy(pendingAttachments = it.pendingAttachments.filter { att -> att.id != attachmentId })
                    } else it
                }
            )
        }
    }

    /**
     * Clear all pending attachments
     */
    fun clearAttachments() {
        val session = _uiState.value.currentSession ?: return

        _uiState.update { state ->
            state.copy(
                sessions = state.sessions.map {
                    if (it.id == session.id) {
                        it.copy(pendingAttachments = emptyList())
                    } else it
                }
            )
        }
    }

    /**
     * Send message with attachments
     */
    fun sendMessageWithAttachments(text: String) {
        var session = _uiState.value.currentSession ?: return

        // If this is a pending session, convert it to a real session now
        if (session.id.startsWith("pending_")) {
            val newSessionId = convertPendingToRealSession(session)
            if (newSessionId == null) {
                Log.w(TAG, "Cannot send message: failed to create session")
                return
            }
            // Re-fetch the updated session
            session = _uiState.value.currentSession ?: return
        }

        // Ensure session has valid LLM ID
        if (!ensureValidSession()) {
            Log.w(TAG, "Cannot send message: session not valid and AI not ready")
            return
        }

        // Re-fetch session after possible update from ensureValidSession
        session = _uiState.value.currentSession ?: return
        val attachments = session.pendingAttachments

        // Create message with attachments
        val userMsg = Message(
            id = java.util.UUID.randomUUID().toString(),
            text = text,
            sender = Sender.USER,
            attachments = attachments
        )
        val aiMsg = repository.createAiPlaceholder()
        streamingMessageId = aiMsg.id

        _uiState.update { s ->
            s.copy(
                sessions = s.sessions.map {
                    if (it.id == session.id) it.copy(
                        showWelcome = false,
                        messages = it.messages + userMsg + aiMsg,
                        pendingAttachments = emptyList() // Clear after sending
                    ) else it
                },
                isGenerating = true
            )
        }

        // Build message text with attachment info
        val messageText = if (attachments.isNotEmpty()) {
            val attachmentInfo = attachments.joinToString("\n") { "[Attached: ${it.name}]" }
            if (text.isNotBlank()) "$text\n\n$attachmentInfo" else attachmentInfo
        } else {
            text
        }

        repository.saveSession(Session(session.id, session.title, text.ifBlank { "Sent ${attachments.size} file(s)" }))
        repository.sendMessage(session.llmSessionId, messageText)

        // Save to local storage for persistence
        saveToLocalStorage()
    }

    override fun onCleared() {
        super.onCleared()
        // Save sessions before clearing to ensure persistence (excluding pending)
        saveToLocalStorage()
        _uiState.value.sessions.forEach {
            if (it.llmSessionId >= 0) {
                repository.destroySession(it.llmSessionId)
            }
        }
        voiceSource?.release()
    }
}

/**
 * ============================================================================
 * HistoryViewModel.kt - History Drawer ViewModel
 * ============================================================================
 */
class HistoryViewModel(private val repository: ChatRepository) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = repository.sessions
        .map { HistoryUiState(sessions = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HistoryUiState())

    fun deleteSession(sessionId: String) = repository.deleteSession(sessionId)
}

/**
 * Factory for ViewModels
 */
class ViewModelFactory(private val repository: ChatRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(ChatViewModel::class.java) -> ChatViewModel(repository) as T
        modelClass.isAssignableFrom(HistoryViewModel::class.java) -> HistoryViewModel(repository) as T
        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
