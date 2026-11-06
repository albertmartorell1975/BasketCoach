package com.martorell.albert.basketcoach.data.guardrail

import com.martorell.albert.basketcoach.data.mapper.HttpException
import com.martorell.albert.basketcoach.data.network.dto.ChatMessageDto
import com.martorell.albert.basketcoach.data.network.dto.JevAnswerDto
import com.martorell.albert.basketcoach.data.network.dto.JevDecisionRequestDto
import com.martorell.albert.basketcoach.data.network.dto.JevDecisionResponseDto
import com.martorell.albert.basketcoach.data.source.remote.AiRemoteDataSource
import com.martorell.albert.basketcoach.domain.prompt.PromptTemplates
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TacticalGuardrailEvaluatorTest {

    private class FakeAiRemoteDataSource : AiRemoteDataSource {
        var usingDevExpert: Boolean = true
        var decisionResponse: JevDecisionResponseDto? = null
        var chatCompletionResponse: String = ""
        var exceptionOnDecision: Throwable? = null
        var lastCapturedMessages: List<ChatMessageDto> = emptyList()

        override fun isUsingDevExpert(): Boolean = usingDevExpert

        override suspend fun executeChatCompletion(
            messages: List<ChatMessageDto>,
            temperature: Double
        ): String {
            lastCapturedMessages = messages
            return chatCompletionResponse
        }

        override suspend fun executeDecision(
            request: JevDecisionRequestDto
        ): JevDecisionResponseDto {
            exceptionOnDecision?.let {
                usingDevExpert = false // Simulate failover latching
                throw it
            }
            return decisionResponse ?: throw IllegalStateException("No decision response configured")
        }
    }

    private lateinit var fakeDataSource: FakeAiRemoteDataSource
    private lateinit var evaluator: TacticalGuardrailEvaluator

    @Before
    fun setUp() {
        fakeDataSource = FakeAiRemoteDataSource()
        evaluator = TacticalGuardrailEvaluator(fakeDataSource)
    }

    @Test
    fun isBasketballRelevant_whenDevExpertReturnsRelevant_returnsTrue() = runTest {
        fakeDataSource.usingDevExpert = true
        fakeDataSource.decisionResponse = JevDecisionResponseDto(
            answers = mapOf(
                PromptTemplates.JEV_QUESTION_DOMAIN_RELEVANCE to JevAnswerDto(choice = PromptTemplates.JEV_CHOICE_BASKETBALL_RELEVANT)
            )
        )

        val result = evaluator.isBasketballRelevant("Com defensem el bloqueig directe?", null)
        assertTrue(result)
    }

    @Test
    fun isBasketballRelevant_whenDevExpertReturnsOffTopic_returnsFalse() = runTest {
        fakeDataSource.usingDevExpert = true
        fakeDataSource.decisionResponse = JevDecisionResponseDto(
            answers = mapOf(
                PromptTemplates.JEV_QUESTION_DOMAIN_RELEVANCE to JevAnswerDto(choice = PromptTemplates.JEV_CHOICE_OFF_TOPIC)
            )
        )

        val result = evaluator.isBasketballRelevant("Quin temps fa a Barcelona?", null)
        assertFalse(result)
    }

    @Test
    fun isBasketballRelevant_whenDevExpertFailsWith402_fallsBackToGeminiClassifier() = runTest {
        fakeDataSource.usingDevExpert = true
        fakeDataSource.exceptionOnDecision = HttpException(402, "Payment Required")
        fakeDataSource.chatCompletionResponse = "{\"choice\": \"basketball_relevant\"}"

        val result = evaluator.isBasketballRelevant("Com millorem el tir exterior?", "Parlem del tir.")
        assertTrue(result)
        assertEquals(2, fakeDataSource.lastCapturedMessages.size)
        assertTrue(fakeDataSource.lastCapturedMessages[1].content.contains("Com millorem el tir exterior?"))
    }
}
