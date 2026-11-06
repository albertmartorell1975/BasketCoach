package com.martorell.albert.basketcoach.data.repository

import com.martorell.albert.basketcoach.data.guardrail.TacticalGuardrailEvaluator
import com.martorell.albert.basketcoach.data.network.dto.ChatMessageDto
import com.martorell.albert.basketcoach.data.network.dto.ChatRole
import com.martorell.albert.basketcoach.data.network.dto.JevAnswerDto
import com.martorell.albert.basketcoach.data.network.dto.JevDecisionRequestDto
import com.martorell.albert.basketcoach.data.network.dto.JevDecisionResponseDto
import com.martorell.albert.basketcoach.data.source.local.AiAnalysisLocalDataSourceImpl
import com.martorell.albert.basketcoach.data.source.local.ChatLocalDataSourceImpl
import com.martorell.albert.basketcoach.data.source.remote.AiRemoteDataSource
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.InsightCategory
import com.martorell.albert.basketcoach.domain.model.InsightMetric
import com.martorell.albert.basketcoach.domain.model.InsightType
import com.martorell.albert.basketcoach.domain.model.MessageSender
import com.martorell.albert.basketcoach.domain.model.MetricSentiment
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.prompt.PromptTemplates
import com.martorell.albert.basketcoach.domain.repository.ChatResourceProvider
import com.martorell.albert.basketcoach.domain.repository.OPEN_INQUIRY_TOPIC_ID
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ChatRepositoryImplTest {

    private class FakeAiRemoteDataSource(
        var chatResponse: String = "AI response text",
        var exceptionToThrow: Throwable? = null,
        var isRelevantChoice: String = PromptTemplates.JEV_CHOICE_BASKETBALL_RELEVANT,
        var usingDevExpert: Boolean = true
    ) : AiRemoteDataSource {
        var capturedMessages: List<ChatMessageDto> = emptyList()
        var capturedTemperature: Double = 0.0
        var capturedDecisionRequest: JevDecisionRequestDto? = null
        var callCount: Int = 0

        override fun isUsingDevExpert(): Boolean = usingDevExpert

        override suspend fun executeChatCompletion(
            messages: List<ChatMessageDto>,
            temperature: Double
        ): String {
            callCount++
            capturedMessages = messages
            capturedTemperature = temperature
            exceptionToThrow?.let { throw it }
            return chatResponse
        }

        override suspend fun executeDecision(
            request: JevDecisionRequestDto
        ): JevDecisionResponseDto {
            capturedDecisionRequest = request
            exceptionToThrow?.let { throw it }
            return JevDecisionResponseDto(
                answers = mapOf(
                    PromptTemplates.JEV_QUESTION_DOMAIN_RELEVANCE to JevAnswerDto(choice = isRelevantChoice)
                )
            )
        }
    }

    private class FakeChatResourceProvider(
        var rejectionText: String = "Com a assistent tàctic del Bàsquet Pia Sabadell, només puc respondre a consultes sobre bàsquet.",
        var inquiryLabel: String = "Consulta Lliure"
    ) : ChatResourceProvider {
        override fun getOffTopicRejectionMessage(): String = rejectionText
        override fun getOpenInquiryLabel(): String = inquiryLabel
    }

    private lateinit var fakeRemoteDataSource: FakeAiRemoteDataSource
    private lateinit var localDataSource: ChatLocalDataSourceImpl
    private lateinit var fakeAnalysisLocalDataSource: AiAnalysisLocalDataSourceImpl
    private lateinit var fakeChatResourceProvider: FakeChatResourceProvider
    private lateinit var repository: ChatRepositoryImpl

    @Before
    fun setUp() {
        fakeRemoteDataSource = FakeAiRemoteDataSource()
        localDataSource = ChatLocalDataSourceImpl()
        fakeAnalysisLocalDataSource = AiAnalysisLocalDataSourceImpl()
        fakeChatResourceProvider = FakeChatResourceProvider()
        val evaluator = TacticalGuardrailEvaluator(fakeRemoteDataSource)
        repository = ChatRepositoryImpl(
            remoteDataSource = fakeRemoteDataSource,
            localDataSource = localDataSource,
            aiAnalysisLocalDataSource = fakeAnalysisLocalDataSource,
            chatResourceProvider = fakeChatResourceProvider,
            guardrailEvaluator = evaluator
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
    fun `sendMessage when prompt is off-topic short-circuits with localized rejection without calling completions`() = runTest {
        fakeRemoteDataSource.isRelevantChoice = PromptTemplates.JEV_CHOICE_OFF_TOPIC

        val sendResult = repository.sendMessage("Quin temps fa avui?", null, OPEN_INQUIRY_TOPIC_ID)

        assertTrue(sendResult is Result.Success)
        val aiMessage = (sendResult as Result.Success).data
        assertEquals(0, fakeRemoteDataSource.callCount)
        assertEquals(fakeChatResourceProvider.rejectionText, aiMessage.text)

        val allMessages = repository.getMessages(OPEN_INQUIRY_TOPIC_ID)
        assertTrue(allMessages is Result.Success)
        val messageList = (allMessages as Result.Success).data
        assertEquals(2, messageList.size)
        assertEquals(MessageSender.COACH, messageList[0].sender)
        assertEquals(MessageSender.AI, messageList[1].sender)
    }

    @Test
    fun `sendMessage for focused improvement area retrieves cached insight, injects topicContext and localized label`() = runTest {
        val sampleInsight = Insight(
            id = "insight-rebots",
            title = "Desavantatge estructural en el rebot defensiu",
            description = "Cedim 14 rebots ofensius per partit generant 18 punts de segona oportunitat.",
            category = InsightCategory.DEFENSE,
            relatedGameRounds = listOf(1, 3, 5),
            type = InsightType.IMPROVEMENT,
            contextTag = "Fase Defensiva · Rebot",
            metric = InsightMetric(
                primaryLabel = "Rebots defensius",
                primaryValue = "62.4%",
                primarySubValue = "-10% vs rivals",
                secondaryLabel = "Segones oportunitats",
                secondaryValue = "18.2 pts/partit",
                secondaryTag = "dataset:def_reb",
                sentiment = MetricSentiment.NEGATIVE
            ),
            priority = "Alta",
            footerNote = "Prioritat màxima de treball tàctic.",
            exploreChatPrompt = "Com millorem el blocatge de rebot defensiu?"
        )
        fakeAnalysisLocalDataSource.saveAnalysis(listOf(sampleInsight), emptyList())

        val sendResult = repository.sendMessage(
            messageText = "Com podem reduir les pèrdues de pilota?",
            games = emptyList(),
            topicId = "insight-rebots"
        )

        assertTrue(sendResult is Result.Success)
        assertEquals(1, fakeRemoteDataSource.callCount)
        assertEquals(0.7, fakeRemoteDataSource.capturedTemperature, 0.001)

        val systemMessage = fakeRemoteDataSource.capturedMessages.firstOrNull { it.role == ChatRole.SYSTEM }?.content.orEmpty()
        assertTrue(systemMessage.contains("Desavantatge estructural en el rebot defensiu"))
        assertTrue(systemMessage.contains("Cedim 14 rebots ofensius"))
        assertTrue(systemMessage.contains("Rebots defensius: 62.4%"))
        assertTrue(systemMessage.contains("ACTIVE INTERVENTION AREA (THREAD FOCUS)"))
        assertTrue(systemMessage.contains("Consulta Lliure"))
    }

    @Test
    fun `sendMessage for open inquiry passes null topicContext and localized open inquiry label`() = runTest {
        val sendResult = repository.sendMessage(
            messageText = "Quina és la valoració global de la fase 1?",
            games = emptyList(),
            topicId = OPEN_INQUIRY_TOPIC_ID
        )

        assertTrue(sendResult is Result.Success)
        assertEquals(1, fakeRemoteDataSource.callCount)
        assertEquals(0.7, fakeRemoteDataSource.capturedTemperature, 0.001)

        val systemMessage = fakeRemoteDataSource.capturedMessages.firstOrNull { it.role == ChatRole.SYSTEM }?.content.orEmpty()
        assertFalse(systemMessage.contains("ACTIVE INTERVENTION AREA (THREAD FOCUS)"))
        assertTrue(systemMessage.contains("Consulta Lliure"))
    }
}
