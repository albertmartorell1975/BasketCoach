package com.martorell.albert.basketcoach.domain.model

/**
 * Sentiment evaluation associated with an [InsightMetric], dictating the semantic color coding in presentation.
 */
enum class MetricSentiment {
    /**
     * Favorable performance metric indicated with success styling (green).
     */
    POSITIVE,

    /**
     * Adverse performance metric or tactical leak indicated with alert styling (danger red or tactical orange).
     */
    NEGATIVE,

    /**
     * Informational or benchmark metric without positive or negative skew (muted).
     */
    NEUTRAL;

    companion object {
        /**
         * Safely parses a string value into a [MetricSentiment], supporting case-insensitive matching.
         *
         * @param value String representation of the metric sentiment.
         * @return Matching [MetricSentiment] instance.
         * @throws IllegalArgumentException if the value cannot be recognized.
         */
        fun fromString(value: String): MetricSentiment {
            return when (value.uppercase()) {
                "POSITIVE" -> POSITIVE
                "NEGATIVE" -> NEGATIVE
                "NEUTRAL" -> NEUTRAL
                else -> throw IllegalArgumentException("Unknown MetricSentiment: '$value'")
            }
        }
    }
}
