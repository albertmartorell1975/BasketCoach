package com.martorell.albert.basketcoach.ui.chat

import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.InsightCategory
import com.martorell.albert.basketcoach.domain.model.InsightMetric
import com.martorell.albert.basketcoach.domain.model.InsightType
import com.martorell.albert.basketcoach.domain.model.MessageSender
import com.martorell.albert.basketcoach.domain.model.MetricSentiment
import com.martorell.albert.basketcoach.domain.model.OUR_TEAM_NAME
import com.martorell.albert.basketcoach.domain.model.Score
import com.martorell.albert.basketcoach.domain.model.TeamStats
import com.martorell.albert.basketcoach.domain.model.Venue
import com.martorell.albert.basketcoach.domain.repository.OPEN_INQUIRY_TOPIC_ID
import com.martorell.albert.basketcoach.ui.chat.mapper.ChatUiMapper
import com.martorell.albert.basketcoach.ui.chat.model.ChatUiContent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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

    private fun sampleInsight(id: String, type: InsightType, title: String): Insight {
        return Insight(
            id = id,
            title = title,
            description = "Description for $title",
            category = InsightCategory.DEFENSE,
            relatedGameRounds = listOf(1, 2),
            type = type,
            contextTag = "Defensa",
            metric = InsightMetric(
                primaryLabel = "Rebots Defensius",
                primaryValue = "13.2",
                primarySubValue = "-5.1",
                secondaryLabel = "Segones Opcions",
                secondaryValue = "14.8",
                secondaryTag = "2nd_chance",
                sentiment = MetricSentiment.NEGATIVE
            ),
            priority = "Crítica #1",
            footerNote = "Evidence footer",
            exploreChatPrompt = "Com millorem el rebot defensiu?"
        )
    }

    @Test
    fun mapToContent_withValidGamesAndInsights_mapsOnlyImprovementsAndAppendsOpenInquiry() {
        val games = listOf(sampleGame(1, isWin = true), sampleGame(2, isWin = false))
        val insights = listOf(
            sampleInsight("insight_imp_1", InsightType.IMPROVEMENT, "Rebot Defensiu"),
            sampleInsight("insight_str_1", InsightType.STRENGTH, "Joc Interior Eficient"),
            sampleInsight("insight_imp_2", InsightType.IMPROVEMENT, "Pèrdues al 3r Quart")
        )
        val messages = listOf(
            ChatMessage("1", MessageSender.COACH, "Hola", 1000L),
            ChatMessage("2", MessageSender.AI, "Hola Jordi", 2000L)
        )

        val content = mapper.mapToContent(
            games = games,
            insights = insights,
            messages = messages,
            activeTopicId = "insight_imp_1",
            inputText = ""
        )

        assertTrue(content is ChatUiContent.Success)
        val success = content as ChatUiContent.Success
        assertEquals("insight_imp_1", success.activeTopicId)
        assertEquals(1, success.metaHeader.wins)
        assertEquals(1, success.metaHeader.losses)
        // 2 improvement insights + 1 open inquiry = 3 topic cards
        assertEquals(3, success.topicList.size)
        assertEquals("insight_imp_1", success.topicList[0].id)
        assertFalse(success.topicList[0].isOpenInquiry)
        assertTrue(success.topicList[0].isPriority)
        assertEquals("insight_imp_2", success.topicList[1].id)
        assertFalse(success.topicList[1].isOpenInquiry)
        assertFalse(success.topicList[1].isPriority)
        assertEquals(OPEN_INQUIRY_TOPIC_ID, success.topicList[2].id)
        assertTrue(success.topicList[2].isOpenInquiry)
        assertFalse(success.topicList[2].isPriority)
        assertEquals(2, success.messages.size)
    }

    @Test
    fun mapToContent_withEmptyGames_returnsErrorNotFound() {
        val content = mapper.mapToContent(
            games = emptyList(),
            insights = emptyList(),
            messages = emptyList(),
            activeTopicId = OPEN_INQUIRY_TOPIC_ID,
            inputText = ""
        )

        assertTrue(content is ChatUiContent.Error)
        assertEquals(DomainError.Local.NotFound, (content as ChatUiContent.Error).error)
    }
}
