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

class ChatRepositoryImpl(
    private val remoteDataSource: AiRemoteDataSource,
    private val localDataSource: ChatLocalDataSource,
    private val idGenerator: () -> String = { UUID.randomUUID().toString() },
    private val timestampProvider: () -> Long = { System.currentTimeMillis() }
) : ChatRepository {

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

    override suspend fun getMessages(): Result<List<ChatMessage>, DomainError> = safeCall {
        localDataSource.getMessages()
    }

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
