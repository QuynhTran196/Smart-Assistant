package com.smartassistant.app.data

import android.net.Uri

/**
 * ============================================================================
 * Models.kt - All Data Models
 * ============================================================================
 */

// ==================== FILE ATTACHMENT ====================

/**
 * Represents a file attached to a message
 */
data class FileAttachment(
    val id: String,
    val uri: Uri,
    val name: String,
    val mimeType: String,
    val size: Long = 0
) {
    val isImage: Boolean
        get() = mimeType.startsWith("image/")

    val isDocument: Boolean
        get() = mimeType.startsWith("application/") || mimeType.startsWith("text/")
}

// ==================== MESSAGE ====================

enum class Sender { USER, AI }

enum class MessageState { IDLE, TYPING, STREAMING, DONE }

data class Message(
    val id: String,
    val text: String,
    val sender: Sender,
    val state: MessageState = MessageState.IDLE,
    val isStreaming: Boolean = false,
    val attachments: List<FileAttachment> = emptyList()
)

// ==================== SESSION ====================

data class Session(
    val id: String,
    val title: String,
    val lastMessage: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

// ==================== UI STATE ====================

data class ChatSessionState(
    val id: String,
    val title: String,
    val llmSessionId: Int,
    val messages: List<Message> = emptyList(),
    val showWelcome: Boolean = true,
    val pendingAttachments: List<FileAttachment> = emptyList()
)

data class ChatUiState(
    val sessions: List<ChatSessionState> = emptyList(),
    val currentSessionId: String? = null,
    val isGenerating: Boolean = false,
    val showAddDialog: Boolean = false
) {
    val currentSession: ChatSessionState?
        get() = sessions.find { it.id == currentSessionId }

    val currentMessages: List<Message>
        get() = currentSession?.messages.orEmpty()

    val pendingAttachments: List<FileAttachment>
        get() = currentSession?.pendingAttachments.orEmpty()
}

data class HistoryUiState(
    val sessions: List<Session> = emptyList()
)
