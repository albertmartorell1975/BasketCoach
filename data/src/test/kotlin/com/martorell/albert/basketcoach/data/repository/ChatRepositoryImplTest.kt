package com.martorell.albert.basketcoach.data.repository

import com.martorell.albert.basketcoach.data.network.dto.AiAnalysisResponseDto
import com.martorell.albert.basketcoach.data.network.dto.ChatMessageDto
import com.martorell.albert.basketcoach.data.source.local.ChatLocalDataSourceImpl
import com.martorell.albert.basketcoach.data.source.remote.AiRemoteDataSource
import com.martorell.albert.basketcoach.data.util.HttpException
import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.MessageSender
import com.martorell.albert.basketcoach.domain.model.Result
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ChatRepositoryImplTest {

    private class FakeAiRemoteDataSource(
        var chatResponse: String = "AI response text",
        var exceptionToThrow: Throwable? = null
    ) : AiRemoteDataSource {
        var capturedHistory: List<ChatMessageDto> = emptyList()
        var capturedPrompt: String = ""
        var capturedContext: String? = null
        var callCount: Int = 0

        override suspend fun generateAnalysis(gameContext: String): AiAnalysisResponseDto {
            throw UnsupportedOperationException()
        }

        override suspend fun generateChatResponse(
            conversationHistory: List<ChatMessageDto>,
            newPrompt: String,
            gameContext: String?
        ): String {
            callCount++
            capturedHistory = conversationHistory
            capturedPrompt = newPrompt
            capturedContext = gameContext
            exceptionToThrow?.let { throw it }
            return chatResponse
        }
    }

    private lateinit var fakeRemoteDataSource: FakeAiRemoteDataSource
    private lateinit var localDataSource: ChatLocalDataSourceImpl
    private lateinit var repository: ChatRepositoryImpl

    @Before
    fun setUp() {
        fakeRemoteDataSource = FakeAiRemoteDataSource()
        localDataSource = ChatLocalDataSourceImpl()

        var idCounter = 0
        repository = ChatRepositoryImpl(
            remoteDataSource = fakeRemoteDataSource,
            localDataSource = localDataSource,
            idGenerator = { "msg-${++idCounter}" },
            timestampProvider = { 1000L * idCounter }
        )
    }

    @Test
    fun `sendMessage appends coach message, invokes remoteDataSource with history and context, and appends ai message`() = runTest {
        val sendResult = repository.sendMessage("How do we improve defense?", emptyList())

        assertTrue(sendResult is Result.Success)
        val aiMessage = (sendResult as Result.Success).data
        assertEquals("msg-2", aiMessage.id)
        assertEquals(MessageSender.AI, aiMessage.sender)
        assertEquals("AI response text", aiMessage.text)

        val allMessages = repository.getMessages()
        assertTrue(allMessages is Result.Success)
        val messageList = (allMessages as Result.Success).data
        assertEquals(2, messageList.size)
        assertEquals(MessageSender.COACH, messageList[0].sender)
        assertEquals("How do we improve defense?", messageList[0].text)
        assertEquals(MessageSender.AI, messageList[1].sender)
        assertEquals("AI response text", messageList[1].text)

        assertEquals("How do we improve defense?", fakeRemoteDataSource.capturedPrompt)
        assertEquals("No games recorded for Phase 1.", fakeRemoteDataSource.capturedContext)
        assertTrue(fakeRemoteDataSource.capturedHistory.isEmpty())
    }

    @Test
    fun `sendMessage preserves coach message in localDataSource when remoteDataSource throws`() = runTest {
        fakeRemoteDataSource.exceptionToThrow = HttpException(500, "Server Error")

        val sendResult = repository.sendMessage("Will this fail?")

        assertTrue(sendResult is Result.Error)
        val error = (sendResult as Result.Error).error
        assertTrue(error is DomainError.Network.ServerError)

        val allMessages = repository.getMessages()
        assertTrue(allMessages is Result.Success)
        val messageList = (allMessages as Result.Success).data
        assertEquals(1, messageList.size)
        assertEquals(MessageSender.COACH, messageList[0].sender)
    }

    @Test
    fun `getMessages returns all messages recorded in localDataSource`() = runTest {
        localDataSource.saveMessage(ChatMessage("msg-1", MessageSender.COACH, "First", 100L))
        localDataSource.saveMessage(ChatMessage("msg-2", MessageSender.AI, "Second", 200L))

        val result = repository.getMessages()

        assertTrue(result is Result.Success)
        assertEquals(2, (result as Result.Success).data.size)
    }
}
