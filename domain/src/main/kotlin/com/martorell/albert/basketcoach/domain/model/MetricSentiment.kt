package com.martorell.albert.basketcoach.domain.model

enum class MetricSentiment {
    POSITIVE,
    NEGATIVE,
    NEUTRAL;

    companion object {
        fun fromString(value: String?): MetricSentiment {
            return entries.firstOrNull { it.name.equals(value?.trim(), ignoreCase = true) } ?: NEUTRAL
        }
    }
}
