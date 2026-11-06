package com.martorell.albert.basketcoach.data.source.remote

import com.martorell.albert.basketcoach.data.network.config.DevExpertConfig
import com.martorell.albert.basketcoach.data.network.config.GeminiConfig
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.net.InetAddress
import java.util.concurrent.TimeUnit

class AiRemoteDataSourceFallbackTest {

    private lateinit var server: MockWebServer
    private val testJson = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }
    private val testOkHttpClient = OkHttpClient.Builder().build()

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun isPromptBasketballRelevant_whenDevExpertReturns402OrTimeout_fallsBackToGeminiClassifier() = runTest {
        val customDns = object : Dns {
            override fun lookup(hostname: String): List<InetAddress> {
                return listOf(InetAddress.getByName(server.hostName))
            }
        }
        val okHttpClient = OkHttpClient.Builder()
            .dns(customDns)
            .connectTimeout(500, TimeUnit.MILLISECONDS)
            .readTimeout(500, TimeUnit.MILLISECONDS)
            .writeTimeout(500, TimeUnit.MILLISECONDS)
            .build()

        val geminiConfig = GeminiConfig.create(
            apiKey = "gemini-test-key",
            baseUrl = server.url("/gemini").toString()
        )
        val devExpertConfig = DevExpertConfig.create(
            apiKey = "devexpert-test-key",
            baseUrl = "http://devexpert.io:${server.port}/v1",
            fallbackConfig = geminiConfig
        )
        val dataSource = AiRemoteDataSourceImpl(
            config = devExpertConfig,
            okHttpClient = okHttpClient,
            json = testJson
        )

        // DevExpert /decisions returns 402 Payment Required (subscription expired)
        server.enqueue(MockResponse().setResponseCode(402).setBody("Payment Required - Subscription Expired"))
        // Gemini fallback /chat/completions returns JSON classification with mandatory id
        val geminiClassifierResponse = """
            {
              "id": "chatcmpl-gemini-classifier",
              "choices": [
                {
                  "index": 0,
                  "message": {
                    "role": "assistant",
                    "content": "{\"choice\": \"basketball_relevant\"}"
                  }
                }
              ]
            }
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(200).setBody(geminiClassifierResponse))

        val isRelevant = dataSource.isPromptBasketballRelevant("Com millorem el balanç defensiu?")
        assertTrue(isRelevant)

        val devExpertRequest = server.takeRequest()
        assertEquals("/v1/decisions", devExpertRequest.path)
        assertEquals("Bearer devexpert-test-key", devExpertRequest.getHeader("Authorization"))

        val geminiRequest = server.takeRequest()
        assertEquals("/gemini/chat/completions", geminiRequest.path)
        assertEquals("Bearer gemini-test-key", geminiRequest.getHeader("Authorization"))
        assertTrue(geminiRequest.body.readUtf8().contains(GeminiConfig.DEFAULT_MODEL))
        assertEquals(GeminiConfig.DEFAULT_MODEL, dataSource.getActiveConfig().model)
    }

    @Test
    fun executeChatCompletion_whenDevExpertFailsWith5xxOr401_retriesWithGeminiAndLatchesProvider() = runTest {
        val geminiConfig = GeminiConfig.create(
            apiKey = "gemini-test-key",
            baseUrl = server.url("/gemini").toString()
        )
        val devExpertConfig = DevExpertConfig.create(
            apiKey = "devexpert-test-key",
            baseUrl = server.url("/devexpert").toString(),
            fallbackConfig = geminiConfig
        )
        val dataSource = AiRemoteDataSourceImpl(
            config = devExpertConfig,
            okHttpClient = testOkHttpClient,
            json = testJson
        )

        // First request: DevExpert returns 503 Service Unavailable, Gemini succeeds
        server.enqueue(MockResponse().setResponseCode(503).setBody("Service Unavailable"))
        val geminiResponse1 = """
            {
              "id": "chatcmpl-gemini-1",
              "choices": [
                {
                  "index": 0,
                  "message": {
                    "role": "assistant",
                    "content": "Gemini fallback reply 1"
                  }
                }
              ]
            }
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(200).setBody(geminiResponse1))

        val reply1 = dataSource.generateChatResponse(emptyList(), "Com ataquem la zona 2-3?")
        assertEquals("Gemini fallback reply 1", reply1)

        val devExpertReq = server.takeRequest()
        assertEquals("/devexpert/chat/completions", devExpertReq.path)
        assertEquals("Bearer devexpert-test-key", devExpertReq.getHeader("Authorization"))

        val geminiReq1 = server.takeRequest()
        assertEquals("/gemini/chat/completions", geminiReq1.path)
        assertEquals("Bearer gemini-test-key", geminiReq1.getHeader("Authorization"))
        assertTrue(geminiReq1.body.readUtf8().contains(GeminiConfig.DEFAULT_MODEL))
        assertEquals(GeminiConfig.DEFAULT_MODEL, dataSource.getActiveConfig().model)

        // Subsequent request in same session: immediately routes to Gemini without DevExpert round-trip
        val geminiResponse2 = """
            {
              "id": "chatcmpl-gemini-2",
              "choices": [
                {
                  "index": 0,
                  "message": {
                    "role": "assistant",
                    "content": "Gemini fallback reply 2"
                  }
                }
              ]
            }
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(200).setBody(geminiResponse2))

        val reply2 = dataSource.generateChatResponse(emptyList(), "I contra una defensa individual?")
        assertEquals("Gemini fallback reply 2", reply2)

        val geminiReq2 = server.takeRequest()
        assertEquals("/gemini/chat/completions", geminiReq2.path)
        assertEquals("Bearer gemini-test-key", geminiReq2.getHeader("Authorization"))
        assertEquals(3, server.requestCount)
    }

    @Test
    fun directStartupWithGemini_whenDevExpertKeyMissing_routesDirectlyToGemini() = runTest {
        val geminiConfig = GeminiConfig.create(
            apiKey = "gemini-direct-key",
            baseUrl = server.url("/gemini").toString()
        )
        val missingDevExpertConfig = DevExpertConfig.create(
            apiKey = "",
            baseUrl = server.url("/devexpert").toString(),
            fallbackConfig = geminiConfig
        )
        val dataSource = AiRemoteDataSourceImpl(
            config = missingDevExpertConfig,
            okHttpClient = testOkHttpClient,
            json = testJson
        )

        assertEquals(GeminiConfig.DEFAULT_MODEL, dataSource.getActiveConfig().model)
        assertEquals("gemini-direct-key", dataSource.getActiveConfig().apiKey)

        val geminiResponse = """
            {
              "id": "chatcmpl-gemini-direct",
              "choices": [
                {
                  "index": 0,
                  "message": {
                    "role": "assistant",
                    "content": "Direct Gemini response"
                  }
                }
              ]
            }
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(200).setBody(geminiResponse))

        val reply = dataSource.generateChatResponse(emptyList(), "Consulta directa")
        assertEquals("Direct Gemini response", reply)

        val req = server.takeRequest()
        assertEquals("/gemini/chat/completions", req.path)
        assertEquals("Bearer gemini-direct-key", req.getHeader("Authorization"))
        assertTrue(req.body.readUtf8().contains(GeminiConfig.DEFAULT_MODEL))
        assertEquals(1, server.requestCount)
    }
}
