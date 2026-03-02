package com.smartassistant.app.data

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

/**
 * ============================================================================
 * ChatLocalStorage.kt - Local Persistence for Chat History
 * ============================================================================
 *
 * Persists chat sessions and messages to SharedPreferences.
 * Ensures chat history survives app kills/restarts.
 */
class ChatLocalStorage(context: Context) {

    companion object {
        private const val TAG = "ChatLocalStorage"
        private const val PREFS_NAME = "chat_history"
        private const val KEY_SESSIONS = "sessions"
    }

    // Use applicationContext to ensure SharedPreferences survives configuration changes
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Save all chat sessions to local storage
     * Only saves sessions that have actual messages and are not pending
     */
    fun saveSessions(sessions: List<ChatSessionState>) {
        try {
            val jsonArray = JSONArray()
            sessions.forEach { session ->
                // Only save sessions that:
                // 1. Have messages (not empty)
                // 2. Are not pending sessions (id doesn't start with "pending_")
                if (session.messages.isNotEmpty() && !session.id.startsWith("pending_")) {
                    val sessionJson = JSONObject().apply {
                        put("id", session.id)
                        put("title", session.title)
                        put("llmSessionId", session.llmSessionId)
                        put("showWelcome", session.showWelcome)
                        put("messages", messagesToJson(session.messages))
                    }
                    jsonArray.put(sessionJson)
                    Log.d(TAG, "Saving session ${session.id} with ${session.messages.size} messages")
                }
            }
            // Use commit() for synchronous write - ensures data is saved before app kill
            val success = prefs.edit().putString(KEY_SESSIONS, jsonArray.toString()).commit()
            Log.d(TAG, "Saved ${jsonArray.length()} sessions to local storage, success: $success")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save sessions: ${e.message}")
        }
    }

    /**
     * Load all chat sessions from local storage
     */
    fun loadSessions(): List<ChatSessionState> {
        return try {
            val json = prefs.getString(KEY_SESSIONS, null) ?: return emptyList()
            val jsonArray = JSONArray(json)
            val sessions = mutableListOf<ChatSessionState>()

            for (i in 0 until jsonArray.length()) {
                val sessionJson = jsonArray.getJSONObject(i)
                sessions.add(ChatSessionState(
                    id = sessionJson.getString("id"),
                    title = sessionJson.getString("title"),
                    llmSessionId = sessionJson.getInt("llmSessionId"),
                    showWelcome = sessionJson.optBoolean("showWelcome", true),
                    messages = messagesFromJson(sessionJson.getJSONArray("messages"))
                ))
            }

            Log.d(TAG, "Loaded ${sessions.size} sessions from local storage")
            sessions
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load sessions: ${e.message}")
            emptyList()
        }
    }

    /**
     * Clear all stored sessions
     */
    fun clearAll() {
        prefs.edit().remove(KEY_SESSIONS).apply()
        Log.d(TAG, "Cleared all local storage")
    }

    private fun messagesToJson(messages: List<Message>): JSONArray {
        return JSONArray().apply {
            messages.forEach { msg ->
                put(JSONObject().apply {
                    put("id", msg.id)
                    put("text", msg.text)
                    put("sender", msg.sender.name)
                    put("state", msg.state.name)
                    put("isStreaming", msg.isStreaming)
                    // Note: Attachments are not persisted (URIs may not be valid after restart)
                })
            }
        }
    }

    private fun messagesFromJson(jsonArray: JSONArray): List<Message> {
        val messages = mutableListOf<Message>()
        for (i in 0 until jsonArray.length()) {
            val msgJson = jsonArray.getJSONObject(i)
            messages.add(Message(
                id = msgJson.getString("id"),
                text = msgJson.getString("text"),
                sender = Sender.valueOf(msgJson.getString("sender")),
                state = try {
                    MessageState.valueOf(msgJson.getString("state"))
                } catch (e: Exception) {
                    MessageState.DONE
                },
                isStreaming = msgJson.optBoolean("isStreaming", false)
            ))
        }
        return messages
    }
}
