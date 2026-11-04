package com.martorell.albert.basketcoach.usecases.chat

import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.repository.ChatRepository
import com.martorell.albert.basketcoach.domain.repository.GameRepository
import javax.inject.Inject

/**
 * Domain interactor that handles dispatching a coach message to the AI assistant within a topic-scoped thread
 * with contextual game grounding.
 *
 * Attempts to enrich conversational context with historical matches from [GameRepository].
 * If match retrieval encounters an issue, it gracefully degrades by submitting the message without
 * match history (`null`), preserving chat availability.
 *
 * @property gameRepository Domain repository providing access to Phase 1 match data.
 * @property chatRepository Domain repository managing topic-scoped coaching conversation threads.
 */
class SendChatMessageUseCase @Inject constructor(
    private val gameRepository: GameRepository,
    private val chatRepository: ChatRepository
) {
    /**
     * Dispatches [messageText] to the AI assistant within the thread identified by [topicId].
     *
     * @param messageText The message or question sent by the coach.
     * @param topicId The unique identifier of the target topic thread or improvement area.
     * @return [Result.Success] containing the assistant's [ChatMessage], or [Result.Error] with [DomainError].
     */
    suspend operator fun invoke(
        messageText: String,
        topicId: String
    ): Result<ChatMessage, DomainError> {
        val games = when (val gamesResult = gameRepository.getPhase1Games()) {
            is Result.Success -> gamesResult.data
            is Result.Error -> null
        }

        return chatRepository.sendMessage(messageText, games, topicId)
    }
}
