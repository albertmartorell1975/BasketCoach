package com.martorell.albert.basketcoach.ui.dashboard

import com.martorell.albert.basketcoach.data.repository.GameRepositoryImpl
import com.martorell.albert.basketcoach.data.source.local.GameLocalDataSourceImpl
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.ui.dashboard.mapper.DashboardUiMapper
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DashboardUiMapperTest {

    private val mapper = DashboardUiMapper()

    @Test
    fun should_MapGamesToDashboardUiState_when_ValidGamesProvided() = runBlocking {
        val repo = GameRepositoryImpl(GameLocalDataSourceImpl())
        val gamesResult = repo.getPhase1Games()
        assertTrue(gamesResult is Result.Success)
        val games = (gamesResult as Result.Success).data

        val success = mapper.toSuccess(games)

        // Summary assertions
        assertEquals(3, success.summary.wins)
        assertEquals(11, success.summary.losses)
        assertEquals(21.4, success.summary.winPercentage, 0.01)
        assertEquals(14, success.summary.totalGames)

        // KPI assertions
        assertEquals(64.6, success.kpis.scoring.averageScore, 0.01)
        assertEquals(73.7, success.kpis.scoring.opponentAverageScore, 0.01)
        assertEquals(-9.1, success.kpis.scoring.netDifferential, 0.01)
        assertEquals(22.8, success.kpis.rebounds.averageRebounds, 0.01)
        assertEquals(30.4, success.kpis.rebounds.opponentAverageRebounds, 0.01)
        assertEquals(-7.6, success.kpis.rebounds.netDifferential, 0.01)
        assertEquals(6.3, success.kpis.rebounds.offensiveRebounds, 0.01)
        assertEquals(16.5, success.kpis.rebounds.defensiveRebounds, 0.01)
        assertEquals(40.5, success.kpis.shooting.efgPercentage, 0.01)
        assertEquals(0.65, success.kpis.gameControl.astToRatio, 0.01)

        // Evolution assertions
        assertEquals(14, success.evolution.rounds.size)
        assertTrue(success.evolution.rounds[2].isWin)
        assertEquals(71, success.evolution.rounds[2].piaScore)
        assertEquals(68, success.evolution.rounds[2].rivalScore)
        assertTrue(success.evolution.rounds[8].isWin)
        assertEquals(68, success.evolution.rounds[8].piaScore)
        assertEquals(65, success.evolution.rounds[8].rivalScore)
        assertTrue(success.evolution.rounds[9].isWin)
        assertEquals(67, success.evolution.rounds[9].piaScore)
        assertEquals(65, success.evolution.rounds[9].rivalScore)

        // Match table assertions
        assertEquals(14, success.matches.size)
        assertEquals("CEB Escola Pia Sabadell", success.matches[0].homeTeam)
        assertEquals("CB Cornellà", success.matches[0].awayTeam)
        assertEquals(62, success.matches[0].homePoints)
        assertEquals(78, success.matches[0].awayPoints)
        assertEquals(22, success.matches[0].personalFouls)
        assertEquals(4, success.matches[0].secondChancePoints)
        assertEquals(14, success.matches[0].opponentSecondChancePoints)
    }

    @Test(expected = IllegalArgumentException::class)
    fun should_ThrowIllegalArgumentException_when_GamesListIsEmpty() {
        mapper.toSuccess(emptyList())
    }
}
