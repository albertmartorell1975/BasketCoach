package com.martorell.albert.basketcoach.data.source.remote

import com.martorell.albert.basketcoach.data.network.config.AiClientConfig
import com.martorell.albert.basketcoach.data.network.dto.ChatMessageDto
import com.martorell.albert.basketcoach.data.util.HttpException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class AiRemoteDataSourceTest {

    private lateinit var server: MockWebServer
    private lateinit var dataSource: AiRemoteDataSourceImpl

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        val baseUrl = server.url("/v1").toString()
        val config = AiClientConfig(
            apiKey = "test-api-key",
            baseUrl = baseUrl,
            model = "chat"
        )
        val shortTimeoutOkHttpClient = OkHttpClient.Builder()
            .connectTimeout(500, TimeUnit.MILLISECONDS)
            .readTimeout(500, TimeUnit.MILLISECONDS)
            .writeTimeout(500, TimeUnit.MILLISECONDS)
            .build()

        dataSource = AiRemoteDataSourceImpl(
            config = config,
            okHttpClient = shortTimeoutOkHttpClient
        )
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `generateAnalysis and generateChatResponse send auth header and parse fenced or plain responses`() = runTest {
        val jsonPayload = """
            {
              "insights": [
                {
                  "id": "ins-1",
                  "title": "Turnover Crisis",
                  "description": "High turnover rate in losses",
                  "category": "OFFENSE",
                  "relatedGameRounds": [1, 2],
                  "type": "IMPROVEMENT",
                  "contextTag": "Pèrdues",
                  "metric": {
                    "primaryLabel": "Pèrdues",
                    "primaryValue": "42%",
                    "primarySubValue": "AST/TO 0.51",
                    "secondaryLabel": "Impacte",
                    "secondaryValue": "6.4 pts",
                    "secondaryTag": "dataset:to",
                    "sentiment": "NEGATIVE"
                  },
                  "priority": "Alta",
                  "footerNote": "Pilar corrector",
                  "exploreChatPrompt": "Com reduir pèrdues?"
                }
              ],
              "drills": [
                {
                  "id": "dr-1",
                  "name": "Pressure Passing",
                  "objective": "Reduce TOs",
                  "instructions": ["Step 1", "Step 2"],
                  "focusArea": "Ball Security"
                }
              ]
            }
        """.trimIndent()

        val fencedContent = "```json\n$jsonPayload\n```"
        val analysisResponse = """
            {
              "id": "chatcmpl-test-1",
              "choices": [
                {
                  "index": 0,
                  "message": {
                    "role": "assistant",
                    "content": ${escapeJsonString(fencedContent)}
                  }
                }
              ]
            }
        """.trimIndent()

        val chatResponse = """
            {
              "id": "chatcmpl-test-2",
              "choices": [
                {
                  "index": 0,
                  "message": {
                    "role": "assistant",
                    "content": "Focus on boxing out and protecting defensive rebounds."
                  }
                }
              ]
            }
        """.trimIndent()

        server.enqueue(MockResponse().setResponseCode(200).setBody(analysisResponse))
        server.enqueue(MockResponse().setResponseCode(200).setBody(chatResponse))

        val analysis = dataSource.generateAnalysis("Phase 1 summary")
        assertEquals(1, analysis.insights.size)
        assertEquals("Turnover Crisis", analysis.insights.first().title)
        assertEquals(1, analysis.drills.size)

        val firstRequest = server.takeRequest()
        assertEquals("POST", firstRequest.method)
        assertEquals("/v1/chat/completions", firstRequest.path)
        assertEquals("Bearer test-api-key", firstRequest.getHeader("Authorization"))

        val reply = dataSource.generateChatResponse(
            listOf(ChatMessageDto("user", "How do we improve rebounds?")),
            "Any specific drills?"
        )
        assertEquals("Focus on boxing out and protecting defensive rebounds.", reply)
    }

    @Test
    fun `throws HttpException on blank apiKey, 401 unauthorized, and 500 server error`() = runTest {
        val blankKeyConfig = AiClientConfig(apiKey = "", baseUrl = server.url("/v1").toString(), model = "chat")
        val unauthSource = AiRemoteDataSourceImpl(config = blankKeyConfig)
        try {
            unauthSource.generateAnalysis("context")
            fail("Expected HttpException for blank API key")
        } catch (e: HttpException) {
            assertEquals(401, e.code)
        }
        assertEquals(0, server.requestCount)

        server.enqueue(MockResponse().setResponseCode(401).setBody("Unauthorized"))
        try {
            dataSource.generateAnalysis("context")
            fail("Expected HttpException 401")
        } catch (e: HttpException) {
            assertEquals(401, e.code)
        }

        server.enqueue(MockResponse().setResponseCode(500).setBody("Internal Server Error"))
        try {
            dataSource.generateAnalysis("context")
            fail("Expected HttpException 500")
        } catch (e: HttpException) {
            assertEquals(500, e.code)
        }
    }

    @Test
    fun `throws SocketTimeoutException on timeout and SerializationException on malformed json`() = runTest {
        server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE))
        try {
            dataSource.generateAnalysis("context")
            fail("Expected SocketTimeoutException")
        } catch (_: SocketTimeoutException) {
            // Expected
        }

        val malformedResponse = """
            {
              "id": "chatcmpl-test-3",
              "choices": [
                {
                  "index": 0,
                  "message": {
                    "role": "assistant",
                    "content": "Not a valid json for analysis"
                  }
                }
              ]
            }
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(200).setBody(malformedResponse))
        try {
            dataSource.generateAnalysis("context")
            fail("Expected SerializationException")
        } catch (_: SerializationException) {
            // Expected
        }
    }

    private fun escapeJsonString(value: String): String {
        val escaped = value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\b", "\\b")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
        return "\"$escaped\""
    }
}
