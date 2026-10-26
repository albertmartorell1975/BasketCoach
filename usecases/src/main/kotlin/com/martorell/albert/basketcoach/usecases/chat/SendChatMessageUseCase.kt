package com.martorell.albert.basketcoach.usecases.chat

import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.repository.ChatRepository
import com.martorell.albert.basketcoach.domain.repository.GameRepository

/**
 * Domain interactor that handles dispatching a coach message to the AI assistant with contextual game grounding.
 *
 * Attempts to enrich conversational context with historical matches from [GameRepository].
 * If match retrieval encounters an issue, it gracefully degrades by submitting the message without
 * match history (`null`), preserving chat availability.
 *
 * @property gameRepository Domain repository providing access to Phase 1 match data.
 * @property chatRepository Domain repository managing the single-thread coaching conversation.
 */
class SendChatMessageUseCase(
    private val gameRepository: GameRepository,
    private val chatRepository: ChatRepository
) {
    /**
     * Sends a coach message to the AI assistant.
     *
     * @param messageText The user prompt or question submitted by the coach.
     * @return [Result.Success] containing the assistant's reply [ChatMessage], or [Result.Error] with [DomainError].
     */
    suspend operator fun invoke(messageText: String): Result<ChatMessage, DomainError> {
        val games = when (val gamesResult = gameRepository.getPhase1Games()) {
            is Result.Success -> gamesResult.data
            is Result.Error -> null
        }

        return chatRepository.sendMessage(messageText, games)
    }
}
