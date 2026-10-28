package com.martorell.albert.basketcoach.data.source.remote

import com.martorell.albert.basketcoach.data.network.config.AiClientConfig
import com.martorell.albert.basketcoach.data.network.dto.AiAnalysisResponseDto
import com.martorell.albert.basketcoach.data.network.dto.ChatCompletionRequestDto
import com.martorell.albert.basketcoach.data.network.dto.ChatCompletionResponseDto
import com.martorell.albert.basketcoach.data.network.dto.ChatMessageDto
import com.martorell.albert.basketcoach.data.network.dto.ChatRole
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

class AiRemoteDataSourceImpl(
    private val config: AiClientConfig,
    private val okHttpClient: OkHttpClient = defaultOkHttpClient(),
    private val json: Json = defaultJson()
) : AiRemoteDataSource {

    companion object {
        private const val ENDPOINT_CHAT_COMPLETIONS = "/chat/completions"
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val HEADER_CONTENT_TYPE = "Content-Type"
        private const val BEARER_PREFIX = "Bearer "
        private const val MEDIA_TYPE_JSON = "application/json; charset=utf-8"
        private const val CODE_FENCE = "```"
        private const val ERROR_BLANK_API_KEY = "API key is blank"
        private const val ERROR_EMPTY_RESPONSE = "Empty response body from AI endpoint"
        private const val ERROR_NO_CHOICES = "No choices returned from AI completion endpoint"

        private val JSON_MEDIA_TYPE = MEDIA_TYPE_JSON.toMediaType()

        fun defaultOkHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
        }

        fun defaultJson(): Json {
            return Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            }
        }
    }

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

    override suspend fun generateChatResponse(
        conversationHistory: List<ChatMessageDto>,
        newPrompt: String,
        gameContext: String?
    ): String {
        val messages = mutableListOf<ChatMessageDto>()

        val systemPrompt = PromptTemplates.buildChatSystemPrompt(gameContext)
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
