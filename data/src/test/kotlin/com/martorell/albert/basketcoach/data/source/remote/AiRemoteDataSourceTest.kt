package com.martorell.albert.basketcoach.data.source.remote

import com.martorell.albert.basketcoach.data.network.config.AiClientConfig
import com.martorell.albert.basketcoach.data.network.dto.ChatMessageDto
import com.martorell.albert.basketcoach.data.network.dto.ChatRole
import com.martorell.albert.basketcoach.data.mapper.HttpException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.net.InetAddress
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class AiRemoteDataSourceTest {

    private lateinit var server: MockWebServer
    private lateinit var dataSource: AiRemoteDataSourceImpl
    private val testJson = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }
    private lateinit var shortTimeoutOkHttpClient: OkHttpClient

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        val baseUrl = server.url("/v1").toString()
        val config = AiClientConfig(
            apiKey = "test-api-key",
            baseUrl = baseUrl,
            model = "chat",
            fallbackConfig = null
        )
        shortTimeoutOkHttpClient = OkHttpClient.Builder()
            .connectTimeout(500, TimeUnit.MILLISECONDS)
            .readTimeout(500, TimeUnit.MILLISECONDS)
            .writeTimeout(500, TimeUnit.MILLISECONDS)
            .build()

        dataSource = AiRemoteDataSourceImpl(
            config = config,
            okHttpClient = shortTimeoutOkHttpClient,
            json = testJson
        )
    }

    @After
    fun tearDown() {
        server.shutdown()
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

    @Test
    fun `generateAnalysis and generateChatResponse send auth header and parse fenced or plain responses`() = runTest {
        val jsonPayload = """
            {
              "insights": [
                {
                  "id": "insight-1",
                  "title": "Control del Rebot Defensiu",
                  "description": "Cedim massa segones opcions en derrotes.",
                  "category": "DEFENSE",
                  "relatedGameRounds": [1, 2],
                  "type": "IMPROVEMENT",
                  "contextTag": "Fase Defensiva",
                  "metric": {
                    "primaryLabel": "Rebots Cedits",
                    "primaryValue": "13.2",
                    "primarySubValue": "per partit",
                    "secondaryLabel": "Diferencial",
                    "secondaryValue": "-8.4 net",
                    "secondaryTag": "dataset:reb",
                    "sentiment": "NEGATIVE"
                  },
                  "priority": "Alta",
                  "footerNote": "Prioritat màxima.",
                  "exploreChatPrompt": "Com millorem el rebot?"
                }
              ],
              "drills": [
                {
                  "id": "drill-1",
                  "name": "3c3 Box out",
                  "objective": "Tancar rebot",
                  "instructions": ["Bloquejar 3 segons"],
                  "focusArea": "Rebot"
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
                    "content": "Bona lectura Jordi."
                  }
                }
              ]
            }
        """.trimIndent()

        server.enqueue(MockResponse().setResponseCode(200).setBody(analysisResponse))
        server.enqueue(MockResponse().setResponseCode(200).setBody(chatResponse))

        val analysisResult = dataSource.generateAnalysis("context summary")
        assertEquals(1, analysisResult.insights.size)
        assertEquals("insight-1", analysisResult.insights[0].id)
        assertEquals("3c3 Box out", analysisResult.drills[0].name)

        val recordedAnalysis = server.takeRequest()
        assertEquals("Bearer test-api-key", recordedAnalysis.getHeader("Authorization"))
        assertEquals("/v1/chat/completions", recordedAnalysis.path)

        val chatResult = dataSource.generateChatResponse(
            conversationHistory = listOf(ChatMessageDto(role = ChatRole.USER, content = "Hola")),
            newPrompt = "Què fem amb el rebot?"
        )
        assertEquals("Bona lectura Jordi.", chatResult)

        val recordedChat = server.takeRequest()
        assertEquals("Bearer test-api-key", recordedChat.getHeader("Authorization"))
        assertEquals("/v1/chat/completions", recordedChat.path)
    }

    @Test
    fun `throws HttpException on 401 unauthorized and 500 server error`() = runTest {
        server.enqueue(MockResponse().setResponseCode(401).setBody("Unauthorized API key"))
        try {
            dataSource.generateAnalysis("context")
            fail("Expected HttpException 401")
        } catch (e: HttpException) {
            assertEquals(401, e.code)
        }

        server.enqueue(MockResponse().setResponseCode(500).setBody("Internal Server Error"))
        try {
            dataSource.generateChatResponse(emptyList(), "prompt")
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

    @Test
    fun isPromptBasketballRelevant_whenUsingDevExpert_parsesJevDecisionResponse() = runTest {
        val customDns = object : Dns {
            override fun lookup(hostname: String): List<InetAddress> {
                return listOf(InetAddress.getByName("127.0.0.1"))
            }
        }
        val devExpertClient = OkHttpClient.Builder().dns(customDns).build()
        val customDataSource = AiRemoteDataSourceImpl(
            config = AiClientConfig(apiKey = "key", baseUrl = "http://devexpert.io:" + server.port + "/v1", model = "chat", fallbackConfig = null),
            okHttpClient = devExpertClient,
            json = testJson
        )
        val jevResponseBody = """{"answers": {"domain_relevance": {"choice": "basketball_relevant"}}}"""
        server.enqueue(MockResponse().setResponseCode(200).setBody(jevResponseBody))
        val isRelevant = customDataSource.isPromptBasketballRelevant("Com tanquem el rebot?")
        assertEquals(true, isRelevant)
        val recordedRequest = server.takeRequest()
        assertEquals("/v1/decisions", recordedRequest.path)
    }
}
