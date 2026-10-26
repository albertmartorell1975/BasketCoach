package com.martorell.albert.basketcoach.data.mapper

import com.martorell.albert.basketcoach.data.network.dto.ChatRole
import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.MessageSender
import org.junit.Assert.assertEquals
import org.junit.Test

class ChatMapperTest {

    @Test
    fun toChatRole_mapsCoachToUserRole() {
        assertEquals(ChatRole.USER, MessageSender.COACH.toChatRole())
    }

    @Test
    fun toChatRole_mapsAiToAssistantRole() {
        assertEquals(ChatRole.ASSISTANT, MessageSender.AI.toChatRole())
    }

    @Test
    fun toDto_mapsChatMessageToDto() {
        val message = ChatMessage(
            id = "msg-1",
            sender = MessageSender.COACH,
            text = "Hello tactical assistant",
            timestamp = 1000L
        )

        val dto = message.toDto()

        assertEquals(ChatRole.USER, dto.role)
        assertEquals("Hello tactical assistant", dto.content)
    }
}
