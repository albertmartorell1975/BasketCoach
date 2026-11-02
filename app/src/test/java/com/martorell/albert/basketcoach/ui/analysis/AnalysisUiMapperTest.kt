package com.martorell.albert.basketcoach.ui.analysis

import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.data.repository.GameRepositoryImpl
import com.martorell.albert.basketcoach.data.source.local.GameLocalDataSourceImpl
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.InsightCategory
import com.martorell.albert.basketcoach.domain.model.InsightMetric
import com.martorell.albert.basketcoach.domain.model.InsightType
import com.martorell.albert.basketcoach.domain.model.MetricSentiment
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.ui.analysis.mapper.AnalysisUiMapper
import com.martorell.albert.basketcoach.ui.analysis.model.AnalysisUiContent
import com.martorell.albert.basketcoach.ui.analysis.model.InsightFilterTab
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AnalysisUiMapperTest {

    private val mapper = AnalysisUiMapper()

    @Test
    fun should_MapGamesAndInsightsToSuccess_and_FilterByTab_when_ValidDataProvided() = runBlocking {
        val repo = GameRepositoryImpl(GameLocalDataSourceImpl())
        val gamesResult = repo.getPhase1Games()
        assertTrue(gamesResult is Result.Success)
        val games = (gamesResult as Result.Success).data

        val sampleInsights = listOf(
            Insight(
                id = "strength_1",
                title = "Eficiència Interior",
                description = "Bona selecció de tir a la pintura",
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
                description = "Pèrdues i contraatacs rivals",
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
                exploreChatPrompt = "Com podem millorar el balanç?"
            )
        )

        val content = mapper.toContent(games, sampleInsights, InsightFilterTab.ALL)
        assertTrue(content is AnalysisUiContent.Success)
        val success = content as AnalysisUiContent.Success

        assertEquals(3, success.summary.wins)
        assertEquals(11, success.summary.losses)
        assertEquals(21.4, success.summary.winPercentage, 0.01)
        assertEquals(14, success.summary.totalGames)

        assertEquals(2, success.totalCount)
        assertEquals(1, success.strengthsCount)
        assertEquals(1, success.improvementsCount)
        assertEquals(2, success.displayedInsights.size)

        val strengthCard = success.allInsights.first { it.id == "strength_1" }
        assertEquals(R.string.analysis_badge_strength, strengthCard.categoryBadgeRes)
        assertFalse(strengthCard.canExploreChat)

        val improvementCard = success.allInsights.first { it.id == "improvement_1" }
        assertEquals(R.string.analysis_badge_improvement, improvementCard.categoryBadgeRes)
        assertTrue(improvementCard.canExploreChat)

        val onlyStrengths = mapper.filterInsights(success.allInsights, InsightFilterTab.STRENGTHS)
        assertEquals(1, onlyStrengths.size)
        assertEquals(InsightType.STRENGTH, onlyStrengths.first().type)

        val onlyImprovements = mapper.filterInsights(success.allInsights, InsightFilterTab.IMPROVEMENTS)
        assertEquals(1, onlyImprovements.size)
        assertEquals(InsightType.IMPROVEMENT, onlyImprovements.first().type)
    }

    @Test
    fun should_ReturnErrorNotFound_when_GamesListIsEmpty() {
        val result = mapper.toContent(games = emptyList(), insights = emptyList())
        assertTrue(result is AnalysisUiContent.Error)
        assertEquals(DomainError.Local.NotFound, (result as AnalysisUiContent.Error).error)
    }
}
