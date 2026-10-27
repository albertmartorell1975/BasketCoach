package com.martorell.albert.basketcoach.domain.model

/**
 * Tactical classification of an [Insight], determining its visual hierarchy and interactive behavior.
 */
enum class InsightType {
    /**
     * Proven tactical advantage or positive performance factor identified during Phase 1.
     */
    STRENGTH,

    /**
     * Tactical vulnerability or corrective priority to address for Phase 2 preparation.
     */
    IMPROVEMENT;

    companion object {
        /**
         * Safely parses a string value into an [InsightType], supporting case-insensitive matching.
         *
         * @param value String representation of the insight type.
         * @return Matching [InsightType] instance.
         * @throws IllegalArgumentException if the value cannot be recognized.
         */
        fun fromString(value: String): InsightType {
            return when (value.uppercase()) {
                "STRENGTH" -> STRENGTH
                "IMPROVEMENT" -> IMPROVEMENT
                else -> throw IllegalArgumentException("Unknown InsightType: '$value'")
            }
        }
    }
}
