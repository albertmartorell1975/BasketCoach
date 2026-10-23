package com.martorell.albert.basketcoach.data.source.local

import com.martorell.albert.basketcoach.domain.model.ChatMessage

/**
 * Local data source contract acting as the Single Source of Truth (SSOT) for the coaching chat thread.
 */
interface ChatLocalDataSource {
    /**
     * Retrieves all recorded chat messages in chronological order.
     */
    suspend fun getMessages(): List<ChatMessage>

    /**
     * Appends a new chat message to the local conversation history.
     */
    suspend fun saveMessage(message: ChatMessage)

    /**
     * Clears all messages from the conversation history.
     */
    suspend fun clearMessages()
}
