package com.martorell.albert.basketcoach.data.mapper

import com.martorell.albert.basketcoach.data.network.dto.ChatRole
import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.MessageSender
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatMapperTest {

    @Test
    fun toCoachChatMessage_createsValidCoachDomainEntity() {
        val message = toCoachChatMessage("Com defensem el blocatge directe?")

        assertEquals(MessageSender.COACH, message.sender)
        assertEquals("Com defensem el blocatge directe?", message.text)
        assertTrue(message.id.isNotBlank())
        assertTrue(message.timestamp > 0L)
    }

    @Test
    fun toAiChatMessage_createsValidAiDomainEntity() {
        val message = toAiChatMessage("Recomano passar per darrere o fer show curt.")

        assertEquals(MessageSender.AI, message.sender)
        assertEquals("Recomano passar per darrere o fer show curt.", message.text)
        assertTrue(message.id.isNotBlank())
        assertTrue(message.timestamp > 0L)
    }

    @Test
    fun toDto_mapsSenderRolesCorrectly() {
        val coachMsg = ChatMessage("1", MessageSender.COACH, "Hola", 1000L)
        val aiMsg = ChatMessage("2", MessageSender.AI, "Hola Coach", 2000L)

        val coachDto = coachMsg.toDto()
        val aiDto = aiMsg.toDto()

        assertEquals(ChatRole.USER, coachDto.role)
        assertEquals("Hola", coachDto.content)
        assertEquals(ChatRole.ASSISTANT, aiDto.role)
        assertEquals("Hola Coach", aiDto.content)
    }
}
