package com.martorell.albert.basketcoach.usecases.chat

import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.usecases.fakes.FakeChatRepository
import com.martorell.albert.basketcoach.usecases.fakes.FakeGameRepository
import com.martorell.albert.basketcoach.usecases.fakes.TestGameFactory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SendChatMessageUseCaseTest {

    private lateinit var fakeGameRepository: FakeGameRepository
    private lateinit var fakeChatRepository: FakeChatRepository
    private lateinit var useCase: SendChatMessageUseCase

    @Before
    fun setUp() {
        fakeGameRepository = FakeGameRepository()
        fakeChatRepository = FakeChatRepository()
        useCase = SendChatMessageUseCase(fakeGameRepository, fakeChatRepository)
    }

    @Test
    fun invoke_validMessage_enrichesWithGamesAndReturnsResponse() = runTest {
        val games = listOf(TestGameFactory.createGame(round = 1))
        fakeGameRepository.gamesToReturn = Result.Success(games)

        val result = useCase("How can we improve defensive rebounding in Phase 2?")

        assertTrue(result is Result.Success)
        assertEquals("How can we improve defensive rebounding in Phase 2?", fakeChatRepository.lastMessageText)
        assertEquals(games, fakeChatRepository.lastGames)
        assertEquals(1, fakeGameRepository.getPhase1GamesCallCount)
        assertEquals(1, fakeChatRepository.sendMessageCallCount)
    }

    @Test
    fun invoke_whenGamesFail_gracefullyFallsBackWithNullGames() = runTest {
        fakeGameRepository.gamesToReturn = Result.Error(DomainError.Local.NotFound)

        val result = useCase("What is a good 2-3 zone defense drill?")

        assertTrue(result is Result.Success)
        assertEquals("What is a good 2-3 zone defense drill?", fakeChatRepository.lastMessageText)
        assertNull("Games must be null on game failure fallback", fakeChatRepository.lastGames)
        assertEquals(1, fakeGameRepository.getPhase1GamesCallCount)
        assertEquals(1, fakeChatRepository.sendMessageCallCount)
    }

    @Test
    fun invoke_whenChatRepositoryFails_propagatesError() = runTest {
        fakeGameRepository.gamesToReturn = Result.Success(emptyList())
        fakeChatRepository.sendMessageResult = Result.Error(DomainError.Network.NoInternet)

        val result = useCase("Any suggestions?")

        assertTrue(result is Result.Error)
        assertEquals(DomainError.Network.NoInternet, (result as Result.Error).error)
    }
}
