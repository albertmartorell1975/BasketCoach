package com.martorell.albert.basketcoach.usecases.game

import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.usecases.fakes.FakeGameRepository
import com.martorell.albert.basketcoach.usecases.fakes.TestGameFactory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetPhase1GamesUseCaseTest {

    private lateinit var fakeGameRepository: FakeGameRepository
    private lateinit var useCase: GetPhase1GamesUseCase

    @Before
    fun setUp() {
        fakeGameRepository = FakeGameRepository()
        useCase = GetPhase1GamesUseCase(fakeGameRepository)
    }

    @Test
    fun invoke_returnsSuccessWithAllGames() = runTest {
        val games = (1..14).map { TestGameFactory.createGame(round = it) }
        fakeGameRepository.gamesToReturn = Result.Success(games)

        val result = useCase()

        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(14, data.size)
        assertEquals(1, fakeGameRepository.getPhase1GamesCallCount)
    }

    @Test
    fun invoke_whenRepositoryFails_propagatesError() = runTest {
        fakeGameRepository.gamesToReturn = Result.Error(DomainError.Local.NotFound)

        val result = useCase()

        assertTrue(result is Result.Error)
        assertEquals(DomainError.Local.NotFound, (result as Result.Error).error)
    }
}
