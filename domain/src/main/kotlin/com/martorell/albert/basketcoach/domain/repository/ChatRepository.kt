package com.martorell.albert.basketcoach.domain.repository

import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Result

interface ChatRepository {
    suspend fun getMessages(): Result<List<ChatMessage>, DomainError>
    suspend fun sendMessage(messageText: String): Result<ChatMessage, DomainError>
}
