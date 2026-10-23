package com.martorell.albert.basketcoach.data.source.remote

import com.martorell.albert.basketcoach.data.network.config.AiClientConfig
import com.martorell.albert.basketcoach.data.network.dto.AiAnalysisResponseDto
import com.martorell.albert.basketcoach.data.network.dto.ChatCompletionRequestDto
import com.martorell.albert.basketcoach.data.network.dto.ChatCompletionResponseDto
import com.martorell.albert.basketcoach.data.network.dto.ChatMessageDto
import com.martorell.albert.basketcoach.data.network.prompt.PromptTemplates
import com.martorell.albert.basketcoach.data.util.HttpException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Production implementation of [AiRemoteDataSource] communicating with OpenAI-compatible
 * chat completion endpoints (`POST {baseUrl}/chat/completions`) using OkHttp.
 */
class AiRemoteDataSourceImpl(
    private val config: AiClientConfig,
    private val okHttpClient: OkHttpClient = defaultOkHttpClient(),
    private val json: Json = defaultJson()
) : AiRemoteDataSource {

    companion object {
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

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
            ChatMessageDto(role = "system", content = PromptTemplates.ANALYSIS_SYSTEM_PROMPT),
            ChatMessageDto(role = "user", content = userPrompt)
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

        val systemPrompt = if (!gameContext.isNullOrBlank()) {
            "${PromptTemplates.CHAT_SYSTEM_PROMPT}\n\nGROUNDING CONTEXT (PHASE 1 MATCH DATA):\n$gameContext"
        } else {
            PromptTemplates.CHAT_SYSTEM_PROMPT
        }
        messages.add(ChatMessageDto(role = "system", content = systemPrompt))

        messages.addAll(conversationHistory)
        messages.add(ChatMessageDto(role = "user", content = newPrompt))

        val requestDto = ChatCompletionRequestDto(
            model = config.model,
            messages = messages,
            temperature = 0.7
        )

        return executeChatCompletion(requestDto)
    }

    private suspend fun executeChatCompletion(requestDto: ChatCompletionRequestDto): String {
        if (config.apiKey.isBlank()) {
            throw HttpException(401, "API key is blank")
        }

        val requestBodyString = json.encodeToString(ChatCompletionRequestDto.serializer(), requestDto)
        val requestUrl = "${config.baseUrl.trimEnd('/')}/chat/completions"

        val request = Request.Builder()
            .url(requestUrl)
            .addHeader("Authorization", "Bearer ${config.apiKey.trim()}")
            .addHeader("Content-Type", "application/json")
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

                val bodyString = resp.body?.string() ?: throw IOException("Empty response body from AI endpoint")
                val responseDto = json.decodeFromString<ChatCompletionResponseDto>(bodyString)
                val firstChoice = responseDto.choices.firstOrNull()
                    ?: throw IOException("No choices returned from AI completion endpoint")

                firstChoice.message.content.trim()
            }
        }
    }

    private fun stripMarkdownCodeFences(raw: String): String {
        val trimmed = raw.trim()
        if (trimmed.startsWith("```")) {
            val lines = trimmed.lines()
            val withoutFirst = if (lines.isNotEmpty() && lines.first().startsWith("```")) {
                lines.drop(1)
            } else {
                lines
            }
            val withoutLast = if (withoutFirst.isNotEmpty() && withoutFirst.last().startsWith("```")) {
                withoutFirst.dropLast(1)
            } else {
                withoutFirst
            }
            return withoutLast.joinToString("\n").trim()
        }
        return trimmed
    }
}
