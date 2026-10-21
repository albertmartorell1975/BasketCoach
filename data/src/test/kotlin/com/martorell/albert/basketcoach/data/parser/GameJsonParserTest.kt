package com.martorell.albert.basketcoach.data.parser

import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Result
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.InputStream

class GameJsonParserTest {

    private lateinit var parser: GameJsonParser

    @Before
    fun setUp() {
        parser = GameJsonParserImpl()
    }

    private fun openDatasetStream(): InputStream {
        val stream = javaClass.classLoader?.getResourceAsStream("dataset.json")
        return checkNotNull(stream) { "Resource dataset.json not found on classpath" }
    }

    @Test
    fun parseGames_fromResourceStream_returnsAll14Games() {
        val result = parser.parseGames(openDatasetStream())

        assertTrue("Expected Result.Success", result is Result.Success)
        val games = (result as Result.Success).data
        assertEquals("Expected exactly 14 games", 14, games.size)
        assertEquals(1, games.first().round)
        assertEquals(14, games.last().round)
    }

    @Test
    fun parseGames_round1HomeLoss_matchesExactData() {
        val result = parser.parseGames(openDatasetStream())
        val games = (result as Result.Success).data
        val round1 = games.first { it.round == 1 }

        assertEquals("13/09/2026", round1.date)
        assertEquals("16:15", round1.time)
        assertEquals("Sabadell", round1.venue.city)
        assertEquals("ESCOLA PIA SABADELL", round1.venue.arena)
        assertEquals("GARCILASO, 99", round1.venue.address)
        assertEquals("BASQUETPIASABADELL BLAU", round1.homeTeam)
        assertEquals("CB. CORNELLÀ", round1.awayTeam)

        assertEquals(62, round1.finalScore.homePoints)
        assertEquals(78, round1.finalScore.awayPoints)

        assertTrue(round1.isHomeGame)
        assertFalse(round1.isWin)
        assertEquals(-16, round1.pointDifferential)
        assertEquals("CB. CORNELLÀ", round1.opponentName)

        val stats = round1.homeStats
        assertEquals(52.4, stats.ftPct, 0.001)
        assertEquals(42.1, stats.fg2Pct, 0.001)
        assertEquals(21.4, stats.fg3Pct, 0.001)
        assertEquals(38.6, stats.efgPct, 0.001)
        assertEquals(5, stats.offensiveRebounds)
        assertEquals(16, stats.defensiveRebounds)
        assertEquals(21, stats.totalRebounds)
        assertEquals(19, stats.turnovers)
        assertEquals(8, stats.assists)
        assertEquals(0.42, stats.astToRatio, 0.001)
        assertEquals(4, stats.steals)
        assertEquals(1, stats.blocks)
        assertEquals(4, stats.secondChancePoints)
        assertEquals(6, stats.fastBreakPoints)
        assertEquals(22, stats.personalFouls)
        assertEquals(62, stats.points)
    }

    @Test
    fun parseGames_round3HomeWin_matchesExactData() {
        val result = parser.parseGames(openDatasetStream())
        val games = (result as Result.Success).data
        val round3 = games.first { it.round == 3 }

        assertEquals("BASQUETPIASABADELL BLAU", round3.homeTeam)
        assertEquals("CEB SANT JORDI BLAU", round3.awayTeam)
        assertEquals(71, round3.finalScore.homePoints)
        assertEquals(68, round3.finalScore.awayPoints)
        assertTrue(round3.isHomeGame)
        assertTrue(round3.isWin)
        assertEquals(3, round3.pointDifferential)
        assertEquals(71, round3.teamStats.points)
    }

    @Test
    fun parseGames_round10AwayWin_matchesExactData() {
        val result = parser.parseGames(openDatasetStream())
        val games = (result as Result.Success).data
        val round10 = games.first { it.round == 10 }

        assertEquals("CEB SANT JORDI BLAU", round10.homeTeam)
        assertEquals("BASQUETPIASABADELL BLAU", round10.awayTeam)
        assertEquals(65, round10.finalScore.homePoints)
        assertEquals(67, round10.finalScore.awayPoints)
        assertFalse(round10.isHomeGame)
        assertTrue(round10.isWin)
        assertEquals(2, round10.pointDifferential)
        assertEquals("CEB SANT JORDI BLAU", round10.opponentName)
        assertEquals(67, round10.teamScore)
        assertEquals(65, round10.opponentScore)
    }

    @Test
    fun parseGames_corruptedJson_returnsDatabaseErrorWithoutCrashing() {
        val corruptedJson = "{ not a valid json at all }"
        val result = parser.parseGames(corruptedJson)

        assertTrue("Expected Result.Error", result is Result.Error)
        val error = (result as Result.Error).error
        assertTrue(error is DomainError.Local.DatabaseError)
    }
}
