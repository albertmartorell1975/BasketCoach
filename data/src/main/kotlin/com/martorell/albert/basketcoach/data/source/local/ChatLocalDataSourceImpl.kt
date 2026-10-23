package com.martorell.albert.basketcoach.data.source.local

import com.martorell.albert.basketcoach.domain.model.ChatMessage
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Thread-safe in-memory implementation of [ChatLocalDataSource] protected by a [Mutex].
 */
class ChatLocalDataSourceImpl : ChatLocalDataSource {

    private val mutex = Mutex()
    private val messages = mutableListOf<ChatMessage>()

    override suspend fun getMessages(): List<ChatMessage> {
        return mutex.withLock { messages.toList() }
    }

    override suspend fun saveMessage(message: ChatMessage) {
        mutex.withLock { messages.add(message) }
    }

    override suspend fun clearMessages() {
        mutex.withLock { messages.clear() }
    }
}
