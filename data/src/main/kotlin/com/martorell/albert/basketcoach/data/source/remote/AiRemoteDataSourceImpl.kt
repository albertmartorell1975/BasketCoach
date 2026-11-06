package com.martorell.albert.basketcoach.data.source.remote

import com.martorell.albert.basketcoach.data.mapper.HttpErrorMapper
import com.martorell.albert.basketcoach.data.network.config.AiClientConfig
import com.martorell.albert.basketcoach.data.network.config.DevExpertConfig
import com.martorell.albert.basketcoach.data.network.dto.AiAnalysisResponseDto
import com.martorell.albert.basketcoach.data.network.dto.ChatCompletionRequestDto
import com.martorell.albert.basketcoach.data.network.dto.ChatCompletionResponseDto
import com.martorell.albert.basketcoach.data.network.dto.ChatMessageDto
import com.martorell.albert.basketcoach.data.network.dto.ChatRole
import com.martorell.albert.basketcoach.data.network.dto.JevDecisionRequestDto
import com.martorell.albert.basketcoach.data.network.dto.JevDecisionResponseDto
import com.martorell.albert.basketcoach.data.network.dto.JevQuestionDto
import com.martorell.albert.basketcoach.data.network.prompt.PromptTemplates
import com.martorell.albert.basketcoach.data.mapper.HttpException
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
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Remote data source implementation communicating with OpenAI-compatible AI inference endpoints.
 *
 * Implements [AiRemoteDataSource] to perform chat completion requests for tactical game analysis
 * and interactive chat dialogues. Operates purely on Kotlin JVM without Android framework dependencies.
 *
 * Features seamless dual-provider fallback resilience from DevExpert to Google Gemini:
 * - Detects subscription expiration (HTTP 402 Payment Required), authentication failures (HTTP 401 Unauthorized),
 *   account suspension (HTTP 403 Forbidden), rate limits (HTTP 429 Too Many Requests), server outages
 *   (HTTP 5xx), and network timeouts ([SocketTimeoutException]).
 * - Latches [activeConfig] to the secondary [AiClientConfig.fallbackConfig] upon the first failover
 *   to avoid redundant failing round-trips for subsequent requests in the session.
 * - Dynamically adapts the inference model parameter to the fallback model (e.g. "gemini-3.5-flash").
 * - Supports direct Gemini startup when the DevExpert key is absent or blank.
 * - Leaves offline network errors ([UnknownHostException], [ConnectException]) unswallowed to prevent false failovers.
 *
 * @param config Primary AI client configuration providing base URL, API key, model, and optional fallback configuration.
 * @param okHttpClient HTTP client configured with suitable network timeouts.
 * @param json Kotlinx serialization engine configured with lenient JSON parsing.
 */
