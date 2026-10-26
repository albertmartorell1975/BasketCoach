package com.martorell.albert.basketcoach.domain

import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Drill
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.InsightCategory
import com.martorell.albert.basketcoach.domain.model.InsightMetric
import com.martorell.albert.basketcoach.domain.model.InsightType
import com.martorell.albert.basketcoach.domain.model.MessageSender
import com.martorell.albert.basketcoach.domain.model.MetricSentiment
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.model.Score
import com.martorell.albert.basketcoach.domain.model.TeamStats
import com.martorell.albert.basketcoach.domain.model.Venue
import com.martorell.albert.basketcoach.domain.model.map
import com.martorell.albert.basketcoach.domain.model.onError
import com.martorell.albert.basketcoach.domain.model.onSuccess
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameModelTest {

    private fun createDummyStats(points: Int): TeamStats {
        return TeamStats(
            ftPct = 65.0,
            fg2Pct = 48.0,
            fg3Pct = 32.0,
            efgPct = 45.0,
            offensiveRebounds = 10,
            defensiveRebounds = 20,
            totalRebounds = 30,
            turnovers = 15,
            assists = 12,
            astToRatio = 0.8,
            steals = 6,
            blocks = 2,
            secondChancePoints = 8,
            fastBreakPoints = 10,
            personalFouls = 18,
            points = points
        )
    }

    @Test
    fun round3HomeWinCalculation() {
        val homeStats = createDummyStats(points = 71)
        val awayStats = createDummyStats(points = 68)
        val game = Game(
            round = 3,
            date = "08/10/2026",
            time = "17:30",
            venue = Venue(
                city = "Sabadell",
                arena = "ESCOLA PIA SABADELL",
                address = "GARCILASO, 99"
            ),
            homeTeam = "BASQUETPIASABADELL BLAU",
            awayTeam = "CEB SANT JORDI BLAU",
            finalScore = Score(homePoints = 71, awayPoints = 68),
            homeStats = homeStats,
            awayStats = awayStats
        )

        assertTrue("Should be recognized as home game", game.isHomeGame)
        assertEquals(71, game.teamScore)
        assertEquals(68, game.opponentScore)
        assertTrue("Should be recognized as a win", game.isWin)
        assertEquals("CEB SANT JORDI BLAU", game.opponentName)
        assertEquals(3, game.pointDifferential)
        assertEquals(homeStats, game.teamStats)
        assertEquals(awayStats, game.opponentStats)
    }

    @Test
    fun round1HomeLossCalculation() {
        val homeStats = createDummyStats(points = 62)
        val awayStats = createDummyStats(points = 78)
        val game = Game(
            round = 1,
            date = "24/09/2026",
            time = "17:30",
            venue = Venue(
                city = "Sabadell",
                arena = "ESCOLA PIA SABADELL",
                address = "GARCILASO, 99"
            ),
            homeTeam = "BASQUETPIASABADELL BLAU",
            awayTeam = "CB CORNELLÀ",
            finalScore = Score(homePoints = 62, awayPoints = 78),
            homeStats = homeStats,
            awayStats = awayStats
        )

        assertTrue("Should be recognized as home game", game.isHomeGame)
        assertEquals(62, game.teamScore)
        assertEquals(78, game.opponentScore)
        assertFalse("Should be recognized as a loss", game.isWin)
        assertEquals("CB CORNELLÀ", game.opponentName)
        assertEquals(-16, game.pointDifferential)
        assertEquals(homeStats, game.teamStats)
        assertEquals(awayStats, game.opponentStats)
    }

    @Test
    fun round10AwayWinCalculation() {
        val homeStats = createDummyStats(points = 65)
        val awayStats = createDummyStats(points = 67)
        val game = Game(
            round = 10,
            date = "29/11/2026",
            time = "18:00",
            venue = Venue(
                city = "Rubí",
                arena = "PAVELLÓ CEB SANT JORDI",
                address = "CERVANTES, S/N"
            ),
            homeTeam = "CEB SANT JORDI BLAU",
            awayTeam = "BASQUETPIASABADELL BLAU",
            finalScore = Score(homePoints = 65, awayPoints = 67),
            homeStats = homeStats,
            awayStats = awayStats
        )

        assertFalse("Should be recognized as away game", game.isHomeGame)
        assertEquals(67, game.teamScore)
        assertEquals(65, game.opponentScore)
        assertTrue("Should be recognized as a win", game.isWin)
        assertEquals("CEB SANT JORDI BLAU", game.opponentName)
        assertEquals(2, game.pointDifferential)
        assertEquals(awayStats, game.teamStats)
        assertEquals(homeStats, game.opponentStats)
    }

    @Test
    fun teamStatsMetricIntegrity() {
        val stats = TeamStats(
            ftPct = 52.4,
            fg2Pct = 42.1,
            fg3Pct = 21.4,
            efgPct = 38.6,
            offensiveRebounds = 5,
            defensiveRebounds = 16,
            totalRebounds = 21,
            turnovers = 19,
            assists = 8,
            astToRatio = 0.42,
            steals = 4,
            blocks = 1,
            secondChancePoints = 4,
            fastBreakPoints = 6,
            personalFouls = 22,
            points = 62
        )

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
    fun insightAndDrillEntities() {
        val metric = InsightMetric(
            label = "Percentatge T2 Zona",
            value = "58.4%",
            unit = "%",
            deltaText = "+8.2% sobre mitjana lliga",
            sentiment = MetricSentiment.POSITIVE,
            evidenceLabel = "Punts / Pos.",
            evidenceValue = "1.24 pts/pos",
            datasetKey = "dataset:paint_eff"
        )

        val insight = Insight(
            id = "insight_1",
            title = "Eficiència en Tir Interior",
            description = "Alta productivitat en finalitzacions sota cistella",
            category = InsightCategory.OFFENSE,
            relatedGameRounds = listOf(1, 4, 6, 8, 11, 13),
            type = InsightType.STRENGTH,
            contextTag = "Fase Ofensiva · Pintura",
            metric = metric,
            footerNote = "Factor diferencial",
            priority = null,
            exploreChatPrompt = "Com mantenir l'eficiència?"
        )

        assertEquals("insight_1", insight.id)
        assertEquals(InsightCategory.OFFENSE, insight.category)
        assertEquals(InsightType.STRENGTH, insight.type)
        assertEquals("Fase Ofensiva · Pintura", insight.contextTag)
        assertNotNull(insight.metric)
        assertEquals("58.4%", insight.metric?.value)
        assertEquals(MetricSentiment.POSITIVE, insight.metric?.sentiment)
        assertEquals("Factor diferencial", insight.footerNote)

        val drill = Drill(
            id = "drill_1",
            name = "Defensive Rebounding Box-Out Circuit",
            objective = "Improve defensive box-out fundamentals",
            instructions = listOf("Pair up players under the rim", "Hold box-out position for 3 seconds"),
            focusArea = "Defensive Transition & Rebounding"
        )

        assertEquals("drill_1", drill.id)
        assertEquals("Defensive Rebounding Box-Out Circuit", drill.name)
        assertEquals(2, drill.instructions.size)
        assertEquals("Defensive Transition & Rebounding", drill.focusArea)
    }

    @Test
    fun chatMessageSenderTests() {
        val coachMessage = ChatMessage(
            id = "msg_coach",
            sender = MessageSender.COACH,
            text = "How can we improve our 3-point defense?",
            timestamp = 1700000000000L
        )

        assertTrue(coachMessage.isFromCoach)
        assertFalse(coachMessage.isFromAi)
        assertEquals(1700000000000L, coachMessage.timestamp)

        val aiMessage = ChatMessage(
            id = "msg_ai",
            sender = MessageSender.AI,
            text = "Focus on closing out on corner shooters.",
            timestamp = 1700000005000L
        )

        assertFalse(aiMessage.isFromCoach)
        assertTrue(aiMessage.isFromAi)
        assertEquals("msg_ai", aiMessage.id)
    }

    @Test
    fun resultFunctionalMappingTests() {
        val success: Result<Int, DomainError> = Result.Success(42)
        val mappedSuccess = success.map { it * 2 }
        assertTrue(mappedSuccess is Result.Success<*>)
        assertEquals(84, (mappedSuccess as Result.Success<*>).data)

        var successCallbackCalled = false
        success.onSuccess {
            assertEquals(42, it)
            successCallbackCalled = true
        }.onError {
            throw AssertionError("onError should not be called for Success")
        }
        assertTrue(successCallbackCalled)

        val error: Result<Int, DomainError> = Result.Error(DomainError.Network.NoInternet)
        val mappedError = error.map { it * 2 }
        assertTrue(mappedError is Result.Error<*>)
        assertEquals(DomainError.Network.NoInternet, (mappedError as Result.Error<*>).error)

        var errorCallbackCalled = false
        error.onError {
            assertEquals(DomainError.Network.NoInternet, it)
            errorCallbackCalled = true
        }.onSuccess {
            throw AssertionError("onSuccess should not be called for Error")
        }
        assertTrue(errorCallbackCalled)
    }
}
