package com.martorell.albert.basketcoach.usecases.ai

import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Drill
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.usecases.fakes.FakeAiAnalysisRepository
import com.martorell.albert.basketcoach.usecases.fakes.FakeGameRepository
import com.martorell.albert.basketcoach.usecases.fakes.TestGameFactory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetPracticeDrillsUseCaseTest {

    private lateinit var fakeGameRepository: FakeGameRepository
    private lateinit var fakeAiAnalysisRepository: FakeAiAnalysisRepository
    private lateinit var useCase: GetPracticeDrillsUseCase

    @Before
    fun setUp() {
        fakeGameRepository = FakeGameRepository()
        fakeAiAnalysisRepository = FakeAiAnalysisRepository()
        useCase = GetPracticeDrillsUseCase(fakeGameRepository, fakeAiAnalysisRepository)
    }

    @Test
    fun invoke_success_retrievesDrillsWithGames() = runTest {
        val games = listOf(TestGameFactory.createGame(round = 1))
        val expectedDrills = listOf(
            Drill(
                id = "drill-1",
                name = "Box-Out & Outlet Progression",
                objective = "Improve defensive rebounding conversion",
                instructions = listOf("Step 1: Contact", "Step 2: Secure", "Step 3: Outlet"),
                focusArea = "Rebounding"
            )
        )
        fakeGameRepository.gamesToReturn = Result.Success(games)
        fakeAiAnalysisRepository.drillsToReturn = Result.Success(expectedDrills)

        val result = useCase()

        assertTrue(result is Result.Success)
        assertEquals(expectedDrills, (result as Result.Success).data)
        assertEquals(1, fakeGameRepository.getPhase1GamesCallCount)
        assertEquals(1, fakeAiAnalysisRepository.getDrillsCallCount)
        assertEquals(games, fakeAiAnalysisRepository.lastGamesForDrills)
    }

    @Test
    fun invoke_whenGameLoadingFails_propagatesErrorWithoutCallingAiRepository() = runTest {
        fakeGameRepository.gamesToReturn = Result.Error(DomainError.Local.NotFound)

        val result = useCase()

        assertTrue(result is Result.Error)
        assertEquals(DomainError.Local.NotFound, (result as Result.Error).error)
        assertEquals(1, fakeGameRepository.getPhase1GamesCallCount)
        assertEquals(0, fakeAiAnalysisRepository.getDrillsCallCount)
    }

    @Test
    fun invoke_whenAiRepositoryFails_propagatesAiError() = runTest {
        val games = listOf(TestGameFactory.createGame(round = 1))
        fakeGameRepository.gamesToReturn = Result.Success(games)
        fakeAiAnalysisRepository.drillsToReturn = Result.Error(DomainError.Network.ServerError(500, "Internal Server Error"))

        val result = useCase()

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).error is DomainError.Network.ServerError)
        assertEquals(1, fakeGameRepository.getPhase1GamesCallCount)
        assertEquals(1, fakeAiAnalysisRepository.getDrillsCallCount)
    }
}