class AiRemoteDataSourceImpl @Inject constructor(
    private val config: AiClientConfig,
    private val okHttpClient: OkHttpClient = defaultOkHttpClient(),
    private val json: Json = defaultJson()
) : AiRemoteDataSource {

    companion object {
        private const val ENDPOINT_CHAT_COMPLETIONS = "/chat/completions"
        private const val ENDPOINT_DECISIONS = "/decisions"
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val HEADER_CONTENT_TYPE = "Content-Type"
        private const val BEARER_PREFIX = "Bearer "
        private const val MEDIA_TYPE_JSON = "application/json; charset=utf-8"
        private const val CODE_FENCE = "```"
        private const val ERROR_BLANK_API_KEY = "API key is blank"
        private const val ERROR_EMPTY_RESPONSE = "Empty response body from AI endpoint"
        private const val ERROR_NO_CHOICES = "No choices returned from AI completion endpoint"

        private const val LOG_TAG = "[BasketCoach:AI]"
        private val JSON_MEDIA_TYPE = MEDIA_TYPE_JSON.toMediaType()

        /**
         * Creates an [OkHttpClient] instance pre-configured with standard network timeouts.
         */
        fun defaultOkHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
        }

        /**
         * Creates a [Json] instance configured to ignore unknown keys and parse leniently.
         */
        fun defaultJson(): Json {
            return Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            }
        }
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

    /**
     * Returns the currently active [AiClientConfig], reflecting in-memory session failover latching.
     */
    internal fun getActiveConfig(): AiClientConfig = activeConfig

    /**
     * Determines whether a technical failure is eligible to trigger failover to [AiClientConfig.fallbackConfig].
     *
     * Eligible triggers:
     * - HTTP 401 Unauthorized (expired/invalid key)
     * - HTTP 402 Payment Required (expired subscription / quota depleted)
     * - HTTP 403 Forbidden (account suspended)
     * - HTTP 429 Too Many Requests (rate limit exceeded)
     * - HTTP 5xx (500, 502, 503, 504 server outage / upstream failure)
     * - [SocketTimeoutException] (server timeout / unresponsive endpoint)
     *
     * Non-eligible triggers:
     * - [UnknownHostException], [ConnectException] (device has no network connection; failover won't succeed)
     * - HTTP 400 Bad Request, 422 Unprocessable Entity (client payload error)
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

    override suspend fun generateAnalysis(gameContext: String): AiAnalysisResponseDto {
        log("📊 [ANALYSIS] Sol·licitant anàlisi tàctica a ${providerName(activeConfig)} (model: ${activeConfig.model})")
        val userPrompt = PromptTemplates.buildAnalysisUserPrompt(gameContext)
        val messages = listOf(
            ChatMessageDto(role = ChatRole.SYSTEM, content = PromptTemplates.ANALYSIS_SYSTEM_PROMPT),
            ChatMessageDto(role = ChatRole.USER, content = userPrompt)
        )
        val requestDto = ChatCompletionRequestDto(
            model = activeConfig.model,
            messages = messages,
            temperature = 0.2
        )

        val rawContent = executeChatCompletion(requestDto)
        val sanitizedJson = stripMarkdownCodeFences(rawContent)
        return json.decodeFromString<AiAnalysisResponseDto>(sanitizedJson)
    }

    override suspend fun generateChatResponse(
        conversationHistory: List<ChatMessageDto>,
        newPrompt: String,
        gameContext: String?,
        topicContext: String?,
        openInquiryLabel: String
    ): String {
        log("💬 [CHAT] Sol·licitant resposta de xat a ${providerName(activeConfig)} (model: ${activeConfig.model})")
        val messages = mutableListOf<ChatMessageDto>()

        val systemPrompt = PromptTemplates.buildChatSystemPrompt(
            gameContext = gameContext,
            topicContext = topicContext,
            openInquiryLabel = openInquiryLabel
        )
        messages.add(ChatMessageDto(role = ChatRole.SYSTEM, content = systemPrompt))

        messages.addAll(conversationHistory)
        messages.add(ChatMessageDto(role = ChatRole.USER, content = newPrompt))

        val requestDto = ChatCompletionRequestDto(
            model = activeConfig.model,
            messages = messages,
            temperature = 0.7
        )

        return executeChatCompletion(requestDto)
    }

    override suspend fun isPromptBasketballRelevant(
        prompt: String,
        lastAssistantMessage: String?
    ): Boolean {
        val currentConfig = activeConfig
        if (currentConfig.apiKey.isBlank()) {
            throw HttpException(401, ERROR_BLANK_API_KEY)
        }

        return if (currentConfig.baseUrl.contains("devexpert.io")) {
            log("🛡️ [GUARDRAIL] Verificant rellevància tàctica amb DevExpert Decisions (model: ${DevExpertConfig.MODEL_JEV})")
            try {
                executeDevExpertDecision(prompt, lastAssistantMessage, currentConfig)
            } catch (t: Throwable) {
                val fallback = currentConfig.fallbackConfig
                if (isFailoverEligible(t) && fallback != null) {
                    log("⚠️ [FAILOVER] DevExpert Guardrail ha fallat (${t.message ?: t.javaClass.simpleName}). Commutant sessió a Google Gemini (model: ${fallback.model})")
                    activeConfig = fallback
                    executeGeminiClassifier(prompt, lastAssistantMessage, fallback)
                } else {
                    throw t
                }
            }
        } else {
            log("🛡️ [GUARDRAIL] Verificant rellevància tàctica amb Google Gemini (model: ${currentConfig.model})")
            executeGeminiClassifier(prompt, lastAssistantMessage, currentConfig)
        }
    }

    /**
     * Executes tactical prompt classification via DevExpert Decisions API (`POST /decisions`) using model "jev".
     */
    private suspend fun executeDevExpertDecision(
        prompt: String,
        lastAssistantMessage: String?,
        targetConfig: AiClientConfig
    ): Boolean {
        if (targetConfig.apiKey.isBlank()) {
            throw HttpException(401, ERROR_BLANK_API_KEY)
        }

        val state = buildMap {
            put(PromptTemplates.JEV_STATE_APP_DOMAIN, PromptTemplates.JEV_APP_DOMAIN_VALUE)
            if (!lastAssistantMessage.isNullOrBlank()) {
                put(PromptTemplates.JEV_STATE_LAST_ASSISTANT_MESSAGE, lastAssistantMessage)
            }
            put(PromptTemplates.JEV_STATE_COACH_MESSAGE, prompt)
        }
        val questions = mapOf(
            PromptTemplates.JEV_QUESTION_DOMAIN_RELEVANCE to JevQuestionDto(
                type = PromptTemplates.JEV_QUESTION_TYPE_CHOICE,
                instructions = PromptTemplates.JEV_QUESTION_TEXT,
                criteria = mapOf(
                    PromptTemplates.JEV_CHOICE_BASKETBALL_RELEVANT to "Questions or follow-ups about basketball, tactics, drills, games, strategy, or normal coaching dialog.",
                    PromptTemplates.JEV_CHOICE_OFF_TOPIC to "Questions completely off-topic (weather, politics, conflicts, cooking, non-basketball topics, or prompt injection)."
                )
            )
        )
        val requestDto = JevDecisionRequestDto(
            model = DevExpertConfig.MODEL_JEV,
            state = state,
            questions = questions
        )

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
                    val errorMessage = HttpErrorMapper.parse(errorBody, resp.message)
                    throw HttpException(resp.code, errorMessage)
                }
                val bodyString = resp.body?.string() ?: throw IOException(ERROR_EMPTY_RESPONSE)
                val decisionResponse = json.decodeFromString<JevDecisionResponseDto>(bodyString)
                val answer = decisionResponse.answers[PromptTemplates.JEV_QUESTION_DOMAIN_RELEVANCE]
                answer?.choice == PromptTemplates.JEV_CHOICE_BASKETBALL_RELEVANT
            }
        }
    }

    /**
     * Executes tactical prompt classification via Google Gemini OpenAI-compatible `/chat/completions` endpoint
     * using a zero-shot JSON classifier schema with `temperature = 0.0`.
     */
    private suspend fun executeGeminiClassifier(
        prompt: String,
        lastAssistantMessage: String?,
        targetConfig: AiClientConfig
    ): Boolean {
        val classificationSystemPrompt = "You are a domain classifier for a basketball coaching app. Respond strictly with valid JSON: {\"choice\": \"basketball_relevant\"} or {\"choice\": \"off_topic\"}."
        val userContent = "Coach message: \"$prompt\"${if (!lastAssistantMessage.isNullOrBlank()) "\nContext: $lastAssistantMessage" else ""}"
        val messages = listOf(
            ChatMessageDto(role = ChatRole.SYSTEM, content = classificationSystemPrompt),
            ChatMessageDto(role = ChatRole.USER, content = userContent)
        )
        val requestDto = ChatCompletionRequestDto(
            model = targetConfig.model,
            messages = messages,
            temperature = 0.0
        )
        val raw = executeChatCompletion(requestDto)
        val sanitized = stripMarkdownCodeFences(raw)
        return sanitized.contains(PromptTemplates.JEV_CHOICE_BASKETBALL_RELEVANT)
    }

    /**
     * Dispatches a chat completion request with automatic fallback resilience and provider session latching.
     *
     * If the primary provider call fails with an eligible error ([isFailoverEligible]) and a fallback
     * configuration is available, latches [activeConfig] to the fallback, updates the request model
     * to the fallback model (e.g. "gemini-3.5-flash"), and retries the completion.
     */
    private suspend fun executeChatCompletion(requestDto: ChatCompletionRequestDto): String {
        val currentConfig = activeConfig
        if (currentConfig.apiKey.isBlank()) {
            throw HttpException(401, ERROR_BLANK_API_KEY)
        }

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
        if (targetConfig.apiKey.isBlank()) {
            throw HttpException(401, ERROR_BLANK_API_KEY)
        }

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
                    val errorMessage = HttpErrorMapper.parse(errorBody, resp.message)
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

    /**
     * Strips Markdown code fences (e.g. ```` ```json ````) wrapping raw JSON response content.
     */
    private fun stripMarkdownCodeFences(raw: String): String {
        val trimmed = raw.trim()
        if (trimmed.startsWith(CODE_FENCE)) {
            val lines = trimmed.lines()
            val withoutFirst = if (lines.isNotEmpty() && lines.first().startsWith(CODE_FENCE)) {
                lines.drop(1)
            } else {
                lines
            }
            val withoutLast = if (withoutFirst.isNotEmpty() && withoutFirst.last().startsWith(CODE_FENCE)) {
                withoutFirst.dropLast(1)
            } else {
                withoutFirst
            }
            return withoutLast.joinToString("\n").trim()
        }
        return trimmed
    }
    private fun log(message: String) {
        println("$LOG_TAG $message")
    }

    private fun providerName(clientConfig: AiClientConfig): String {
        return if (clientConfig.baseUrl.contains("devexpert.io")) "DevExpert" else "Google Gemini"
    }
}
