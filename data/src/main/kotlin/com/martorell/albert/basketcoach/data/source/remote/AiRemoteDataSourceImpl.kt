package com.martorell.albert.basketcoach.data.source.remote

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
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Remote data source implementation communicating with OpenAI-compatible AI inference endpoints.
 *
 * Implements [AiRemoteDataSource] to perform chat completion requests for tactical game analysis
 * and interactive chat dialogues. Operates purely on Kotlin JVM without Android framework dependencies.
 *
 * @param config AI client configuration providing base URL, API key, and model identifiers.
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

    /**
     * Sends formatted Phase 1 match context to the AI inference endpoint and parses the response into [AiAnalysisResponseDto].
     *
     * @param gameContext Formatted textual summary of Phase 1 games.
     * @return Deserialized [AiAnalysisResponseDto] containing insights and practice drills.
     */
    override suspend fun generateAnalysis(gameContext: String): AiAnalysisResponseDto {
        val userPrompt = PromptTemplates.buildAnalysisUserPrompt(gameContext)
        val messages = listOf(
            ChatMessageDto(role = ChatRole.SYSTEM, content = PromptTemplates.ANALYSIS_SYSTEM_PROMPT),
            ChatMessageDto(role = ChatRole.USER, content = userPrompt)
        )
        val requestDto = ChatCompletionRequestDto(
            model = config.model,
            messages = messages,
            temperature = 0.2
        )

        val rawContent = executeChatCompletion(requestDto)
        val sanitizedJson = stripMarkdownCodeFences(rawContent)
        return json.decodeFromString<AiAnalysisResponseDto>(sanitizedJson)
    }

    /**
     * Dispatches an interactive chat conversation history and user prompt to the AI completion endpoint.
     *
     * @param conversationHistory Prior messages exchanged in the current session.
     * @param newPrompt The new query or command provided by the coach.
     * @param gameContext Optional grounding context describing Phase 1 performance.
     * @param topicContext Optional grounding context describing the active intervention area.
     * @param openInquiryLabel Localized label of the open consultation thread.
     * @return Raw text response generated by the AI assistant.
     */
    override suspend fun generateChatResponse(
        conversationHistory: List<ChatMessageDto>,
        newPrompt: String,
        gameContext: String?,
        topicContext: String?,
        openInquiryLabel: String
    ): String {
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
            model = config.model,
            messages = messages,
            temperature = 0.7
        )

        return executeChatCompletion(requestDto)
    }

    override suspend fun isPromptBasketballRelevant(
        prompt: String,
        lastAssistantMessage: String?
    ): Boolean {
        if (config.apiKey.isBlank()) {
            throw HttpException(401, ERROR_BLANK_API_KEY)
        }

        return if (config.baseUrl.contains("devexpert.io")) {
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
            val requestUrl = "${config.baseUrl.trimEnd('/')}$ENDPOINT_DECISIONS"

            val request = Request.Builder()
                .url(requestUrl)
                .addHeader(HEADER_AUTHORIZATION, "$BEARER_PREFIX${config.apiKey.trim()}")
                .addHeader(HEADER_CONTENT_TYPE, MEDIA_TYPE_JSON)
                .post(requestBodyString.toRequestBody(JSON_MEDIA_TYPE))
                .build()

            withContext(Dispatchers.IO) {
                val response = okHttpClient.newCall(request).execute()
                response.use { resp ->
                    if (!resp.isSuccessful) {
                        val errorBody = resp.body?.string().orEmpty()
                        val errorMessage = if (errorBody.isNotBlank()) errorBody else resp.message
                        throw HttpException(resp.code, errorMessage)
                    }
                    val bodyString = resp.body?.string() ?: throw IOException(ERROR_EMPTY_RESPONSE)
                    val decisionResponse = json.decodeFromString<JevDecisionResponseDto>(bodyString)
                    val answer = decisionResponse.answers[PromptTemplates.JEV_QUESTION_DOMAIN_RELEVANCE]
                    answer?.choice == PromptTemplates.JEV_CHOICE_BASKETBALL_RELEVANT
                }
            }
        } else {
            // Gemini Fallback Mode
            val classificationSystemPrompt = "You are a domain classifier for a basketball coaching app. Respond strictly with valid JSON: {\"choice\": \"basketball_relevant\"} or {\"choice\": \"off_topic\"}."
            val userContent = "Coach message: \"$prompt\"${if (!lastAssistantMessage.isNullOrBlank()) "\nContext: $lastAssistantMessage" else ""}"
            val messages = listOf(
                ChatMessageDto(role = ChatRole.SYSTEM, content = classificationSystemPrompt),
                ChatMessageDto(role = ChatRole.USER, content = userContent)
            )
            val requestDto = ChatCompletionRequestDto(
                model = config.model,
                messages = messages,
                temperature = 0.0
            )
            val raw = executeChatCompletion(requestDto)
            val sanitized = stripMarkdownCodeFences(raw)
            sanitized.contains(PromptTemplates.JEV_CHOICE_BASKETBALL_RELEVANT)
        }
    }

    private suspend fun executeChatCompletion(requestDto: ChatCompletionRequestDto): String {
        if (config.apiKey.isBlank()) {
            throw HttpException(401, ERROR_BLANK_API_KEY)
        }

        val requestBodyString = json.encodeToString(ChatCompletionRequestDto.serializer(), requestDto)
        val requestUrl = "${config.baseUrl.trimEnd('/')}$ENDPOINT_CHAT_COMPLETIONS"

        val request = Request.Builder()
            .url(requestUrl)
            .addHeader(HEADER_AUTHORIZATION, "$BEARER_PREFIX${config.apiKey.trim()}")
            .addHeader(HEADER_CONTENT_TYPE, MEDIA_TYPE_JSON)
            .post(requestBodyString.toRequestBody(JSON_MEDIA_TYPE))
            .build()

        return withContext(Dispatchers.IO) {
            val response = okHttpClient.newCall(request).execute()
            response.use { resp ->
                if (!resp.isSuccessful) {
                    val errorBody = resp.body?.string().orEmpty()
                    val errorMessage = if (errorBody.isNotBlank()) errorBody else resp.message
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
}
