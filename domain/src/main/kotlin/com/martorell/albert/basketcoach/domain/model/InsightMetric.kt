package com.martorell.albert.basketcoach.domain.model

/**
 * Quantitative empirical metric supporting a tactical [Insight].
 *
 * Houses both primary performance metrics and benchmark comparisons derived from competition box scores.
 *
 * @property primaryLabel Label describing the primary statistical metric (e.g., "Percentatge T2 Zona").
 * @property primaryValue Primary metric value formatted for presentation (e.g., "58.4%").
 * @property primarySubValue Supplementary context or differential for the primary metric (e.g., "+8.2% sobre mitjana lliga").
 * @property secondaryLabel Label describing the secondary or benchmark metric (e.g., "Punts / Pos.").
 * @property secondaryValue Secondary metric value formatted for presentation (e.g., "1.24 pts/pos").
 * @property secondaryTag Technical dataset tag or competition context (e.g., "dataset:paint_eff").
 * @property sentiment Evaluation sentiment of the metric ([MetricSentiment]), driving UI visual indicators.
 */
data class InsightMetric(
    val primaryLabel: String,
    val primaryValue: String,
    val primarySubValue: String,
    val secondaryLabel: String,
    val secondaryValue: String,
    val secondaryTag: String,
    val sentiment: MetricSentiment
)
