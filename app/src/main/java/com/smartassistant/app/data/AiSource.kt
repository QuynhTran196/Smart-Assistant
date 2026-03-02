package com.smartassistant.app.data

import android.util.Log
import 3rd.llm.3rdLlm
import 3rd.llm.device.3rdLlmDeviceDiagnosticManager
import 3rd.llm.device.MessageCache

/**
 * AiSource - AI Backend Data Source
 * Wraps the LLM SDK for AI chat functionality.
 */
class AiSource {

    companion object {
        private const val TAG = "AiSource"
    }

    private var llm: 3rdLlm? = null
    private var manager: 3rdLlmDeviceDiagnosticManager? = null

    /** Check if AI backend is initialized and ready */
    fun isReady(): Boolean = manager != null

    /** Initialize the AI backend - must be called first */
    fun initialize() {
        Log.d(TAG, "Initializing AI backend...")
        llm = 3rdLlm.create3rdLlm { _, ready ->
            Log.d(TAG, if (ready) "AI backend ready" else "AI backend not ready")
        }
        manager = llm?.get3rdLlmManager(3rdLlm.DEVICE_DIAGNOSTIC)
                as? 3rdLlmDeviceDiagnosticManager
    }

    /** Create a new chat session - returns -1 if not initialized */
    fun createSession(): Int {
        // Guard: Return invalid ID if not initialized yet
        if (manager == null) {
            Log.w(TAG, "createSession called before initialization")
            return -1
        }
        Log.d(TAG, "Creating chat session")
        return manager!!.createChatSession()
    }

    /** Destroy a chat session - no-op if not initialized */
    fun destroySession(sessionId: Int) {
        if (manager == null) return
        Log.d(TAG, "Destroying session: $sessionId")
        manager!!.destroyChatSession(sessionId)
    }

    /** Register callback for streaming tokens - no-op if not initialized */
    fun registerTokenListener(sessionId: Int, onToken: (String) -> Unit) {
        if (manager == null) return
        Log.d(TAG, "Registering listener for session: $sessionId")
        val listener = object : 3rdLlmDeviceDiagnosticManager.3rdLlmDeviceDiagnosticListener {
            override fun onMessageResponse(sid: Int, message: String) {
                onToken(message)
            }
            override fun onTroubleShootStatus(sid: Int, status: Boolean) {}
        }
        manager!!.register3rdLlmDeviceDiagnosticListener(sessionId, listener)
    }

    /** Send message to AI - no-op if not initialized */
    fun sendMessage(sessionId: Int, message: String) {
        if (manager == null) {
            Log.w(TAG, "sendMessage called before initialization")
            return
        }
        Log.d(TAG, "Sending to session $sessionId: $message")
        manager!!.sendMessage(sessionId, message)
    }

    /** Stop AI generation - no-op if not initialized */
    fun stopGeneration(sessionId: Int) {
        if (manager == null) return
        manager!!.forceStopChatSession(sessionId)
    }

    /** Load a user profile - no-op if not initialized */
    fun loadProfile(name: String) {
        if (manager == null) {
            Log.w(TAG, "loadProfile called before initialization")
            return
        }
        try {
            manager!!.loadProfile(name)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load profile: ${e.message}")
        }
    }

    /** Get cached messages from backend - returns empty list if not yet initialized */
    fun getCache(): List<MessageCache> {
        // Guard: Return empty if not initialized yet (prevents startup blocking)
        if (manager == null) return emptyList()

        return try {
            manager!!.getMessagesCache()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get cache: ${e.message}")
            emptyList()
        }
    }
}
