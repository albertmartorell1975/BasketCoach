package com.martorell.albert.basketcoach.ui.dashboard

import com.martorell.albert.basketcoach.data.repository.GameRepositoryImpl
import com.martorell.albert.basketcoach.data.source.local.GameLocalDataSourceImpl
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.repository.GameRepository
import com.martorell.albert.basketcoach.ui.dashboard.mapper.DashboardUiMapper
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardUiContent
import com.martorell.albert.basketcoach.usecases.game.GetPhase1GamesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
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
    private val realGames: List<Game> = runBlocking {
        (GameRepositoryImpl(GameLocalDataSourceImpl()).getPhase1Games() as Result.Success).data
    }

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
        var callCount = 0
        val fakeRepo = object : GameRepository {
            override suspend fun getPhase1Games(): Result<List<Game>, DomainError> {
                callCount++
                return Result.Success(realGames)
            }
            override suspend fun getGameByRound(round: Int): Result<Game, DomainError> =
                Result.Error(DomainError.Local.NotFound)
        }
        val useCase = GetPhase1GamesUseCase(fakeRepo)
        val viewModel = DashboardViewModel(useCase, mapper)

        // 1. Passive initialization check: zero active work triggered
        assertTrue(viewModel.uiState.value.content is DashboardUiContent.Loading)
        assertFalse(viewModel.uiState.value.isRefreshing)
        assertEquals(0, callCount)

        // 2. Explicit load execution + in-flight concurrency guard check
        viewModel.loadDashboard()
        assertTrue(viewModel.uiState.value.isRefreshing)
        viewModel.loadDashboard(forceRefresh = true)
        testDispatcher.scheduler.advanceUntilIdle()

        // 3. Success state transition (only 1 repository call executed)
        assertEquals(1, callCount)
        assertTrue(viewModel.uiState.value.content is DashboardUiContent.Success)
        assertFalse(viewModel.uiState.value.isRefreshing)

        // 4. Re-entry guard: redundant loadDashboard() on Success is ignored
        viewModel.loadDashboard()
        assertFalse(viewModel.uiState.value.isRefreshing)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, callCount)

        // 5. Explicit forceRefresh = true: preserves Success while refreshing, then increments callCount
        viewModel.loadDashboard(forceRefresh = true)
        assertTrue(viewModel.uiState.value.content is DashboardUiContent.Success)
        assertTrue(viewModel.uiState.value.isRefreshing)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, callCount)
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
                    Result.Success(realGames)
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
        assertEquals(DomainError.Local.NotFound, (errorContent as DashboardUiContent.Error).error)
        assertFalse(viewModel.uiState.value.isRefreshing)

        // Retry recovery: immediately transitions to Loading + isRefreshing before completion
        shouldFail = false
        viewModel.loadDashboard()
        assertTrue(viewModel.uiState.value.content is DashboardUiContent.Loading)
        assertTrue(viewModel.uiState.value.isRefreshing)

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.content is DashboardUiContent.Success)
        assertFalse(viewModel.uiState.value.isRefreshing)
    }
}
