package com.martorell.albert.basketcoach.data.network.client

import com.martorell.albert.basketcoach.data.config.DevExpertConfig
import com.martorell.albert.basketcoach.data.network.dto.AiAnalysisResponseDto
import com.martorell.albert.basketcoach.data.network.dto.ChatCompletionRequestDto
import com.martorell.albert.basketcoach.data.network.dto.ChatCompletionResponseDto
import com.martorell.albert.basketcoach.data.network.dto.ChatMessageDto
import com.martorell.albert.basketcoach.data.network.prompt.PromptTemplates
import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.MessageSender
import com.martorell.albert.basketcoach.domain.model.Result
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
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

/**
 * OkHttp-based implementation of [DevExpertApiClient] connecting to OpenAI-compatible
 * chat completion endpoints (`POST {baseUrl}/chat/completions`).
 *
 * Handles Markdown code fence stripping, JSON deserialization, and maps all HTTP and network
 * exceptions into typed [DomainError] without swallowing [CancellationException].
 */
class DevExpertApiClientImpl(
    private val config: DevExpertConfig,
    private val okHttpClient: OkHttpClient = defaultOkHttpClient(),
    private val json: Json = defaultJson()
) : DevExpertApiClient {

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
                coerceInputValues = true
            }
        }
    }

    override suspend fun generateAnalysis(gameContext: String): Result<AiAnalysisResponseDto, DomainError> {
        if (config.apiKey.isBlank()) {
            return Result.Error(DomainError.Network.Unauthorized)
        }

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

        return executeChatCompletion(requestDto).mapCatching { content ->
            val sanitized = stripMarkdownCodeFences(content)
            json.decodeFromString<AiAnalysisResponseDto>(sanitized)
        }
    }

    override suspend fun generateChatResponse(
        conversationHistory: List<ChatMessage>,
        newPrompt: String,
        gameContext: String?
    ): Result<String, DomainError> {
        if (config.apiKey.isBlank()) {
            return Result.Error(DomainError.Network.Unauthorized)
        }

        val messages = mutableListOf<ChatMessageDto>()

        val systemPrompt = if (!gameContext.isNullOrBlank()) {
            "${PromptTemplates.CHAT_SYSTEM_PROMPT}\n\nPhase 1 Match Context:\n$gameContext"
        } else {
            PromptTemplates.CHAT_SYSTEM_PROMPT
        }
        messages.add(ChatMessageDto(role = "system", content = systemPrompt))

        for (msg in conversationHistory) {
            val role = when (msg.sender) {
                MessageSender.COACH -> "user"
                MessageSender.AI -> "assistant"
            }
            messages.add(ChatMessageDto(role = role, content = msg.text))
        }

        messages.add(ChatMessageDto(role = "user", content = newPrompt))

        val requestDto = ChatCompletionRequestDto(
            model = config.model,
            messages = messages,
            temperature = 0.7
        )

        return executeChatCompletion(requestDto)
    }

    private suspend fun executeChatCompletion(
        requestDto: ChatCompletionRequestDto
    ): Result<String, DomainError> = safeCall {
        val requestBodyString = json.encodeToString(ChatCompletionRequestDto.serializer(), requestDto)
        val requestBody = requestBodyString.toRequestBody(JSON_MEDIA_TYPE)

        val fullUrl = buildChatCompletionsUrl(config.baseUrl)
        val request = Request.Builder()
            .url(fullUrl)
            .addHeader("Authorization", "Bearer ${config.apiKey}")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            val responseBody = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                return@safeCall when (response.code) {
                    401, 403 -> Result.Error(DomainError.Network.Unauthorized)
                    in 500..599 -> Result.Error(DomainError.Network.ServerError(response.code, responseBody))
                    else -> Result.Error(DomainError.Network.ServerError(response.code, responseBody))
                }
            }

            try {
                val completion = json.decodeFromString<ChatCompletionResponseDto>(responseBody)
                val content = completion.choices.firstOrNull()?.message?.content
                if (content.isNullOrBlank()) {
                    Result.Error(DomainError.Generic("Empty completion choices returned by model"))
                } else {
                    Result.Success(content)
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Result.Error(DomainError.Generic("Failed to parse completion response: ${e.message}", e))
            }
        }
    }

    private fun buildChatCompletionsUrl(baseUrl: String): String {
        val cleanBase = baseUrl.trimEnd('/')
        return if (cleanBase.endsWith("/chat/completions")) {
            cleanBase
        } else {
            "$cleanBase/chat/completions"
        }
    }

    private fun stripMarkdownCodeFences(content: String): String {
        val trimmed = content.trim()
        if (trimmed.startsWith("```")) {
            val lines = trimmed.lines()
            val startIndex = 1
            val endIndex = if (lines.last().trim() == "```") lines.lastIndex else lines.size
            if (startIndex < endIndex) {
                return lines.subList(startIndex, endIndex).joinToString("\n").trim()
            }
        }
        return trimmed
    }

    private suspend fun <T> safeCall(
        block: () -> Result<T, DomainError>
    ): Result<T, DomainError> = withContext(Dispatchers.IO) {
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: SocketTimeoutException) {
            Result.Error(DomainError.Network.Timeout)
        } catch (e: UnknownHostException) {
            Result.Error(DomainError.Network.NoInternet)
        } catch (e: ConnectException) {
            Result.Error(DomainError.Network.NoInternet)
        } catch (e: IOException) {
            Result.Error(DomainError.Network.NoInternet)
        } catch (e: Exception) {
            Result.Error(DomainError.Generic(e.message, e))
        }
    }

    private inline fun <T, R> Result<T, DomainError>.mapCatching(
        transform: (T) -> R
    ): Result<R, DomainError> {
        return when (this) {
            is Result.Success -> {
                try {
                    Result.Success(transform(data))
                } catch (e: CancellationException) {
                    throw e
                } catch (e: SerializationException) {
                    Result.Error(DomainError.Generic("Failed to parse AI response: ${e.message}", e))
                } catch (e: Exception) {
                    Result.Error(DomainError.Generic("Error processing AI response: ${e.message}", e))
                }
            }
            is Result.Error -> this
        }
    }
}
