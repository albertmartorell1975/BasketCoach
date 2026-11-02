package com.martorell.albert.basketcoach.ui.analysis

import com.martorell.albert.basketcoach.data.repository.GameRepositoryImpl
import com.martorell.albert.basketcoach.data.source.local.GameLocalDataSourceImpl
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Drill
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.InsightCategory
import com.martorell.albert.basketcoach.domain.model.InsightMetric
import com.martorell.albert.basketcoach.domain.model.InsightType
import com.martorell.albert.basketcoach.domain.model.MetricSentiment
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.repository.AiAnalysisRepository
import com.martorell.albert.basketcoach.domain.repository.GameRepository
import com.martorell.albert.basketcoach.ui.analysis.mapper.AnalysisUiMapper
import com.martorell.albert.basketcoach.ui.analysis.model.AnalysisUiContent
import com.martorell.albert.basketcoach.ui.analysis.model.InsightFilterTab
import com.martorell.albert.basketcoach.usecases.ai.GenerateInsightsUseCase
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
class AnalysisViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val mapper = AnalysisUiMapper()
    private val realGames: List<Game> = runBlocking {
        (GameRepositoryImpl(GameLocalDataSourceImpl()).getPhase1Games() as Result.Success).data
    }

    private val sampleInsights = listOf(
        Insight(
            id = "strength_1",
            title = "Eficiència Interior",
            description = "Descripció",
            category = InsightCategory.OFFENSE,
            relatedGameRounds = listOf(1, 3),
            type = InsightType.STRENGTH,
            contextTag = "Pintura",
            metric = InsightMetric(
                primaryLabel = "T2%",
                primaryValue = "58%",
                primarySubValue = "+8%",
                secondaryLabel = "Punts",
                secondaryValue = "34",
                secondaryTag = "tag",
                sentiment = MetricSentiment.POSITIVE
            ),
            priority = "Alta",
            footerNote = "Evidència",
            exploreChatPrompt = ""
        ),
        Insight(
            id = "improvement_1",
            title = "Balanç Defensiu",
            description = "Descripció",
            category = InsightCategory.DEFENSE,
            relatedGameRounds = listOf(2, 4),
            type = InsightType.IMPROVEMENT,
            contextTag = "Transició",
            metric = InsightMetric(
                primaryLabel = "Contraatac",
                primaryValue = "16",
                primarySubValue = "-5",
                secondaryLabel = "Prioritat",
                secondaryValue = "1",
                secondaryTag = "fastbreak",
                sentiment = MetricSentiment.NEGATIVE
            ),
            priority = "Crítica",
            footerNote = "Pilar corrector",
            exploreChatPrompt = "Com podem millorar?"
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun should_RemainPassiveInitially_and_TransitionToSuccessAndFilter_when_LoadAnalysisInvoked() = runTest {
        val fakeGameRepo = object : GameRepository {
            override suspend fun getPhase1Games(): Result<List<Game>, DomainError> =
                Result.Success(realGames)
            override suspend fun getGameByRound(round: Int): Result<Game, DomainError> =
                Result.Error(DomainError.Local.NotFound)
        }
        val fakeAiRepo = object : AiAnalysisRepository {
            override suspend fun getInsights(games: List<Game>): Result<List<Insight>, DomainError> =
                Result.Success(sampleInsights)
            override suspend fun getDrills(games: List<Game>): Result<List<Drill>, DomainError> =
                Result.Success(emptyList())
        }

        val getPhase1GamesUseCase = GetPhase1GamesUseCase(fakeGameRepo)
        val generateInsightsUseCase = GenerateInsightsUseCase(fakeGameRepo, fakeAiRepo)
        val viewModel = AnalysisViewModel(getPhase1GamesUseCase, generateInsightsUseCase, mapper)

        // 1. Passive initialization check: zero active work triggered
        assertTrue(viewModel.uiState.value.content is AnalysisUiContent.Loading)
        assertFalse(viewModel.uiState.value.isRefreshing)

        // 2. Explicit load execution
        viewModel.loadAnalysis()
        testDispatcher.scheduler.advanceUntilIdle()

        // 3. Success state transition
        val successContent = viewModel.uiState.value.content
        assertTrue(successContent is AnalysisUiContent.Success)
        assertFalse(viewModel.uiState.value.isRefreshing)

        val success = successContent as AnalysisUiContent.Success
        assertEquals(2, success.totalCount)
        assertEquals(2, success.displayedInsights.size)

        // 4. In-memory filter switching
        viewModel.onFilterSelected(InsightFilterTab.STRENGTHS)
        val filteredContent = viewModel.uiState.value.content as AnalysisUiContent.Success
        assertEquals(InsightFilterTab.STRENGTHS, filteredContent.selectedFilter)
        assertEquals(1, filteredContent.displayedInsights.size)
        assertEquals(InsightType.STRENGTH, filteredContent.displayedInsights.first().type)
    }

    @Test
    fun should_TransitionToError_when_UseCaseFails_and_RecoverOnRetry() = runTest {
        var shouldFail = true
        val fakeGameRepo = object : GameRepository {
            override suspend fun getPhase1Games(): Result<List<Game>, DomainError> =
                Result.Success(realGames)
            override suspend fun getGameByRound(round: Int): Result<Game, DomainError> =
                Result.Error(DomainError.Local.NotFound)
        }
        val fakeAiRepo = object : AiAnalysisRepository {
            override suspend fun getInsights(games: List<Game>): Result<List<Insight>, DomainError> {
                return if (shouldFail) {
                    Result.Error(DomainError.Network.NoInternet)
                } else {
                    Result.Success(sampleInsights)
                }
            }
            override suspend fun getDrills(games: List<Game>): Result<List<Drill>, DomainError> =
                Result.Success(emptyList())
        }

        val getPhase1GamesUseCase = GetPhase1GamesUseCase(fakeGameRepo)
        val generateInsightsUseCase = GenerateInsightsUseCase(fakeGameRepo, fakeAiRepo)
        val viewModel = AnalysisViewModel(getPhase1GamesUseCase, generateInsightsUseCase, mapper)

        // Initial error load
        viewModel.loadAnalysis()
        testDispatcher.scheduler.advanceUntilIdle()

        val errorContent = viewModel.uiState.value.content
        assertTrue(errorContent is AnalysisUiContent.Error)
        assertEquals(DomainError.Network.NoInternet, (errorContent as AnalysisUiContent.Error).error)

        // Retry recovery
        shouldFail = false
        viewModel.loadAnalysis()
        testDispatcher.scheduler.advanceUntilIdle()

        val recoveredContent = viewModel.uiState.value.content
        assertTrue(recoveredContent is AnalysisUiContent.Success)
        assertFalse(viewModel.uiState.value.isRefreshing)
    }

    @Test
    fun should_TransitionToErrorNotFound_when_GamesListIsEmpty() = runTest {
        val fakeGameRepo = object : GameRepository {
            override suspend fun getPhase1Games(): Result<List<Game>, DomainError> =
                Result.Success(emptyList())
            override suspend fun getGameByRound(round: Int): Result<Game, DomainError> =
                Result.Error(DomainError.Local.NotFound)
        }
        val fakeAiRepo = object : AiAnalysisRepository {
            override suspend fun getInsights(games: List<Game>): Result<List<Insight>, DomainError> =
                Result.Success(emptyList())
            override suspend fun getDrills(games: List<Game>): Result<List<Drill>, DomainError> =
                Result.Success(emptyList())
        }

        val getPhase1GamesUseCase = GetPhase1GamesUseCase(fakeGameRepo)
        val generateInsightsUseCase = GenerateInsightsUseCase(fakeGameRepo, fakeAiRepo)
        val viewModel = AnalysisViewModel(getPhase1GamesUseCase, generateInsightsUseCase, mapper)

        viewModel.loadAnalysis()
        testDispatcher.scheduler.advanceUntilIdle()

        val content = viewModel.uiState.value.content
        assertTrue(content is AnalysisUiContent.Error)
        assertEquals(DomainError.Local.NotFound, (content as AnalysisUiContent.Error).error)
        assertFalse(viewModel.uiState.value.isRefreshing)
    }
}
