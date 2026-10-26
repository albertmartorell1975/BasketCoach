package com.martorell.albert.basketcoach.data.mapper

import com.martorell.albert.basketcoach.data.network.dto.ChatMessageDto
import com.martorell.albert.basketcoach.data.network.dto.ChatRole
import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.MessageSender

/**
 * Maps a domain [MessageSender] into the corresponding OpenAI-compatible role string constant.
 *
 * @receiver The domain [MessageSender] (`COACH` or `AI`).
 * @return [ChatRole.USER] for [MessageSender.COACH], or [ChatRole.ASSISTANT] for [MessageSender.AI].
 */
fun MessageSender.toChatRole(): String = when (this) {
    MessageSender.COACH -> ChatRole.USER
    MessageSender.AI -> ChatRole.ASSISTANT
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
