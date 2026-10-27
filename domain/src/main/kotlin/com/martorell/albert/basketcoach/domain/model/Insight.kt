package com.martorell.albert.basketcoach.domain.model

/**
 * Tactical insight derived from Phase 1 competition performance analysis.
 *
 * Encapsulates coach-facing tactical conclusions (both strengths and areas of improvement)
 * grounded in empirical match box scores and competition evidence.
 *
 * @property id Unique identifier of the insight.
 * @property title Concise, bold tactical headline summarizing the finding.
 * @property description Detailed tactical analysis describing the game pattern and its impact.
 * @property category General category classification for the insight ([InsightCategory]).
 * @property relatedGameRounds Specific competition rounds (1..14) where this pattern was observed.
 * @property type Classification of whether this insight is a [InsightType.STRENGTH] or [InsightType.IMPROVEMENT].
 * @property contextTag Contextual game phase or tactical domain tag (e.g., "Fase Ofensiva · Pintura").
 * @property metric Empirical statistical container ([InsightMetric]) providing quantitative proof.
 * @property priority Tactical urgency indicator or priority level (e.g., "Crítica #1").
 * @property footerNote Explanatory note or evidence summary displayed in the card footer.
 * @property exploreChatPrompt Pre-grounded prompt dispatched to the conversational assistant when exploring this insight.
 */
data class Insight(
    val id: String,
    val title: String,
    val description: String,
    val category: InsightCategory,
    val relatedGameRounds: List<Int>,
    val type: InsightType,
    val contextTag: String,
    val metric: InsightMetric,
    val priority: String,
    val footerNote: String,
    val exploreChatPrompt: String
)
