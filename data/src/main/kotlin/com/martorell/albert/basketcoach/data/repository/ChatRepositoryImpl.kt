package com.martorell.albert.basketcoach.data.repository

import com.martorell.albert.basketcoach.data.network.dto.ChatMessageDto
import com.martorell.albert.basketcoach.data.source.local.ChatLocalDataSource
import com.martorell.albert.basketcoach.data.source.remote.AiRemoteDataSource
import com.martorell.albert.basketcoach.data.util.safeCall
import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.MessageSender
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.repository.ChatRepository
import java.util.UUID

/**
 * Production implementation of [ChatRepository] coordinating [AiRemoteDataSource]
 * and [ChatLocalDataSource] to manage a single-thread AI basketball coach chat conversation.
 */
class ChatRepositoryImpl(
    private val remoteDataSource: AiRemoteDataSource,
    private val localDataSource: ChatLocalDataSource,
    private val idGenerator: () -> String = { UUID.randomUUID().toString() },
    private val timestampProvider: () -> Long = { System.currentTimeMillis() }
) : ChatRepository {

    override suspend fun getMessages(): Result<List<ChatMessage>, DomainError> {
        return Result.Success(localDataSource.getMessages())
    }

    override suspend fun sendMessage(
        messageText: String,
        gameContext: String?
    ): Result<ChatMessage, DomainError> {
        val coachMessage = ChatMessage(
            id = idGenerator(),
            sender = MessageSender.COACH,
            text = messageText,
            timestamp = timestampProvider()
        )
        localDataSource.saveMessage(coachMessage)

        val priorMessages = localDataSource.getMessages().dropLast(1)
        val historyDtos = priorMessages.map { msg ->
            ChatMessageDto(
                role = if (msg.sender == MessageSender.COACH) "user" else "assistant",
                content = msg.text
            )
        }

        val remoteResult = safeCall {
            remoteDataSource.generateChatResponse(
                conversationHistory = historyDtos,
                newPrompt = messageText,
                gameContext = gameContext
            )
        }

        return when (remoteResult) {
            is Result.Success -> {
                val aiMessage = ChatMessage(
                    id = idGenerator(),
                    sender = MessageSender.AI,
                    text = remoteResult.data,
                    timestamp = timestampProvider()
                )
                localDataSource.saveMessage(aiMessage)
                Result.Success(aiMessage)
            }
            is Result.Error -> remoteResult
        }
    }
}
