package com.martorell.albert.basketcoach.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network Data Transfer Object representing the empirical metric payload of an insight.
 *
 * Adheres strictly to Constraint #14 (all fields mandatory and non-nullable with zero defaults).
 *
 * @property primaryLabel Label for the primary statistical metric.
 * @property primaryValue Primary metric value as a formatted string.
 * @property primarySubValue Supplementary context or percentage delta for the primary metric.
 * @property secondaryLabel Label for the secondary benchmark metric.
 * @property secondaryValue Benchmark value as a formatted string.
 * @property secondaryTag Technical dataset tag reference.
 * @property sentiment Sentiment evaluation string corresponding to a [MetricSentiment] entry.
 */
@Serializable
data class InsightMetricDto(
    @SerialName("primaryLabel")
    val primaryLabel: String,
    @SerialName("primaryValue")
    val primaryValue: String,
    @SerialName("primarySubValue")
    val primarySubValue: String,
    @SerialName("secondaryLabel")
    val secondaryLabel: String,
    @SerialName("secondaryValue")
    val secondaryValue: String,
    @SerialName("secondaryTag")
    val secondaryTag: String,
    @SerialName("sentiment")
    val sentiment: String
)
