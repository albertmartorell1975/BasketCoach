package com.martorell.albert.basketcoach.data.repository

import com.martorell.albert.basketcoach.data.network.client.DevExpertApiClient
import com.martorell.albert.basketcoach.data.network.dto.AiAnalysisResponseDto
import com.martorell.albert.basketcoach.data.network.dto.DrillDto
import com.martorell.albert.basketcoach.data.network.dto.InsightDto
import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.InsightCategory
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.model.Score
import com.martorell.albert.basketcoach.domain.model.TeamStats
import com.martorell.albert.basketcoach.domain.model.Venue
import com.martorell.albert.basketcoach.domain.repository.GameRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AiAnalysisRepositoryImplTest {

    private val dummyStats = TeamStats(
        ftPct = 70.0,
        fg2Pct = 45.0,
        fg3Pct = 30.0,
        efgPct = 50.0,
        offensiveRebounds = 10,
        defensiveRebounds = 25,
        totalRebounds = 35,
        turnovers = 15,
        assists = 12,
        astToRatio = 0.8,
        steals = 8,
        blocks = 2,
        secondChancePoints = 10,
        fastBreakPoints = 14,
        personalFouls = 18
    )

    private val dummyGame = Game(
        round = 1,
        date = "2023-10-01",
        time = "18:00",
        venue = Venue(city = "Sabadell", arena = "Pavello", address = "Carrer Major"),
        homeTeam = "BASQUETPIASABADELL BLAU",
        awayTeam = "RIVAL",
        finalScore = Score(homePoints = 65, awayPoints = 70),
        homeStats = dummyStats,
        awayStats = dummyStats
    )

    private val fakeGameRepository = object : GameRepository {
        override suspend fun getPhase1Games(): Result<List<Game>, DomainError> {
            return Result.Success(listOf(dummyGame))
        }

        override suspend fun getGameByRound(round: Int): Result<Game, DomainError> {
            return Result.Success(dummyGame)
        }
    }

    private class FakeApiClient(
        var analysisCallCount: Int = 0,
        var analysisResponse: Result<AiAnalysisResponseDto, DomainError>
    ) : DevExpertApiClient {
        override suspend fun generateAnalysis(gameContext: String): Result<AiAnalysisResponseDto, DomainError> {
            analysisCallCount++
            return analysisResponse
        }

        override suspend fun generateChatResponse(
            conversationHistory: List<ChatMessage>,
            newPrompt: String,
            gameContext: String?
        ): Result<String, DomainError> {
            return Result.Success("Chat response")
        }
    }

    @Test
    fun `getInsights returns mapped domain insights and populates cache`() = runTest {
        val dto = AiAnalysisResponseDto(
            insights = listOf(
                InsightDto(
                    id = "i1",
                    title = "Turnover Vulnerability",
                    description = "Too many lost balls in transition",
                    category = "OFFENSE",
                    relatedGameRounds = listOf(1)
                )
            ),
            drills = listOf(
                DrillDto(
                    id = "d1",
                    name = "3v2 Transition Drill",
                    objective = "Improve transition defense",
                    instructions = listOf("Start from baseline", "Run 3v2 break"),
                    focusArea = "Transition"
                )
            )
        )
        val fakeClient = FakeApiClient(analysisResponse = Result.Success(dto))
        val repository = AiAnalysisRepositoryImpl(fakeGameRepository, fakeClient)

        val insightsResult = repository.getInsights()

        assertTrue(insightsResult is Result.Success)
        val insights = (insightsResult as Result.Success).data
        assertEquals(1, insights.size)
        assertEquals("Turnover Vulnerability", insights.first().title)
        assertEquals(InsightCategory.OFFENSE, insights.first().category)
        assertEquals(listOf(1), insights.first().relatedGameRounds)
        assertEquals(1, fakeClient.analysisCallCount)
    }

    @Test
    fun `getDrills uses shared cache when getInsights was called first without second network call`() = runTest {
        val dto = AiAnalysisResponseDto(
            insights = listOf(
                InsightDto("i1", "Title", "Desc", "DEFENSE", listOf(1))
            ),
            drills = listOf(
                DrillDto("d1", "Drill 1", "Obj 1", listOf("Step 1"), "Focus")
            )
        )
        val fakeClient = FakeApiClient(analysisResponse = Result.Success(dto))
        val repository = AiAnalysisRepositoryImpl(fakeGameRepository, fakeClient)

        val insightsResult = repository.getInsights()
        assertTrue(insightsResult is Result.Success)
        assertEquals(1, fakeClient.analysisCallCount)

        val drillsResult = repository.getDrills()
        assertTrue(drillsResult is Result.Success)
        val drills = (drillsResult as Result.Success).data
        assertEquals(1, drills.size)
        assertEquals("Drill 1", drills.first().name)
        // Verified: only 1 API call across both insights and drills
        assertEquals(1, fakeClient.analysisCallCount)
    }

    @Test
    fun `propagates API errors properly`() = runTest {
        val fakeClient = FakeApiClient(analysisResponse = Result.Error(DomainError.Network.NoInternet))
        val repository = AiAnalysisRepositoryImpl(fakeGameRepository, fakeClient)

        val result = repository.getInsights()

        assertTrue(result is Result.Error)
        assertEquals(DomainError.Network.NoInternet, (result as Result.Error).error)
    }
}
