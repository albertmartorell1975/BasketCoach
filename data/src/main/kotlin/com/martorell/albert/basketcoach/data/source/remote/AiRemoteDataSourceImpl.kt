package com.martorell.albert.basketcoach.data.source.remote

import com.martorell.albert.basketcoach.data.util.HttpErrorParser
import com.martorell.albert.basketcoach.data.network.HttpException
import com.martorell.albert.basketcoach.data.network.config.AiClientConfig
import com.martorell.albert.basketcoach.data.network.dto.ChatCompletionRequestDto
import com.martorell.albert.basketcoach.data.network.dto.ChatCompletionResponseDto
import com.martorell.albert.basketcoach.data.network.dto.ChatMessageDto
import com.martorell.albert.basketcoach.data.network.dto.JevDecisionRequestDto
import com.martorell.albert.basketcoach.data.network.dto.JevDecisionResponseDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

/**
 * Remote data source implementation providing mechanical HTTP transport for OpenAI-compatible AI endpoints.
 *
 * Implements [AiRemoteDataSource] to perform chat completions and decision classifications without
 * coupling to domain prompt generation, Markdown sanitization, or presentation context.
 * Operates purely on Kotlin JVM without Android framework dependencies.
 *
 * Features seamless dual-provider fallback resilience from DevExpert to Google Gemini:
 * - Detects authentication failures (HTTP 401), subscription expiration (HTTP 402), account
 *   suspension (HTTP 403), rate limits (HTTP 429), server outages (HTTP 5xx), and timeouts ([SocketTimeoutException]).
 * - Latches [activeConfig] to the secondary [AiClientConfig.fallbackConfig] upon the first failover
 *   to avoid redundant failing round-trips for subsequent requests in the session.
 * - Dynamically adapts the inference model parameter to the fallback model (e.g. "gemini-3.5-flash").
 * - Supports direct Gemini startup when the DevExpert key is absent or blank.
 * - Leaves offline network errors ([UnknownHostException], [ConnectException]) unswallowed to prevent false failovers.
 *
 * @param config Primary AI client configuration providing base URL, API key, model, and optional fallback configuration.
 * @param okHttpClient HTTP client configured with suitable network timeouts injected via Hilt.
 * @param json Kotlinx serialization engine configured with lenient JSON parsing injected via Hilt.
 */
