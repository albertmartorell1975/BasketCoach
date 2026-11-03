package com.martorell.albert.basketcoach.data.repository

import com.martorell.albert.basketcoach.data.mapper.toDto
import com.martorell.albert.basketcoach.data.network.prompt.PromptTemplates
import com.martorell.albert.basketcoach.data.source.local.ChatLocalDataSource
import com.martorell.albert.basketcoach.data.source.remote.AiRemoteDataSource
import com.martorell.albert.basketcoach.data.util.safeCall
import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.MessageSender
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.repository.ChatRepository
import java.util.UUID
import javax.inject.Inject

/**
 * Concrete implementation of [ChatRepository] coordinating the single-thread in-memory conversation
 * history and AI response generation with domain guardrail pre-classification.
 *
 * Adheres to Clean Architecture data layer governance:
 * - Maintains single-thread in-memory session SSOT via [ChatLocalDataSource].
 * - Executes the 2-step AI guardrail pipeline: pre-evaluates user prompt domain relevance via
 *   [AiRemoteDataSource.isPromptBasketballRelevant] before invoking chat completions.
 * - Short-circuits off-topic queries immediately with a localized tactical refusal message
 *   ([PromptTemplates.buildOffTopicRejectionMessage]) without consuming generative AI tokens.
 * - Encapsulates all data operations within [safeCall] to map exceptions into typed [DomainError] results.
 *
 * @param remoteDataSource Remote client communicating with DevExpert or Gemini AI endpoints.
 * @param localDataSource Thread-safe in-memory cache holding the continuous chat thread messages.
 * @param idGenerator Lambda producing unique message identifiers (defaults to random UUID).
 * @param timestampProvider Lambda producing epoch millisecond timestamps (defaults to system time).
 */
class ChatRepositoryImpl(
    private val remoteDataSource: AiRemoteDataSource,
    private val localDataSource: ChatLocalDataSource,
    private val idGenerator: () -> String = { UUID.randomUUID().toString() },
    private val timestampProvider: () -> Long = { System.currentTimeMillis() }
) : ChatRepository {

    /**
     * Primary Hilt-injected constructor providing default UUID generation and system time.
     *
     * @param remoteDataSource Remote client communicating with DevExpert or Gemini AI endpoints.
     * @param localDataSource Thread-safe in-memory cache holding the continuous chat thread messages.
     */
    @Inject
    constructor(
        remoteDataSource: AiRemoteDataSource,
        localDataSource: ChatLocalDataSource
    ) : this(
        remoteDataSource = remoteDataSource,
        localDataSource = localDataSource,
        idGenerator = { UUID.randomUUID().toString() },
        timestampProvider = { System.currentTimeMillis() }
    )

    /**
     * Retrieves all conversation messages recorded in the single-thread session.
     *
     * @return [Result.Success] containing the chronological list of [ChatMessage] entities,
     * or [Result.Error] with [DomainError] if an unexpected local failure occurs.
     */
    override suspend fun getMessages(): Result<List<ChatMessage>, DomainError> = safeCall {
        localDataSource.getMessages()
    }

    /**
     * Appends a coach message to the conversation and generates an AI response.
     *
     * Pre-evaluates domain relevance via [AiRemoteDataSource.isPromptBasketballRelevant]. If the
     * prompt is off-topic, short-circuits by persisting and returning a localized refusal message
     * without calling remote chat completion inference.
     *
     * @param messageText The user input text submitted by the coach.
     * @param games Optional list of Phase 1 match fixtures used for contextual grounding.
     * @return [Result.Success] containing the newly generated assistant [ChatMessage],
     * or [Result.Error] with [DomainError] if a network, authorization, or technical failure occurs.
     */
    override suspend fun sendMessage(
        messageText: String,
        games: List<Game>?
    ): Result<ChatMessage, DomainError> = safeCall {
        val coachMessage = ChatMessage(
            id = idGenerator(),
            sender = MessageSender.COACH,
            text = messageText,
            timestamp = timestampProvider()
        )
        localDataSource.saveMessage(coachMessage)

        val lastAssistantMessage = localDataSource.getMessages()
            .dropLast(1)
            .lastOrNull { it.sender == MessageSender.AI }
            ?.text

        val isRelevant = remoteDataSource.isPromptBasketballRelevant(
            prompt = messageText,
            lastAssistantMessage = lastAssistantMessage
        )

        val responseText = if (isRelevant) {
            val historyDtos = localDataSource.getMessages().dropLast(1).map { it.toDto() }
            val gameContext = games?.let { PromptTemplates.formatGameSummary(it) }
            remoteDataSource.generateChatResponse(
                conversationHistory = historyDtos,
                newPrompt = messageText,
                gameContext = gameContext
            )
        } else {
            PromptTemplates.buildOffTopicRejectionMessage()
        }

        val aiMessage = ChatMessage(
            id = idGenerator(),
            sender = MessageSender.AI,
            text = responseText,
            timestamp = timestampProvider()
        )
        localDataSource.saveMessage(aiMessage)
        aiMessage
    }
}
