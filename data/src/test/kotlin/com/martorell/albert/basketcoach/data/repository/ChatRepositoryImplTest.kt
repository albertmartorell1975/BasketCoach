package com.martorell.albert.basketcoach.data.repository

import com.martorell.albert.basketcoach.data.network.client.DevExpertApiClient
import com.martorell.albert.basketcoach.data.network.dto.AiAnalysisResponseDto
import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.MessageSender
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.repository.GameRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatRepositoryImplTest {

    private val fakeGameRepository = object : GameRepository {
        override suspend fun getPhase1Games(): Result<List<Game>, DomainError> = Result.Success(emptyList())
        override suspend fun getGameByRound(round: Int): Result<Game, DomainError> = Result.Error(DomainError.Local.NotFound)
    }

    private class FakeChatApiClient(
        var responseToReturn: Result<String, DomainError> = Result.Success("AI advice")
    ) : DevExpertApiClient {
        var capturedHistory: List<ChatMessage>? = null
        var capturedPrompt: String? = null

        override suspend fun generateAnalysis(gameContext: String): Result<AiAnalysisResponseDto, DomainError> {
            return Result.Error(DomainError.Generic("Not used"))
        }

        override suspend fun generateChatResponse(
            conversationHistory: List<ChatMessage>,
            newPrompt: String,
            gameContext: String?
        ): Result<String, DomainError> {
            capturedHistory = conversationHistory
            capturedPrompt = newPrompt
            return responseToReturn
        }
    }

    @Test
    fun `initial getMessages returns empty list`() = runTest {
        val fakeClient = FakeChatApiClient()
        val repository = ChatRepositoryImpl(fakeGameRepository, fakeClient)

        val messagesResult = repository.getMessages()

        assertTrue(messagesResult is Result.Success)
        assertTrue((messagesResult as Result.Success).data.isEmpty())
    }

    @Test
    fun `sendMessage appends coach query, calls API, appends AI response, and returns Success`() = runTest {
        val fakeClient = FakeChatApiClient(Result.Success("Boxing out is essential."))
        var idCounter = 1
        val repository = ChatRepositoryImpl(
            gameRepository = fakeGameRepository,
            apiClient = fakeClient,
            idGenerator = { (idCounter++).toString() },
            timestampProvider = { 1000L }
        )

        val sendResult = repository.sendMessage("How do we improve rebounding?")

        assertTrue(sendResult is Result.Success)
        val aiMessage = (sendResult as Result.Success).data
        assertEquals("Boxing out is essential.", aiMessage.text)
        assertEquals(MessageSender.AI, aiMessage.sender)

        val messagesResult = repository.getMessages()
        assertTrue(messagesResult is Result.Success)
        val messages = (messagesResult as Result.Success).data
        assertEquals(2, messages.size)

        assertEquals("How do we improve rebounding?", messages[0].text)
        assertEquals(MessageSender.COACH, messages[0].sender)

        assertEquals("Boxing out is essential.", messages[1].text)
        assertEquals(MessageSender.AI, messages[1].sender)
    }

    @Test
    fun `network failure preserves coach query in thread and returns Result Error`() = runTest {
        val fakeClient = FakeChatApiClient(Result.Error(DomainError.Network.Timeout))
        val repository = ChatRepositoryImpl(
            gameRepository = fakeGameRepository,
            apiClient = fakeClient
        )

        val sendResult = repository.sendMessage("Any drill suggestions?")

        assertTrue(sendResult is Result.Error)
        assertEquals(DomainError.Network.Timeout, (sendResult as Result.Error).error)

        val messagesResult = repository.getMessages()
        assertTrue(messagesResult is Result.Success)
        val messages = (messagesResult as Result.Success).data
        assertEquals(1, messages.size)
        assertEquals("Any drill suggestions?", messages.first().text)
        assertEquals(MessageSender.COACH, messages.first().sender)
    }
}
