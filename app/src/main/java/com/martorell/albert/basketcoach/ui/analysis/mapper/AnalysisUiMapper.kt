package com.martorell.albert.basketcoach.ui.analysis.mapper

import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.InsightType
import com.martorell.albert.basketcoach.ui.analysis.model.AnalysisUiContent
import com.martorell.albert.basketcoach.ui.analysis.model.InsightCardUiModel
import com.martorell.albert.basketcoach.ui.analysis.model.InsightFilterTab
import com.martorell.albert.basketcoach.ui.dashboard.model.Phase1SummaryUiModel
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Presentation mapper responsible for converting domain models into UI state models for the Analysis screen.
 *
 * Computes the Phase 1 season summary ([Phase1SummaryUiModel]) with authentic win-loss metrics,
 * maps [Insight] domain models into presentation [InsightCardUiModel] instances (incorporating
 * localized category badge string resource IDs and chat explore capability flags), and manages
 * in-memory filtering by [InsightFilterTab].
 */
class AnalysisUiMapper @Inject constructor() {

    /**
     * Maps a list of Phase 1 [Game] domain models and AI-generated [Insight] items into [AnalysisUiContent].
     *
     * If [games] is empty, returns [AnalysisUiContent.Error] with [DomainError.Local.NotFound].
     * Otherwise, calculates competitive record statistics, formats insight cards, computes category counts,
     * and filters cards according to [filter].
     *
     * @param games The list of historical Phase 1 fixtures.
     * @param insights The list of AI-generated tactical insights.
     * @param filter The currently active [InsightFilterTab], defaulting to [InsightFilterTab.ALL].
     * @return [AnalysisUiContent.Success] with computed summary and insight cards, or [AnalysisUiContent.Error] if [games] is empty.
     */
    fun toContent(
        games: List<Game>,
        insights: List<Insight>,
        filter: InsightFilterTab = InsightFilterTab.ALL
    ): AnalysisUiContent {
        if (games.isEmpty()) {
            return AnalysisUiContent.Error(error = DomainError.Local.NotFound)
        }

        val totalGames = games.size
        val wins = games.count { it.isWin }
        val losses = totalGames - wins
        val winPercentage = roundToOneDecimal((wins.toDouble() / totalGames) * 100)

        val summary = Phase1SummaryUiModel(
            wins = wins,
            losses = losses,
            winPercentage = winPercentage,
            totalGames = totalGames,
            categoryRes = R.string.topbar_category,
            teamNameRes = R.string.hero_club_name
        )

        val allInsightCards = insights.map { insight ->
            InsightCardUiModel(
                id = insight.id,
                type = insight.type,
                categoryBadgeRes = if (insight.type == InsightType.STRENGTH) {
                    R.string.analysis_badge_strength
                } else {
                    R.string.analysis_badge_improvement
                },
                contextTag = insight.contextTag,
                title = insight.title,
                description = insight.description,
                primaryMetricLabel = insight.metric.primaryLabel,
                primaryMetricValue = insight.metric.primaryValue,
                primaryMetricSubValue = insight.metric.primarySubValue,
                secondaryMetricLabel = insight.metric.secondaryLabel,
                secondaryMetricValue = insight.metric.secondaryValue,
                secondaryMetricTag = insight.metric.secondaryTag,
                metricSentiment = insight.metric.sentiment,
                footerNote = insight.footerNote,
                canExploreChat = insight.type == InsightType.IMPROVEMENT && insight.exploreChatPrompt.isNotBlank(),
                exploreChatPrompt = insight.exploreChatPrompt
            )
        }

        val totalCount = allInsightCards.size
        val strengthsCount = allInsightCards.count { it.type == InsightType.STRENGTH }
        val improvementsCount = allInsightCards.count { it.type == InsightType.IMPROVEMENT }
        val displayedInsights = filterInsights(allInsightCards, filter)

        return AnalysisUiContent.Success(
            summary = summary,
            selectedFilter = filter,
            totalCount = totalCount,
            strengthsCount = strengthsCount,
            improvementsCount = improvementsCount,
            allInsights = allInsightCards,
            displayedInsights = displayedInsights
        )
    }

    /**
     * Filters a list of [InsightCardUiModel] based on the selected [InsightFilterTab].
     *
     * @param allInsights The full list of mapped insight cards.
     * @param filter The active tab filter.
     * @return Filtered list of [InsightCardUiModel] matching the given [filter].
     */
    fun filterInsights(
        allInsights: List<InsightCardUiModel>,
        filter: InsightFilterTab
    ): List<InsightCardUiModel> = when (filter) {
        InsightFilterTab.ALL -> allInsights
        InsightFilterTab.STRENGTHS -> allInsights.filter { it.type == InsightType.STRENGTH }
        InsightFilterTab.IMPROVEMENTS -> allInsights.filter { it.type == InsightType.IMPROVEMENT }
    }

    private fun roundToOneDecimal(value: Double): Double {
        return (value * 10.0).roundToInt() / 10.0
    }
}
