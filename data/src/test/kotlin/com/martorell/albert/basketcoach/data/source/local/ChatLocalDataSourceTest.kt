package com.martorell.albert.basketcoach.data.source.local

import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.MessageSender
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatLocalDataSourceTest {

    @Test
    fun `returns empty list when no messages saved`() = runTest {
        val dataSource = ChatLocalDataSourceImpl()
        assertTrue(dataSource.getMessages().isEmpty())
    }

    @Test
    fun `saveMessage appends messages in chronological order`() = runTest {
        val dataSource = ChatLocalDataSourceImpl()
        val msg1 = ChatMessage("1", MessageSender.COACH, "Hi", 1000L)
        val msg2 = ChatMessage("2", MessageSender.AI, "Hello Coach", 2000L)

        dataSource.saveMessage(msg1)
        dataSource.saveMessage(msg2)

        val messages = dataSource.getMessages()
        assertEquals(2, messages.size)
        assertEquals(msg1, messages[0])
        assertEquals(msg2, messages[1])
    }

    @Test
    fun `clearMessages empties the message list`() = runTest {
        val dataSource = ChatLocalDataSourceImpl()
        dataSource.saveMessage(ChatMessage("1", MessageSender.COACH, "Hi", 1000L))

        dataSource.clearMessages()

        assertTrue(dataSource.getMessages().isEmpty())
    }
}
