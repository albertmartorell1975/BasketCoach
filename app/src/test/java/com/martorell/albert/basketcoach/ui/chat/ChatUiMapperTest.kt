package com.martorell.albert.basketcoach.ui.chat

import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.MessageSender
import com.martorell.albert.basketcoach.domain.model.OUR_TEAM_NAME
import com.martorell.albert.basketcoach.domain.model.Score
import com.martorell.albert.basketcoach.domain.model.TeamStats
import com.martorell.albert.basketcoach.domain.model.Venue
import com.martorell.albert.basketcoach.ui.chat.mapper.ChatUiMapper
import com.martorell.albert.basketcoach.ui.chat.model.ChatLeftPaneMode
import com.martorell.albert.basketcoach.ui.chat.model.ChatTopicId
import com.martorell.albert.basketcoach.ui.chat.model.ChatUiContent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatUiMapperTest {

    private val mapper = ChatUiMapper()

    private fun sampleGame(round: Int, isWin: Boolean): Game {
        val homePoints = if (isWin) 70 else 50
        val awayPoints = 60
        val stats = TeamStats(
            ftPct = 66.7,
            fg2Pct = 50.0,
            fg3Pct = 33.3,
            efgPct = 45.0,
            offensiveRebounds = 10,
            defensiveRebounds = 20,
            totalRebounds = 30,
            turnovers = 14,
            assists = 12,
            astToRatio = 0.86,
            steals = 5,
            blocks = 2,
            secondChancePoints = 8,
            fastBreakPoints = 6,
            personalFouls = 18,
            points = homePoints
        )
        val oppStats = TeamStats(
            ftPct = 66.7,
            fg2Pct = 42.8,
            fg3Pct = 33.3,
            efgPct = 45.0,
            offensiveRebounds = 8,
            defensiveRebounds = 22,
            totalRebounds = 30,
            turnovers = 16,
            assists = 10,
            astToRatio = 0.62,
            steals = 6,
            blocks = 1,
            secondChancePoints = 6,
            fastBreakPoints = 4,
            personalFouls = 15,
            points = awayPoints
        )
        return Game(
            round = round,
            date = "2023-10-08",
            time = "17:45",
            venue = Venue("Sabadell", "Pavello Nord", "Carrer Ronda 1"),
            homeTeam = OUR_TEAM_NAME,
            awayTeam = "Rival",
            finalScore = Score(homePoints, awayPoints),
            homeStats = stats,
            awayStats = oppStats
        )
    }

    @Test
    fun mapToContent_withValidGamesAndMessages_mapsCorrectly() {
        val games = listOf(sampleGame(1, isWin = true), sampleGame(2, isWin = false))
        val messages = listOf(
            ChatMessage("1", MessageSender.COACH, "Hola", 1000L),
            ChatMessage("2", MessageSender.AI, "Hola Jordi", 2000L)
        )

        val content = mapper.mapToContent(
            games = games,
            messages = messages,
            leftPaneMode = ChatLeftPaneMode.TOPIC_LIST,
            activeTopicId = ChatTopicId.REBOUND_CONTROL,
            inputText = ""
        )

        assertTrue(content is ChatUiContent.Success)
        val success = content as ChatUiContent.Success
        assertEquals(ChatTopicId.REBOUND_CONTROL, success.activeTopicId)
        assertEquals(ChatLeftPaneMode.TOPIC_LIST, success.leftPaneMode)
        assertEquals(1, success.metaHeader.wins)
        assertEquals(1, success.metaHeader.losses)
        assertEquals(4, success.topicList.size)
        assertTrue(success.topicList.first().hasDiagnosticEvidence)
        assertEquals(2, success.messages.size)
        assertEquals("Hola", success.messages[0].text)
        assertEquals("Hola Jordi", success.messages[1].text)
    }

    @Test
    fun mapToContent_withEmptyGames_returnsErrorNotFound() {
        val content = mapper.mapToContent(
            games = emptyList(),
            messages = emptyList(),
            leftPaneMode = ChatLeftPaneMode.TOPIC_LIST,
            activeTopicId = ChatTopicId.REBOUND_CONTROL,
            inputText = ""
        )

        assertTrue(content is ChatUiContent.Error)
        assertEquals(DomainError.Local.NotFound, (content as ChatUiContent.Error).error)
    }
}
