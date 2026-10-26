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

class GetGameByRoundUseCaseTest {

    private lateinit var fakeGameRepository: FakeGameRepository
    private lateinit var useCase: GetGameByRoundUseCase

    @Before
    fun setUp() {
        fakeGameRepository = FakeGameRepository()
        useCase = GetGameByRoundUseCase(fakeGameRepository)
    }

    @Test
    fun invoke_validRound_returnsMatchingGame() = runTest {
        val gameRound3 = TestGameFactory.createGame(round = 3)
        fakeGameRepository.gamesToReturn = Result.Success(listOf(gameRound3))

        val result = useCase(3)

        assertTrue(result is Result.Success)
        assertEquals(3, (result as Result.Success).data.round)
        assertEquals(3, fakeGameRepository.lastRequestedRound)
    }

    @Test
    fun invoke_invalidRound_returnsNotFound() = runTest {
        val gameRound1 = TestGameFactory.createGame(round = 1)
        fakeGameRepository.gamesToReturn = Result.Success(listOf(gameRound1))

        val result = useCase(99)

        assertTrue(result is Result.Error)
        assertEquals(DomainError.Local.NotFound, (result as Result.Error).error)
        assertEquals(99, fakeGameRepository.lastRequestedRound)
    }
}
