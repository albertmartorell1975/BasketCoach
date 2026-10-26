package com.martorell.albert.basketcoach.domain.model

/**
 * Structured metric measurement backing a tactical insight.
 *
 * @property label Short metric label (e.g. "Rebot Defensiu", "Pèrdues per partit").
 * @property value Primary formatted statistical value (e.g. "28.3 reb/p", "17.4 TO").
 * @property unit Optional unit designation.
 * @property deltaText Optional formatted delta string.
 * @property sentiment Sentiment classification ([MetricSentiment.POSITIVE], [MetricSentiment.NEGATIVE], [MetricSentiment.NEUTRAL]).
 * @property evidenceLabel Contextual source label (e.g. "Jornades 1-14").
 * @property evidenceValue Numerical evidence supporting the metric.
 * @property datasetKey Key mapping into the underlying statistical dataset.
 * @property comparison Comparison benchmark string (e.g. "vs 23.5 rival", "lliga 32.8%").
 * @property delta Net differential indicator (e.g. "+4.8 diferencial", "-6.4% diferencial").
 */
data class InsightMetric(
    val label: String,
    val value: String,
    val unit: String = "",
    val deltaText: String? = null,
    val sentiment: MetricSentiment = MetricSentiment.NEUTRAL,
    val evidenceLabel: String? = null,
    val evidenceValue: String? = null,
    val datasetKey: String? = null,
    val comparison: String? = null,
    val delta: String? = null
)
