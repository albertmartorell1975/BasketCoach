package com.martorell.albert.basketcoach.data.network

import com.martorell.albert.basketcoach.data.network.config.AiClientConfig
import com.martorell.albert.basketcoach.data.network.client.DevExpertApiClientImpl
import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.MessageSender
import com.martorell.albert.basketcoach.domain.model.Result
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class DevExpertApiClientTest {

    private lateinit var server: MockWebServer
    private lateinit var client: DevExpertApiClientImpl

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

        client = DevExpertApiClientImpl(
            config = config,
            okHttpClient = shortTimeoutOkHttpClient
        )
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `generateAnalysis sends authorization header and parses 200 response with markdown code fence`() = runTest {
        val jsonPayload = """
            {
              "insights": [
                {
                  "id": "ins-1",
                  "title": "Turnover Crisis",
                  "description": "High turnover rate in losses",
                  "category": "OFFENSE",
                  "relatedGameRounds": [1, 2]
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
        val mockApiResponse = """
            {
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

        server.enqueue(MockResponse().setResponseCode(200).setBody(mockApiResponse))

        val result = client.generateAnalysis("Phase 1 summary")

        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(1, data.insights.size)
        assertEquals("Turnover Crisis", data.insights.first().title)
        assertEquals("OFFENSE", data.insights.first().category)
        assertEquals(1, data.drills.size)
        assertEquals("Pressure Passing", data.drills.first().name)

        val recordedRequest = server.takeRequest()
        assertEquals("POST", recordedRequest.method)
        assertEquals("/v1/chat/completions", recordedRequest.path)
        assertEquals("Bearer test-api-key", recordedRequest.getHeader("Authorization"))
    }

    @Test
    fun `generateChatResponse returns completion content`() = runTest {
        val mockApiResponse = """
            {
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

        server.enqueue(MockResponse().setResponseCode(200).setBody(mockApiResponse))

        val history = listOf(
            ChatMessage("1", MessageSender.COACH, "How do we improve rebounds?", 1000L)
        )
        val result = client.generateChatResponse(history, "Any specific drills?")

        assertTrue(result is Result.Success)
        assertEquals("Focus on boxing out and protecting defensive rebounds.", (result as Result.Success).data)

        val recordedRequest = server.takeRequest()
        assertEquals("/v1/chat/completions", recordedRequest.path)
    }

    @Test
    fun `returns Unauthorized when apiKey is blank`() = runTest {
        val blankKeyConfig = AiClientConfig(apiKey = "", baseUrl = server.url("/v1").toString(), model = "chat")
        val unauthClient = DevExpertApiClientImpl(config = blankKeyConfig)

        val result = unauthClient.generateAnalysis("context")

        assertTrue(result is Result.Error)
        assertEquals(DomainError.Network.Unauthorized, (result as Result.Error).error)
        assertEquals(0, server.requestCount)
    }

    @Test
    fun `maps 401 response to DomainError Network Unauthorized`() = runTest {
        server.enqueue(MockResponse().setResponseCode(401).setBody("Unauthorized"))

        val result = client.generateAnalysis("context")

        assertTrue(result is Result.Error)
        assertEquals(DomainError.Network.Unauthorized, (result as Result.Error).error)
    }

    @Test
    fun `maps 500 server error to DomainError Network ServerError`() = runTest {
        server.enqueue(MockResponse().setResponseCode(500).setBody("Internal Server Error"))

        val result = client.generateAnalysis("context")

        assertTrue(result is Result.Error)
        val error = (result as Result.Error).error
        assertTrue(error is DomainError.Network.ServerError)
        assertEquals(500, (error as DomainError.Network.ServerError).code)
    }

    @Test
    fun `maps socket timeout to DomainError Network Timeout`() = runTest {
        val timeoutResponse = MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE)
        server.enqueue(timeoutResponse)

        val result = client.generateAnalysis("context")

        assertTrue(result is Result.Error)
        assertEquals(DomainError.Network.Timeout, (result as Result.Error).error)
    }

    @Test
    fun `maps malformed json response to DomainError Generic`() = runTest {
        val mockApiResponse = """
            {
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
        server.enqueue(MockResponse().setResponseCode(200).setBody(mockApiResponse))

        val result = client.generateAnalysis("context")

        assertTrue(result is Result.Error)
        val error = (result as Result.Error).error
        assertTrue(error is DomainError.Generic)
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
