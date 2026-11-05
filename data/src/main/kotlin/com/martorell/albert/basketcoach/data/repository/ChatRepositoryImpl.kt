package com.martorell.albert.basketcoach.data.repository

import com.martorell.albert.basketcoach.data.mapper.toDomain
import com.martorell.albert.basketcoach.data.mapper.toDto
import com.martorell.albert.basketcoach.data.network.dto.ChatMessageDto
import com.martorell.albert.basketcoach.data.network.dto.ChatRole
import com.martorell.albert.basketcoach.data.network.prompt.PromptTemplates
import com.martorell.albert.basketcoach.data.source.local.AiAnalysisLocalDataSource
import com.martorell.albert.basketcoach.data.source.local.ChatLocalDataSource
import com.martorell.albert.basketcoach.data.source.remote.AiRemoteDataSource
import com.martorell.albert.basketcoach.data.util.safeCall
import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.MessageSender
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.repository.ChatRepository
import com.martorell.albert.basketcoach.domain.repository.ChatResourceProvider
import com.martorell.albert.basketcoach.domain.repository.OPEN_INQUIRY_TOPIC_ID
import javax.inject.Inject

/**
 * Concrete implementation of [ChatRepository] coordinating topic-scoped in-memory conversation
 * threads and AI response generation with domain guardrail pre-classification and topic focus.
 *
 * Adheres to Clean Architecture data layer governance:
 * - Maintains topic-scoped in-memory session SSOT via [ChatLocalDataSource].
 * - Retrieves active [Insight] from [AiAnalysisLocalDataSource] to ground improvement area threads.
 * - Delegates user-facing rejection and UI thread labels to [ChatResourceProvider] backed by Android strings.
 * - Executes the 2-step AI guardrail pipeline: pre-evaluates user prompt domain relevance via
 *   [AiRemoteDataSource.isPromptBasketballRelevant] before invoking chat completions.
 * - Short-circuits off-topic queries immediately with a localized tactical refusal message
 *   without consuming generative AI tokens.
 * - Encapsulates all data operations within [safeCall] to map exceptions into typed [DomainError] results.
 * - Delegates domain entity mapping strictly via [ChatMessageDto.toDomain].
 *
 * @param remoteDataSource Remote client communicating with DevExpert or Gemini AI endpoints.
 * @param localDataSource Thread-safe in-memory cache holding topic-partitioned conversation messages.
 * @param aiAnalysisLocalDataSource Single Source of Truth for cached tactical insights.
 * @param chatResourceProvider Provider delivering localized user-facing chat strings.
 */
class ChatRepositoryImpl @Inject constructor(
    private val remoteDataSource: AiRemoteDataSource,
    private val localDataSource: ChatLocalDataSource,
    private val aiAnalysisLocalDataSource: AiAnalysisLocalDataSource,
    private val chatResourceProvider: ChatResourceProvider
) : ChatRepository {

    override suspend fun getMessages(topicId: String): Result<List<ChatMessage>, DomainError> = safeCall {
        localDataSource.getMessages(topicId)
    }

    override suspend fun sendMessage(
        messageText: String,
        games: List<Game>?,
        topicId: String
    ): Result<ChatMessage, DomainError> = safeCall {
        val coachMessage = ChatMessageDto(
            role = ChatRole.USER,
            content = messageText
        ).toDomain()
        localDataSource.saveMessage(coachMessage, topicId)

        val lastAssistantMessage = localDataSource.getMessages(topicId)
            .dropLast(1)
            .lastOrNull { it.sender == MessageSender.AI }
            ?.text

        val isRelevant = remoteDataSource.isPromptBasketballRelevant(
            prompt = messageText,
            lastAssistantMessage = lastAssistantMessage
        )

        val responseText = if (isRelevant) {
            val historyDtos = localDataSource.getMessages(topicId).dropLast(1).map { it.toDto() }
            val gameContext = games?.let { PromptTemplates.formatGameSummary(it) }
            val topicContext = if (topicId != OPEN_INQUIRY_TOPIC_ID) {
                val activeInsight = aiAnalysisLocalDataSource.getCachedInsights()?.find {
                    it.id == topicId || (it.exploreChatPrompt.isNotBlank() && it.exploreChatPrompt.equals(topicId, ignoreCase = true))
                }
                activeInsight?.let { PromptTemplates.formatTopicContext(it) }
            } else {
                null
            }
            remoteDataSource.generateChatResponse(
                conversationHistory = historyDtos,
                newPrompt = messageText,
                gameContext = gameContext,
                topicContext = topicContext,
                openInquiryLabel = chatResourceProvider.getOpenInquiryLabel()
            )
        } else {
            chatResourceProvider.getOffTopicRejectionMessage()
        }

        val aiMessage = ChatMessageDto(
            role = ChatRole.ASSISTANT,
            content = responseText
        ).toDomain()
        localDataSource.saveMessage(aiMessage, topicId)
        aiMessage
    }
}
