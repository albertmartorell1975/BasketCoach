package com.martorell.albert.basketcoach.data.source.remote

import com.martorell.albert.basketcoach.data.network.HttpException
import com.martorell.albert.basketcoach.data.network.config.AiClientConfig
import com.martorell.albert.basketcoach.data.network.dto.ChatMessageDto
import com.martorell.albert.basketcoach.data.network.dto.ChatRole
import com.martorell.albert.basketcoach.data.network.dto.JevDecisionRequestDto
import com.martorell.albert.basketcoach.data.network.dto.JevQuestionDto
import kotlinx.coroutines.test.runTest
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

    @Test
    fun `executeChatCompletion sends Bearer auth and returns raw model content`() = runTest {
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

        server.enqueue(MockResponse().setResponseCode(200).setBody(chatResponse))

        val result = dataSource.executeChatCompletion(
            messages = listOf(ChatMessageDto(role = ChatRole.USER, content = "Hola")),
            temperature = 0.7
        )
        assertEquals("Bona lectura Jordi.", result)

        val recorded = server.takeRequest()
        assertEquals("Bearer test-api-key", recorded.getHeader("Authorization"))
        assertEquals("/v1/chat/completions", recorded.path)
        assertTrue(recorded.body.readUtf8().contains("Hola"))
    }

    @Test
    fun `throws HttpException on 401 unauthorized and 500 server error`() = runTest {
        server.enqueue(MockResponse().setResponseCode(401).setBody("Unauthorized API key"))
        try {
            dataSource.executeChatCompletion(emptyList(), 0.7)
            fail("Expected HttpException 401")
        } catch (e: HttpException) {
            assertEquals(401, e.code)
        }

        server.enqueue(MockResponse().setResponseCode(500).setBody("Internal Server Error"))
        try {
            dataSource.executeChatCompletion(emptyList(), 0.7)
            fail("Expected HttpException 500")
        } catch (e: HttpException) {
            assertEquals(500, e.code)
        }
    }

    @Test
    fun `throws SocketTimeoutException on timeout`() = runTest {
        server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE))
        try {
            dataSource.executeChatCompletion(emptyList(), 0.7)
            fail("Expected SocketTimeoutException")
        } catch (_: SocketTimeoutException) {
            // Expected
        }
    }

    @Test
    fun `executeDecision when using DevExpert parses JevDecisionResponse`() = runTest {
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

        val request = JevDecisionRequestDto(
            model = "jev",
            state = mapOf("coach_message" to "Com tanquem el rebot?"),
            questions = mapOf(
                "domain_relevance" to JevQuestionDto(
                    type = "choice",
                    instructions = "Check relevance",
                    criteria = mapOf("basketball_relevant" to "Relevant")
                )
            )
        )
        val response = customDataSource.executeDecision(request)
        assertEquals("basketball_relevant", response.answers["domain_relevance"]?.choice)
        assertTrue(customDataSource.isUsingDevExpert())

        val recordedRequest = server.takeRequest()
        assertEquals("/v1/decisions", recordedRequest.path)
    }
}