class AiRemoteDataSourceImpl @Inject constructor(
    private val config: AiClientConfig,
    private val okHttpClient: OkHttpClient,
    private val json: Json
) : AiRemoteDataSource {

    companion object {
        private const val ENDPOINT_CHAT_COMPLETIONS = "/chat/completions"
        private const val ENDPOINT_DECISIONS = "/decisions"
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val HEADER_CONTENT_TYPE = "Content-Type"
        private const val BEARER_PREFIX = "Bearer "
        private const val MEDIA_TYPE_JSON = "application/json; charset=utf-8"
        private const val ERROR_EMPTY_RESPONSE = "Empty response body from AI endpoint"
        private const val ERROR_NO_CHOICES = "No choices returned from AI completion endpoint"

        private const val LOG_TAG = "[BasketCoach:AI]"
        private val JSON_MEDIA_TYPE = MEDIA_TYPE_JSON.toMediaType()
    }

    @Volatile
    private var activeConfig: AiClientConfig = if (config.apiKey.isBlank() && config.fallbackConfig != null) {
        config.fallbackConfig
    } else {
        config
    }

    init {
        log("🚀 [INIT] Data source inicialitzat. Proveïdor actiu: ${providerName(activeConfig)} (model: ${activeConfig.model})")
    }

    override fun isUsingDevExpert(): Boolean = activeConfig.baseUrl.contains("devexpert.io")

    /**
     * Returns the currently active [AiClientConfig], reflecting in-memory session failover latching.
     */
    internal fun getActiveConfig(): AiClientConfig = activeConfig

    /**
     * Determines whether a technical failure is eligible to trigger failover to [AiClientConfig.fallbackConfig].
     */
    internal fun isFailoverEligible(throwable: Throwable): Boolean {
        if (throwable is UnknownHostException || throwable is ConnectException) {
            return false
        }
        if (throwable.cause is UnknownHostException || throwable.cause is ConnectException) {
            return false
        }
        return when (throwable) {
            is HttpException -> throwable.code in listOf(401, 402, 403, 429) || throwable.code >= 500
            is SocketTimeoutException -> true
            else -> throwable.cause is SocketTimeoutException
        }
    }

    override suspend fun executeChatCompletion(
        messages: List<ChatMessageDto>,
        temperature: Double
    ): String {
        log("💬 [COMPLETION] Sol·licitant inferència a ${providerName(activeConfig)} (model: ${activeConfig.model}, temp: $temperature)")
        val requestDto = ChatCompletionRequestDto(
            model = activeConfig.model,
            messages = messages,
            temperature = temperature
        )
        return executeChatCompletionWithFailover(requestDto)
    }

    override suspend fun executeDecision(
        request: JevDecisionRequestDto
    ): JevDecisionResponseDto {
        val currentConfig = activeConfig
        log("🛡️ [DECISIONS] Sol·licitant decisió a ${providerName(currentConfig)} (model: ${request.model})")

        return try {
            executeSingleDecision(request, currentConfig)
        } catch (t: Throwable) {
            val fallback = currentConfig.fallbackConfig
            if (isFailoverEligible(t) && fallback != null) {
                log("⚠️ [FAILOVER] DevExpert Decisions ha fallat (${t.message ?: t.javaClass.simpleName}). Commutant sessió a Google Gemini (model: ${fallback.model})")
                activeConfig = fallback
            }
            throw t
        }
    }

    /**
     * Dispatches a single HTTP POST request to the `/decisions` endpoint of [targetConfig].
     */
    private suspend fun executeSingleDecision(
        requestDto: JevDecisionRequestDto,
        targetConfig: AiClientConfig
    ): JevDecisionResponseDto {
        val requestBodyString = json.encodeToString(JevDecisionRequestDto.serializer(), requestDto)
        val requestUrl = "${targetConfig.baseUrl.trimEnd('/')}$ENDPOINT_DECISIONS"

        val request = Request.Builder()
            .url(requestUrl)
            .addHeader(HEADER_AUTHORIZATION, "$BEARER_PREFIX${targetConfig.apiKey.trim()}")
            .addHeader(HEADER_CONTENT_TYPE, MEDIA_TYPE_JSON)
            .post(requestBodyString.toRequestBody(JSON_MEDIA_TYPE))
            .build()

        return withContext(Dispatchers.IO) {
            val response = okHttpClient.newCall(request).execute()
            response.use { resp ->
                if (!resp.isSuccessful) {
                    val errorBody = resp.body?.string().orEmpty()
                    val errorMessage = HttpErrorParser.parse(errorBody, resp.message)
                    throw HttpException(resp.code, errorMessage)
                }
                val bodyString = resp.body?.string() ?: throw IOException(ERROR_EMPTY_RESPONSE)
                json.decodeFromString<JevDecisionResponseDto>(bodyString)
            }
        }
    }

    /**
     * Dispatches a chat completion request with automatic fallback resilience and provider session latching.
     */
    private suspend fun executeChatCompletionWithFailover(requestDto: ChatCompletionRequestDto): String {
        val currentConfig = activeConfig

        val adjustedRequest = if (requestDto.model != currentConfig.model) {
            requestDto.copy(model = currentConfig.model)
        } else {
            requestDto
        }

        return try {
            executeSingleChatCompletion(adjustedRequest, currentConfig)
        } catch (t: Throwable) {
            val fallback = currentConfig.fallbackConfig
            if (isFailoverEligible(t) && fallback != null) {
                log("⚠️ [FAILOVER] Petició a ${providerName(currentConfig)} ha fallat (${t.message ?: t.javaClass.simpleName}). Commutant sessió a Google Gemini (model: ${fallback.model})")
                activeConfig = fallback
                val retryRequest = adjustedRequest.copy(model = fallback.model)
                executeSingleChatCompletion(retryRequest, fallback)
            } else {
                throw t
            }
        }
    }

    /**
     * Dispatches a single HTTP POST request to the `/chat/completions` endpoint of [targetConfig].
     */
    private suspend fun executeSingleChatCompletion(
        requestDto: ChatCompletionRequestDto,
        targetConfig: AiClientConfig
    ): String {
        val requestBodyString = json.encodeToString(ChatCompletionRequestDto.serializer(), requestDto)
        val requestUrl = "${targetConfig.baseUrl.trimEnd('/')}$ENDPOINT_CHAT_COMPLETIONS"

        val request = Request.Builder()
            .url(requestUrl)
            .addHeader(HEADER_AUTHORIZATION, "$BEARER_PREFIX${targetConfig.apiKey.trim()}")
            .addHeader(HEADER_CONTENT_TYPE, MEDIA_TYPE_JSON)
            .post(requestBodyString.toRequestBody(JSON_MEDIA_TYPE))
            .build()

        return withContext(Dispatchers.IO) {
            val response = okHttpClient.newCall(request).execute()
            response.use { resp ->
                if (!resp.isSuccessful) {
                    val errorBody = resp.body?.string().orEmpty()
                    val errorMessage = HttpErrorParser.parse(errorBody, resp.message)
                    throw HttpException(resp.code, errorMessage)
                }

                val bodyString = resp.body?.string() ?: throw IOException(ERROR_EMPTY_RESPONSE)
                val responseDto = json.decodeFromString<ChatCompletionResponseDto>(bodyString)
                val firstChoice = responseDto.choices.firstOrNull()
                    ?: throw IOException(ERROR_NO_CHOICES)

                firstChoice.message.content.trim()
            }
        }
    }

    private fun log(message: String) {
        println("$LOG_TAG $message")
    }

    private fun providerName(clientConfig: AiClientConfig): String {
        return if (clientConfig.baseUrl.contains("devexpert.io")) "DevExpert" else "Google Gemini"
    }
}
