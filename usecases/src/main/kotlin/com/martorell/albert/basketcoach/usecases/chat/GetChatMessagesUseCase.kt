package com.martorell.albert.basketcoach.usecases.chat

import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.repository.ChatRepository
import javax.inject.Inject

/**
 * Domain interactor to retrieve all conversation messages in chronological order for a specific
 * topic thread from the AI coaching chat.
 *
 * @property chatRepository Domain repository managing in-memory chat messages.
 */
class GetChatMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    /**
     * Retrieves the chronological message history for the given [topicId].
     *
     * @param topicId The unique identifier of the topic or improvement area.
     * @return [Result.Success] containing the list of [ChatMessage] items, or [Result.Error] with [DomainError].
     */
    suspend operator fun invoke(topicId: String): Result<List<ChatMessage>, DomainError> {
        return chatRepository.getMessages(topicId)
    }
}
