package com.martorell.albert.basketcoach.usecases.chat

import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.repository.ChatRepository

/**
 * Domain interactor to retrieve all conversation messages in chronological order from the
 * single-thread AI coaching chat.
 *
 * @property chatRepository Domain repository managing in-memory chat messages.
 */
class GetChatMessagesUseCase(
    private val chatRepository: ChatRepository
) {
    /**
     * Retrieves all chat messages.
     *
     * @return [Result.Success] with the list of [ChatMessage] entities, or [Result.Error] with [DomainError].
     */
    suspend operator fun invoke(): Result<List<ChatMessage>, DomainError> {
        return chatRepository.getMessages()
    }
}
