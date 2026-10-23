package com.martorell.albert.basketcoach.data.repository

import com.martorell.albert.basketcoach.data.dto.GameDto
import com.martorell.albert.basketcoach.data.source.local.GameLocalDataSource
import com.martorell.albert.basketcoach.data.source.local.GameLocalDataSourceImpl
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Result
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.FileNotFoundException

class GameRepositoryImplTest {

    private lateinit var repository: GameRepositoryImpl

    @Before
    fun setUp() {
        repository = GameRepositoryImpl(GameLocalDataSourceImpl())
    }

    private suspend fun loadGames(): List<Game> {
        val result = repository.getPhase1Games()
        assertTrue("Expected Result.Success", result is Result.Success)
        return (result as Result.Success).data
    }

    @Test
    fun getPhase1Games_returnsAll14GamesSortedByRound() = runBlocking {
        val games = loadGames()

        assertEquals(14, games.size)
        assertEquals(1, games.first().round)
        assertEquals(14, games.last().round)
    }

    @Test
    fun getPhase1Games_round1HomeLoss_matchesExactData() = runBlocking {
        val round1 = loadGames().first { it.round == 1 }

        assertEquals("13/09/2026", round1.date)
        assertEquals("16:15", round1.time)
        assertEquals("Sabadell", round1.venue.city)
        assertEquals("BASQUETPIASABADELL BLAU", round1.homeTeam)
        assertEquals(62, round1.finalScore.homePoints)
        assertEquals(78, round1.finalScore.awayPoints)
        assertTrue(round1.isHomeGame)
        assertFalse(round1.isWin)
        assertEquals(-16, round1.pointDifferential)

        val stats = round1.homeStats
        assertEquals(52.4, stats.ftPct, 0.001)
        assertEquals(19, stats.turnovers)
        assertEquals(8, stats.assists)
        assertEquals(0.42, stats.astToRatio, 0.001)
        assertEquals(22, stats.personalFouls)
        assertEquals(62, stats.points)
    }

    @Test
    fun getPhase1Games_round3HomeWin_matchesExactData() = runBlocking {
        val round3 = loadGames().first { it.round == 3 }

        assertEquals(71, round3.finalScore.homePoints)
        assertEquals(68, round3.finalScore.awayPoints)
        assertTrue(round3.isHomeGame)
        assertTrue(round3.isWin)
        assertEquals(3, round3.pointDifferential)
    }

    @Test
    fun getPhase1Games_round10AwayWin_matchesExactData() = runBlocking {
        val round10 = loadGames().first { it.round == 10 }

        assertFalse(round10.isHomeGame)
        assertTrue(round10.isWin)
        assertEquals(2, round10.pointDifferential)
        assertEquals(67, round10.teamScore)
        assertEquals(65, round10.opponentScore)
    }

    @Test
    fun getGameByRound_validRounds_returnsCorrectGame() = runBlocking {
        for (round in 1..14) {
            val result = repository.getGameByRound(round)
            assertTrue("Expected success for round $round", result is Result.Success)
            assertEquals(round, (result as Result.Success).data.round)
        }
    }

    @Test
    fun getGameByRound_missingRound_returnsNotFound() = runBlocking {
        val result = repository.getGameByRound(99)

        assertTrue("Expected Result.Error", result is Result.Error)
        assertTrue(
            "Expected NotFound error",
            (result as Result.Error).error is DomainError.Local.NotFound
        )
    }

    @Test
    fun getPhase1Games_missingDataset_mapsToNotFound() = runBlocking {
        val failingSource = object : GameLocalDataSource {
            override suspend fun getGames(): List<GameDto> = throw FileNotFoundException("missing")
        }

        val result = GameRepositoryImpl(failingSource).getPhase1Games()

        assertTrue("Expected Result.Error", result is Result.Error)
        assertTrue((result as Result.Error).error is DomainError.Local.NotFound)
    }
}
