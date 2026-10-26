package com.martorell.albert.basketcoach.domain.repository

import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Result

/**
 * Domain repository contract for managing the single-thread AI coaching conversation.
 *
 * Implementations maintain chronological message history and handle remote communication
 * with the conversational AI assistant.
 */
interface ChatRepository {
    /**
     * Retrieves all conversation messages in chronological order.
     *
     * @return [Result.Success] containing the list of [ChatMessage] items, or [Result.Error] with [DomainError].
     */
    suspend fun getMessages(): Result<List<ChatMessage>, DomainError>

    /**
     * Dispatches a coach message to the AI assistant and records both user and assistant messages.
     *
     * @param messageText The message or query submitted by the coach.
     * @param games Optional list of [Game] fixtures to provide tactical context to the assistant.
     * @return [Result.Success] containing the generated assistant [ChatMessage], or [Result.Error] with [DomainError].
     */
    suspend fun sendMessage(messageText: String, games: List<Game>? = null): Result<ChatMessage, DomainError>
}
