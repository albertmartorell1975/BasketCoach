package com.martorell.albert.basketcoach.domain.repository

import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Result

/**
 * Identifier for the unconstrained tactical inquiry thread ("Open Inquiry" / "Consulta Lliure").
 *
 * Used when the coach initiates an open-ended dialogue not tied to a specific AI-detected improvement area.
 */
const val OPEN_INQUIRY_TOPIC_ID = "open_inquiry"

/**
 * Domain repository contract for managing topic-scoped AI coaching conversation threads.
 *
 * Implementations maintain chronological message histories partitioned by topic ID
 * and handle remote communication with the conversational AI assistant.
 */
interface ChatRepository {
    /**
     * Retrieves all conversation messages for the specified [topicId] in chronological order.
     *
     * @param topicId The unique identifier of the conversation topic or improvement area.
     * @return [Result.Success] containing the list of [ChatMessage] items, or [Result.Error] with [DomainError].
     */
    suspend fun getMessages(topicId: String): Result<List<ChatMessage>, DomainError>

    /**
     * Dispatches a coach message to the AI assistant within the specified [topicId] thread
     * and records both user and assistant messages.
     *
     * @param messageText The message or query submitted by the coach.
     * @param games Optional list of [Game] fixtures to provide tactical context to the assistant.
     * @param topicId The unique identifier of the conversation topic or improvement area.
     * @return [Result.Success] containing the generated assistant [ChatMessage], or [Result.Error] with [DomainError].
     */
    suspend fun sendMessage(
        messageText: String,
        games: List<Game>?,
        topicId: String
    ): Result<ChatMessage, DomainError>
}
