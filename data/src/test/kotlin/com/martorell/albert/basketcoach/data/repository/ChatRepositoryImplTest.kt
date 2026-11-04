package com.martorell.albert.basketcoach.data.repository

import com.martorell.albert.basketcoach.data.network.dto.AiAnalysisResponseDto
import com.martorell.albert.basketcoach.data.network.dto.ChatMessageDto
import com.martorell.albert.basketcoach.data.source.local.ChatLocalDataSourceImpl
import com.martorell.albert.basketcoach.data.source.remote.AiRemoteDataSource
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
        var exceptionToThrow: Throwable? = null,
        var isRelevantResponse: Boolean = true
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

        override suspend fun isPromptBasketballRelevant(
            prompt: String,
            lastAssistantMessage: String?
        ): Boolean {
            return isRelevantResponse
        }
    }

    private lateinit var fakeRemoteDataSource: FakeAiRemoteDataSource
    private lateinit var localDataSource: ChatLocalDataSourceImpl
    private lateinit var repository: ChatRepositoryImpl

    @Before
    fun setUp() {
        fakeRemoteDataSource = FakeAiRemoteDataSource()
        localDataSource = ChatLocalDataSourceImpl()
        repository = ChatRepositoryImpl(
            remoteDataSource = fakeRemoteDataSource,
            localDataSource = localDataSource
        )
    }

    @Test
    fun `sendMessage and getMessages maintain topic-scoped thread isolation`() = runTest {
        val topic1 = "topic_rebound"
        val topic2 = "topic_turnovers"

        val sendResult1 = repository.sendMessage("Message in topic 1", emptyList(), topic1)
        assertTrue(sendResult1 is Result.Success)

        val sendResult2 = repository.sendMessage("Message in topic 2", emptyList(), topic2)
        assertTrue(sendResult2 is Result.Success)

        val topic1MessagesResult = repository.getMessages(topic1)
        assertTrue(topic1MessagesResult is Result.Success)
        val topic1Messages = (topic1MessagesResult as Result.Success).data
        assertEquals(2, topic1Messages.size)
        assertEquals("Message in topic 1", topic1Messages[0].text)
        assertEquals(MessageSender.COACH, topic1Messages[0].sender)
        assertEquals(MessageSender.AI, topic1Messages[1].sender)

        val topic2MessagesResult = repository.getMessages(topic2)
        assertTrue(topic2MessagesResult is Result.Success)
        val topic2Messages = (topic2MessagesResult as Result.Success).data
        assertEquals(2, topic2Messages.size)
        assertEquals("Message in topic 2", topic2Messages[0].text)
        assertEquals(MessageSender.COACH, topic2Messages[0].sender)
        assertEquals(MessageSender.AI, topic2Messages[1].sender)
    }

    @Test
    fun `sendMessage when prompt is off-topic short-circuits without calling chat completions`() = runTest {
        fakeRemoteDataSource.isRelevantResponse = false

        val sendResult = repository.sendMessage("Quin temps fa avui?", null, "open_inquiry")

        assertTrue(sendResult is Result.Success)
        val aiMessage = (sendResult as Result.Success).data
        assertEquals(0, fakeRemoteDataSource.callCount)
        assertTrue(aiMessage.text.contains("Bàsquet Pia Sabadell"))

        val allMessages = repository.getMessages("open_inquiry")
        assertTrue(allMessages is Result.Success)
        val messageList = (allMessages as Result.Success).data
        assertEquals(2, messageList.size)
        assertEquals(MessageSender.COACH, messageList[0].sender)
        assertEquals(MessageSender.AI, messageList[1].sender)
    }
}
