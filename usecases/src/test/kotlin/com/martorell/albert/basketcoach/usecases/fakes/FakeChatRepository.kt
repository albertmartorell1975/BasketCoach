package com.martorell.albert.basketcoach.usecases.fakes

import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.MessageSender
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.repository.ChatRepository

class FakeChatRepository : ChatRepository {
    var messagesToReturn: Result<List<ChatMessage>, DomainError> = Result.Success(emptyList())
    var sendMessageResult: Result<ChatMessage, DomainError>? = null
    var lastMessageText: String? = null
    var lastGames: List<Game>? = null
    var getMessagesCallCount: Int = 0
    var sendMessageCallCount: Int = 0

    override suspend fun getMessages(): Result<List<ChatMessage>, DomainError> {
        getMessagesCallCount++
        return messagesToReturn
    }

    override suspend fun sendMessage(messageText: String, games: List<Game>?): Result<ChatMessage, DomainError> {
        sendMessageCallCount++
        lastMessageText = messageText
        lastGames = games

        return sendMessageResult ?: Result.Success(
            ChatMessage(
                id = "fake-ai-msg-1",
                sender = MessageSender.AI,
                text = "AI response to: $messageText",
                timestamp = 1696800000000L
            )
        )
    }
}
