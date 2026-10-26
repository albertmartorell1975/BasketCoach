package com.martorell.albert.basketcoach.usecases.chat

import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.MessageSender
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.usecases.fakes.FakeChatRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetChatMessagesUseCaseTest {

    private lateinit var fakeChatRepository: FakeChatRepository
    private lateinit var useCase: GetChatMessagesUseCase

    @Before
    fun setUp() {
        fakeChatRepository = FakeChatRepository()
        useCase = GetChatMessagesUseCase(fakeChatRepository)
    }

    @Test
    fun invoke_returnsMessagesFromRepository() = runTest {
        val messages = listOf(
            ChatMessage("msg-1", MessageSender.COACH, "How can we improve 2-3 zone?", 1000L),
            ChatMessage("msg-2", MessageSender.AI, "Focus on guard communication.", 2000L)
        )
        fakeChatRepository.messagesToReturn = Result.Success(messages)

        val result = useCase()

        assertTrue(result is Result.Success)
        assertEquals(messages, (result as Result.Success).data)
        assertEquals(1, fakeChatRepository.getMessagesCallCount)
    }

    @Test
    fun invoke_whenRepositoryFails_propagatesError() = runTest {
        fakeChatRepository.messagesToReturn = Result.Error(DomainError.Generic("Storage failure"))

        val result = useCase()

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).error is DomainError.Generic)
    }
}
