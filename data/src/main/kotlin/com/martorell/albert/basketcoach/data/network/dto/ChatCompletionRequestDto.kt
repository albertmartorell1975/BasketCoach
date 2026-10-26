package com.martorell.albert.basketcoach.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * OpenAI-compatible chat completion request payload.
 * All fields are strictly mandatory.
 *
 * @property model Model identifier.
 * @property messages List of messages comprising the conversation thread.
 * @property temperature Sampling temperature (0.0 to 2.0).
 */
@Serializable
data class ChatCompletionRequestDto(
    @SerialName("model")
    val model: String,
    @SerialName("messages")
    val messages: List<ChatMessageDto>,
    @SerialName("temperature")
    val temperature: Double
)

/**
 * Standard OpenAI chat roles used across completion payloads.
 */
object ChatRole {
    const val SYSTEM = "system"
    const val USER = "user"
    const val ASSISTANT = "assistant"
}

/**
 * DTO representing an individual message in a chat completion conversation.
 * All fields are strictly mandatory.
 *
 * @property role Role of the author ("system", "user", "assistant").
 * @property content Content of the message.
 */
@Serializable
data class ChatMessageDto(
    @SerialName("role")
    val role: String,
    @SerialName("content")
    val content: String
)
