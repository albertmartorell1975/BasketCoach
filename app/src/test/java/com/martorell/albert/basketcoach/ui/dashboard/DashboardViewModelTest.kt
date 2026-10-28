package com.martorell.albert.basketcoach.ui.dashboard

import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.repository.GameRepository
import com.martorell.albert.basketcoach.ui.dashboard.mapper.DashboardUiMapper
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardUiContent
import com.martorell.albert.basketcoach.usecases.game.GetPhase1GamesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val mapper = DashboardUiMapper()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun should_RemainPassiveInitially_and_TransitionToSuccess_when_LoadDashboardInvoked() = runTest {
        val fakeRepo = object : GameRepository {
            override suspend fun getPhase1Games(): Result<List<Game>, DomainError> =
                Result.Success(emptyList())
            override suspend fun getGameByRound(round: Int): Result<Game, DomainError> =
                Result.Error(DomainError.Local.NotFound)
        }
        val useCase = GetPhase1GamesUseCase(fakeRepo)
        val viewModel = DashboardViewModel(useCase, mapper)

        // 1. Passive initialization check: zero active work triggered
        assertTrue(viewModel.uiState.value.content is DashboardUiContent.Loading)
        assertFalse(viewModel.uiState.value.isRefreshing)

        // 2. Explicit load execution
        viewModel.loadDashboard()
        testDispatcher.scheduler.advanceUntilIdle()

        // 3. Success state transition
        assertTrue(viewModel.uiState.value.content is DashboardUiContent.Success)
        assertFalse(viewModel.uiState.value.isRefreshing)
    }

    @Test
    fun should_TransitionToError_when_UseCaseReturnsError_and_RecoverOnRetry() = runTest {
        var shouldFail = true
        val fakeRepo = object : GameRepository {
            override suspend fun getPhase1Games(): Result<List<Game>, DomainError> {
                return if (shouldFail) {
                    Result.Error(DomainError.Local.NotFound)
                } else {
                    Result.Success(emptyList())
                }
            }
            override suspend fun getGameByRound(round: Int): Result<Game, DomainError> =
                Result.Error(DomainError.Local.NotFound)
        }
        val useCase = GetPhase1GamesUseCase(fakeRepo)
        val viewModel = DashboardViewModel(useCase, mapper)

        // Initial error load
        viewModel.loadDashboard()
        testDispatcher.scheduler.advanceUntilIdle()

        val errorContent = viewModel.uiState.value.content
        assertTrue(errorContent is DashboardUiContent.Error)
        assertEquals("No s'han trobat les dades dels partits de la 1a Fase.", (errorContent as DashboardUiContent.Error).message)

        // Retry recovery
        shouldFail = false
        viewModel.loadDashboard()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.content is DashboardUiContent.Success)
    }
}
