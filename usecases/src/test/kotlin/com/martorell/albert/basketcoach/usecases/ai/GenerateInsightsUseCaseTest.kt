package com.martorell.albert.basketcoach.usecases.ai

import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.InsightCategory
import com.martorell.albert.basketcoach.domain.model.InsightMetric
import com.martorell.albert.basketcoach.domain.model.InsightType
import com.martorell.albert.basketcoach.domain.model.MetricSentiment

import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.usecases.fakes.FakeAiAnalysisRepository
import com.martorell.albert.basketcoach.usecases.fakes.FakeGameRepository
import com.martorell.albert.basketcoach.usecases.fakes.TestGameFactory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GenerateInsightsUseCaseTest {

    private lateinit var fakeGameRepository: FakeGameRepository
    private lateinit var fakeAiAnalysisRepository: FakeAiAnalysisRepository
    private lateinit var useCase: GenerateInsightsUseCase

    @Before
    fun setUp() {
        fakeGameRepository = FakeGameRepository()
        fakeAiAnalysisRepository = FakeAiAnalysisRepository()
        useCase = GenerateInsightsUseCase(fakeGameRepository, fakeAiAnalysisRepository)
    }

    @Test
    fun invoke_success_retrievesInsightsWithGames() = runTest {
        val games = listOf(
            TestGameFactory.createGame(round = 1, isHome = true, teamPoints = 70, oppPoints = 65),
            TestGameFactory.createGame(round = 2, isHome = false, teamPoints = 55, oppPoints = 80)
        )
        val expectedInsights = listOf(
            Insight(
                id = "insight-1",
                title = "Defensive Rebounding Vulnerability",
                description = "Conceding second chance points in losses.",
                category = InsightCategory.DEFENSE,
                relatedGameRounds = listOf(2),
                type = InsightType.IMPROVEMENT,
                contextTag = "Rebot",
                metric = InsightMetric(
                    primaryLabel = "Rebots Ofensius Concedits",
                    primaryValue = "11.6",
                    primarySubValue = "14.8 pts de 2a oportunitat",
                    secondaryLabel = "Diferència",
                    secondaryValue = "-7.8 reb",
                    secondaryTag = "dataset:reb",
                    sentiment = MetricSentiment.NEGATIVE
                ),
                priority = "Alta",
                footerNote = "Pilar corrector",
                exploreChatPrompt = "Com millorar el rebot?"
            )
        )
        fakeGameRepository.gamesToReturn = Result.Success(games)
        fakeAiAnalysisRepository.insightsToReturn = Result.Success(expectedInsights)

        val result = useCase()

        assertTrue(result is Result.Success)
        assertEquals(expectedInsights, (result as Result.Success).data)
        assertEquals(1, fakeGameRepository.getPhase1GamesCallCount)
        assertEquals(1, fakeAiAnalysisRepository.getInsightsCallCount)
        assertEquals(games, fakeAiAnalysisRepository.lastGamesForInsights)
    }

    @Test
    fun invoke_whenGameLoadingFails_propagatesErrorWithoutCallingAiRepository() = runTest {
        fakeGameRepository.gamesToReturn = Result.Error(DomainError.Local.NotFound)

        val result = useCase()

        assertTrue(result is Result.Error)
        assertEquals(DomainError.Local.NotFound, (result as Result.Error).error)
        assertEquals(1, fakeGameRepository.getPhase1GamesCallCount)
        assertEquals(0, fakeAiAnalysisRepository.getInsightsCallCount)
    }

    @Test
    fun invoke_whenAiRepositoryFails_propagatesAiError() = runTest {
        val games = listOf(TestGameFactory.createGame(round = 1))
        fakeGameRepository.gamesToReturn = Result.Success(games)
        fakeAiAnalysisRepository.insightsToReturn = Result.Error(DomainError.Network.Timeout)

        val result = useCase()

        assertTrue(result is Result.Error)
        assertEquals(DomainError.Network.Timeout, (result as Result.Error).error)
        assertEquals(1, fakeGameRepository.getPhase1GamesCallCount)
        assertEquals(1, fakeAiAnalysisRepository.getInsightsCallCount)
    }
}
