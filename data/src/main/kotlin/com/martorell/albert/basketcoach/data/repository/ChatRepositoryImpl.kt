package com.martorell.albert.basketcoach.data.repository

import com.martorell.albert.basketcoach.data.network.client.DevExpertApiClient
import com.martorell.albert.basketcoach.data.network.prompt.PromptTemplates
import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.MessageSender
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.repository.ChatRepository
import com.martorell.albert.basketcoach.domain.repository.GameRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID

/**
 * Implementation of [ChatRepository] managing a single-thread AI basketball coach chat conversation.
 *
 * Grounded in Phase 1 match data sourced from [GameRepository] and communicated via [DevExpertApiClient].
 * Maintains active conversation history in memory protected by a [Mutex].
 */
class ChatRepositoryImpl(
    private val gameRepository: GameRepository,
    private val apiClient: DevExpertApiClient,
    private val idGenerator: () -> String = { UUID.randomUUID().toString() },
    private val timestampProvider: () -> Long = { System.currentTimeMillis() }
) : ChatRepository {

    private val mutex = Mutex()
    private val messages = mutableListOf<ChatMessage>()

    override suspend fun getMessages(): Result<List<ChatMessage>, DomainError> {
        mutex.withLock {
            return Result.Success(messages.toList())
        }
    }

    override suspend fun sendMessage(messageText: String): Result<ChatMessage, DomainError> {
        val coachMessage = ChatMessage(
            id = idGenerator(),
            sender = MessageSender.COACH,
            text = messageText,
            timestamp = timestampProvider()
        )

        val historySnapshot: List<ChatMessage>
        mutex.withLock {
            messages.add(coachMessage)
            historySnapshot = messages.dropLast(1).toList()
        }

        val gameContext = when (val gamesResult = gameRepository.getPhase1Games()) {
            is Result.Success -> PromptTemplates.formatGameSummary(gamesResult.data)
            is Result.Error -> null
        }

        val responseResult = apiClient.generateChatResponse(
            conversationHistory = historySnapshot,
            newPrompt = messageText,
            gameContext = gameContext
        )

        return when (responseResult) {
            is Result.Success -> {
                val aiMessage = ChatMessage(
                    id = idGenerator(),
                    sender = MessageSender.AI,
                    text = responseResult.data,
                    timestamp = timestampProvider()
                )
                mutex.withLock {
                    messages.add(aiMessage)
                }
                Result.Success(aiMessage)
            }
            is Result.Error -> {
                // Coach message is preserved in thread history on failure
                responseResult
            }
        }
    }
}
