package com.smartassistant.app.data

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

// Import models from Models.kt (same package, so auto-imported)

/**
 * ============================================================================
 * ChatRepository.kt - Single Source of Truth for Chat Data
 * ============================================================================
 *
 * Manages all chat data: sessions, messages, history.
 * Now includes local persistence to survive app kills.
 */
class ChatRepository(
    private val aiSource: AiSource,
    private val localStorage: ChatLocalStorage? = null
) {

    companion object {
        private const val TAG = "ChatRepository"
    }

    // Session history for drawer
    private val _sessions = MutableStateFlow<List<Session>>(emptyList())
    val sessions: StateFlow<List<Session>> = _sessions

    init {
        // Don't load from local storage here - getCache() is the single source of truth.
        // The ViewModel's refreshHistoryFromCache() will populate sessions after AI initializes.
    }

    /**
     * Populate session history from the AI backend's getCache().
     * Only called after AI initialization completes.
     */
    fun restoreSessionsFromCache() {
        val cached = aiSource.getCache()
        Log.d(TAG, "Loading ${cached.size} sessions from AI cache into history drawer")
        // Clear stale local data first
        _sessions.value = emptyList()
        cached.forEach { cache ->
            saveSession(Session(
                id = cache.sessionId.toString(),
                title = "Smart Assistant",
                lastMessage = cache.queries.lastOrNull()
            ))
        }
    }

    /** Build message list from cached queries/responses */
    fun buildMessagesFromCache(queries: List<String>, responses: List<String>): List<Message> {
        val count = minOf(queries.size, responses.size)
        return buildList {
            for (i in 0 until count) {
                add(Message(UUID.randomUUID().toString(), queries[i], Sender.USER))
                add(Message(UUID.randomUUID().toString(), responses[i], Sender.AI))
            }
        }
    }

    // Session management
    fun createSession() = aiSource.createSession()
    fun destroySession(sessionId: Int) = aiSource.destroySession(sessionId)

    /** Check if AI backend is ready */
    fun isReady(): Boolean = aiSource.isReady()

    fun saveSession(session: Session) {
        _sessions.value = _sessions.value.filterNot { it.id == session.id } + session
    }

    fun deleteSession(sessionId: String) {
        _sessions.value = _sessions.value.filterNot { it.id == sessionId }
        sessionId.toIntOrNull()?.let { aiSource.destroySession(it) }
    }

    // Messaging
    fun registerTokenListener(sessionId: Int, onToken: (String) -> Unit) =
        aiSource.registerTokenListener(sessionId, onToken)

    fun sendMessage(sessionId: Int, message: String) =
        aiSource.sendMessage(sessionId, message)

    fun stopGeneration(sessionId: Int) =
        aiSource.stopGeneration(sessionId)

    // Helpers
    fun createUserMessage(text: String) = Message(
        id = UUID.randomUUID().toString(),
        text = text,
        sender = Sender.USER
    )

    fun createAiPlaceholder() = Message(
        id = UUID.randomUUID().toString(),
        text = "",
        sender = Sender.AI,
        state = MessageState.TYPING,
        isStreaming = true
    )

    fun getCache() = aiSource.getCache()

    // ==================== LOCAL PERSISTENCE ====================

    /**
     * Save sessions to local storage for persistence across app kills
     */
    fun saveSessionsToLocal(sessions: List<ChatSessionState>) {
        localStorage?.saveSessions(sessions)
    }

    /**
     * Load sessions from local storage (returns empty if none saved)
     */
    fun loadSessionsFromLocal(): List<ChatSessionState> {
        return localStorage?.loadSessions() ?: emptyList()
    }

    /**
     * Clear all local storage and in-memory session history.
     * Called when getCache() returns empty (database was cleared).
     */
    fun clearLocalStorage() {
        localStorage?.clearAll()
        _sessions.value = emptyList()
        Log.d(TAG, "Cleared local storage and session history")
    }
}
