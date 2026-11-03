package com.martorell.albert.basketcoach.data.mapper

import com.martorell.albert.basketcoach.data.network.dto.ChatMessageDto
import com.martorell.albert.basketcoach.data.network.dto.ChatRole
import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.MessageSender
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatMapperTest {

    @Test
    fun toDomain_mapsUserRoleToCoachDomainEntity() {
        val dto = ChatMessageDto(role = ChatRole.USER, content = "Com defensem el blocatge directe?")
        val message = dto.toDomain()

        assertEquals(MessageSender.COACH, message.sender)
        assertEquals("Com defensem el blocatge directe?", message.text)
        assertTrue(message.id.isNotBlank())
        assertTrue(message.timestamp > 0L)
    }

    @Test
    fun toDomain_mapsAssistantRoleToAiDomainEntity() {
        val dto = ChatMessageDto(role = ChatRole.ASSISTANT, content = "Recomano passar per darrere o fer show curt.")
        val message = dto.toDomain()

        assertEquals(MessageSender.AI, message.sender)
        assertEquals("Recomano passar per darrere o fer show curt.", message.text)
        assertTrue(message.id.isNotBlank())
        assertTrue(message.timestamp > 0L)
    }

    @Test
    fun toDomain_throwsIllegalArgumentExceptionWhenRoleIsSystem() {
        val dto = ChatMessageDto(role = ChatRole.SYSTEM, content = "You are a basketball assistant.")
        assertThrows(IllegalArgumentException::class.java) {
            dto.toDomain()
        }
    }

    @Test
    fun toDto_mapsDomainMessagesToDtosCorrectly() {
        val coachMsg = ChatMessage("1", MessageSender.COACH, "Hola", 1000L)
        val aiMsg = ChatMessage("2", MessageSender.AI, "Hola Coach", 2000L)

        val coachDto = coachMsg.toDto()
        val aiDto = aiMsg.toDto()

        assertEquals(ChatRole.USER, coachDto.role)
        assertEquals("Hola", coachDto.content)
        assertEquals(ChatRole.ASSISTANT, aiDto.role)
        assertEquals("Hola Coach", aiDto.content)
    }

    @Test
    fun toChatRole_mapsMessageSenderCorrectly() {
        assertEquals(ChatRole.USER, MessageSender.COACH.toChatRole())
        assertEquals(ChatRole.ASSISTANT, MessageSender.AI.toChatRole())
    }

    @Test
    fun toMessageSender_mapsChatRoleCorrectly() {
        assertEquals(MessageSender.COACH, ChatRole.USER.toMessageSender())
        assertEquals(MessageSender.AI, ChatRole.ASSISTANT.toMessageSender())
    }

    @Test
    fun toMessageSender_throwsIllegalArgumentExceptionForSystemRole() {
        assertThrows(IllegalArgumentException::class.java) {
            ChatRole.SYSTEM.toMessageSender()
        }
    }

    @Test
    fun chatMessageDto_serializationAndDeserialization_handlesAllRoles() {
        val json = Json { ignoreUnknownKeys = true }

        val userDto = ChatMessageDto(role = ChatRole.USER, content = "Hola")
        val userJson = json.encodeToString(ChatMessageDto.serializer(), userDto)
        assertTrue(userJson.contains("\"role\":\"user\""))
        val decodedUser = json.decodeFromString(ChatMessageDto.serializer(), userJson)
        assertEquals(ChatRole.USER, decodedUser.role)
        assertEquals("Hola", decodedUser.content)

        val assistantDto = ChatMessageDto(role = ChatRole.ASSISTANT, content = "Resposta")
        val assistantJson = json.encodeToString(ChatMessageDto.serializer(), assistantDto)
        assertTrue(assistantJson.contains("\"role\":\"assistant\""))
        val decodedAssistant = json.decodeFromString(ChatMessageDto.serializer(), assistantJson)
        assertEquals(ChatRole.ASSISTANT, decodedAssistant.role)

        val systemDto = ChatMessageDto(role = ChatRole.SYSTEM, content = "System")
        val systemJson = json.encodeToString(ChatMessageDto.serializer(), systemDto)
        assertTrue(systemJson.contains("\"role\":\"system\""))
        val decodedSystem = json.decodeFromString(ChatMessageDto.serializer(), systemJson)
        assertEquals(ChatRole.SYSTEM, decodedSystem.role)
    }
}
