package com.martorell.albert.basketcoach.data.mapper

import com.martorell.albert.basketcoach.data.network.dto.ChatMessageDto
import com.martorell.albert.basketcoach.data.network.dto.ChatRole
import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.MessageSender
import java.util.UUID

/**
 * Maps a domain [MessageSender] into the corresponding OpenAI-compatible [ChatRole] enum.
 *
 * @receiver The domain [MessageSender] (`COACH`, `AI`, or `SYSTEM`).
 * @return The corresponding [ChatRole] enum value.
 */
fun MessageSender.toChatRole(): ChatRole = when (this) {
    MessageSender.COACH -> ChatRole.USER
    MessageSender.AI -> ChatRole.ASSISTANT
    MessageSender.SYSTEM -> ChatRole.SYSTEM
}

/**
 * Maps a network [ChatRole] into its corresponding domain [MessageSender].
 *
 * @receiver The [ChatRole] enum value.
 * @return The corresponding [MessageSender] enum value.
 */
fun ChatRole.toMessageSender(): MessageSender = when (this) {
    ChatRole.USER -> MessageSender.COACH
    ChatRole.ASSISTANT -> MessageSender.AI
    ChatRole.SYSTEM -> MessageSender.SYSTEM
}

/**
 * Converts a domain [ChatMessage] entity into a network [ChatMessageDto] suitable for OpenAI-compatible chat completion requests.
 *
 * @receiver The domain [ChatMessage] to transform.
 * @return A [ChatMessageDto] containing the mapped role and message content text.
 */
fun ChatMessage.toDto(): ChatMessageDto = ChatMessageDto(
    role = sender.toChatRole(),
    content = text
)

/**
 * Maps a [ChatMessageDto] into its domain entity [ChatMessage] with an auto-generated unique identifier and timestamp.
 *
 * Adheres to standard Clean Architecture DTO-to-Domain mapping conventions.
 *
 * @receiver The network/data transfer object [ChatMessageDto].
 * @return A fully populated domain [ChatMessage] instance.
 */
fun ChatMessageDto.toDomain(): ChatMessage = ChatMessage(
    id = UUID.randomUUID().toString(),
    sender = role.toMessageSender(),
    text = content,
    timestamp = System.currentTimeMillis()
)
