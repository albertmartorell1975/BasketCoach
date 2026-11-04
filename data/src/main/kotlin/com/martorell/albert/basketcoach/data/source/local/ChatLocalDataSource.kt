package com.martorell.albert.basketcoach.data.source.local

import com.martorell.albert.basketcoach.domain.model.ChatMessage

/**
 * Local data source contract acting as the Single Source of Truth (SSOT) for topic-scoped coaching chat threads.
 *
 * Stores conversation messages in memory partitioned by topic ID during the active application session.
 */
interface ChatLocalDataSource {
    /**
     * Retrieves all cached messages for the specified [topicId] in chronological order.
     *
     * @param topicId The unique identifier of the topic or improvement area.
     * @return List of [ChatMessage] instances, or an empty list if no messages exist for this topic.
     */
    suspend fun getMessages(topicId: String): List<ChatMessage>

    /**
     * Appends a new [message] to the in-memory conversation thread associated with [topicId].
     *
     * @param message The [ChatMessage] to append.
     * @param topicId The unique identifier of the target topic thread.
     */
    suspend fun saveMessage(message: ChatMessage, topicId: String)

    /**
     * Clears all cached conversation messages for the specified [topicId].
     *
     * @param topicId The unique identifier of the topic thread to clear.
     */
    suspend fun clearMessages(topicId: String)

    /**
     * Clears all cached conversation messages across all topic threads.
     */
    suspend fun clearAllMessages()
}
