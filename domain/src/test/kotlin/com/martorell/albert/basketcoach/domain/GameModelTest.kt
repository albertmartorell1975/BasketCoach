package com.martorell.albert.basketcoach.domain

import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.MessageSender
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.model.Score
import com.martorell.albert.basketcoach.domain.model.TeamStats
import com.martorell.albert.basketcoach.domain.model.Venue
import com.martorell.albert.basketcoach.domain.model.map
import com.martorell.albert.basketcoach.domain.model.onError
import com.martorell.albert.basketcoach.domain.model.onSuccess
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameModelTest {

    private fun createDummyStats(points: Int): TeamStats = TeamStats(
        ftPct = 70.0,
        fg2Pct = 50.0,
        fg3Pct = 30.0,
        efgPct = 45.0,
        offensiveRebounds = 10,
        defensiveRebounds = 20,
        totalRebounds = 30,
        turnovers = 12,
        assists = 14,
        astToRatio = 1.16,
        steals = 6,
        blocks = 2,
        secondChancePoints = 8,
        fastBreakPoints = 10,
        personalFouls = 18,
        points = points
    )

    @Test
    fun gameOutcomeCalculations_homeWinAwayWinAndHomeLoss() {
        val homeWin = Game(
            round = 3,
            date = "04/10/2026",
            time = "17:30",
            venue = Venue("Sabadell", "ESCOLA PIA SABADELL", "GARCILASO, 99"),
            homeTeam = "BASQUETPIASABADELL BLAU",
            awayTeam = "CEB SANT JORDI BLAU",
            finalScore = Score(homePoints = 71, awayPoints = 68),
            homeStats = createDummyStats(71),
            awayStats = createDummyStats(68)
        )
        assertTrue(homeWin.isHomeGame)
        assertTrue(homeWin.isWin)
        assertEquals(71, homeWin.teamScore)
        assertEquals(68, homeWin.opponentScore)
        assertEquals(3, homeWin.pointDifferential)
        assertEquals("CEB SANT JORDI BLAU", homeWin.opponentName)

        val awayWin = Game(
            round = 10,
            date = "29/11/2026",
            time = "18:00",
            venue = Venue("Rubí", "PAVELLÓ CEB SANT JORDI", "CERVANTES, S/N"),
            homeTeam = "CEB SANT JORDI BLAU",
            awayTeam = "BASQUETPIASABADELL BLAU",
            finalScore = Score(homePoints = 65, awayPoints = 67),
            homeStats = createDummyStats(65),
            awayStats = createDummyStats(67)
        )
        assertFalse(awayWin.isHomeGame)
        assertTrue(awayWin.isWin)
        assertEquals(67, awayWin.teamScore)
        assertEquals(65, awayWin.opponentScore)
        assertEquals(2, awayWin.pointDifferential)
        assertEquals("CEB SANT JORDI BLAU", awayWin.opponentName)

        val homeLoss = Game(
            round = 1,
            date = "13/09/2026",
            time = "16:15",
            venue = Venue("Sabadell", "ESCOLA PIA SABADELL", "GARCILASO, 99"),
            homeTeam = "BASQUETPIASABADELL BLAU",
            awayTeam = "CB. CORNELLÀ",
            finalScore = Score(homePoints = 62, awayPoints = 78),
            homeStats = createDummyStats(62),
            awayStats = createDummyStats(78)
        )
        assertTrue(homeLoss.isHomeGame)
        assertFalse(homeLoss.isWin)
        assertEquals(-16, homeLoss.pointDifferential)
    }

    @Test
    fun chatMessageSenderFlags_distinguishCoachAndAi() {
        val coachMessage = ChatMessage("msg_coach", MessageSender.COACH, "Question", 1000L)
        assertTrue(coachMessage.isFromCoach)
        assertFalse(coachMessage.isFromAi)

        val aiMessage = ChatMessage("msg_ai", MessageSender.AI, "Answer", 2000L)
        assertFalse(aiMessage.isFromCoach)
        assertTrue(aiMessage.isFromAi)
    }

    @Test
    fun resultFunctionalCombinators_mapOnSuccessAndOnError() {
        val success: Result<Int, DomainError> = Result.Success(42)
        val mappedSuccess = success.map { it * 2 }
        assertEquals(84, (mappedSuccess as Result.Success).data)

        var successCalled = false
        success.onSuccess { successCalled = true }
            .onError { throw AssertionError("onError should not fire on Success") }
        assertTrue(successCalled)

        val error: Result<Int, DomainError> = Result.Error(DomainError.Network.NoInternet)
        val mappedError = error.map { it * 2 }
        assertEquals(DomainError.Network.NoInternet, (mappedError as Result.Error).error)

        var errorCalled = false
        error.onError { errorCalled = true }
            .onSuccess { throw AssertionError("onSuccess should not fire on Error") }
        assertTrue(errorCalled)
    }
}
