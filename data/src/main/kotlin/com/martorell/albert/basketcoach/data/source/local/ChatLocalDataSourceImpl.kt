package com.martorell.albert.basketcoach.data.source.local

import com.martorell.albert.basketcoach.domain.model.ChatMessage
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

/**
 * Thread-safe in-memory implementation of [ChatLocalDataSource] protected by a [Mutex].
 *
 * Partitions conversation messages into isolated threads using a [messagesByTopic] map keyed by topic ID,
 * ensuring tactical discussions for different improvement areas remain separate.
 */
class ChatLocalDataSourceImpl @Inject constructor() : ChatLocalDataSource {

    private val mutex = Mutex()
    private val messagesByTopic = mutableMapOf<String, MutableList<ChatMessage>>()

    override suspend fun getMessages(topicId: String): List<ChatMessage> {
        return mutex.withLock {
            messagesByTopic[topicId]?.toList() ?: emptyList()
        }
    }

    override suspend fun saveMessage(message: ChatMessage, topicId: String) {
        mutex.withLock {
            messagesByTopic.getOrPut(topicId) { mutableListOf() }.add(message)
        }
    }

    override suspend fun clearMessages(topicId: String) {
        mutex.withLock {
            messagesByTopic.remove(topicId)
        }
    }

    override suspend fun clearAllMessages() {
        mutex.withLock {
            messagesByTopic.clear()
        }
    }
}
