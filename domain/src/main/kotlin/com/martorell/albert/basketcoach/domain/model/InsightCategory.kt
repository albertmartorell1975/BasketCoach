package com.martorell.albert.basketcoach.domain.model

/**
 * Categorizes analytical insights discovered from basketball team performance.
 */
enum class InsightCategory {
    /** Offensive execution, shooting efficiency, and turnover rate insights. */
    OFFENSE,

    /** Defensive coverage, rebounding security, and opponent scoring insights. */
    DEFENSE,

    /** Team chemistry, lineup performance, and ball movement synergy. */
    SYNERGY,

    /** Overall tactical observations, game management, and broad trend analysis. */
    GENERAL;

    companion object {
        /**
         * Safely parses a string value into an [InsightCategory], defaulting to [GENERAL] if unrecognized or null.
         */
        fun fromString(value: String?): InsightCategory {
            return entries.firstOrNull { it.name.equals(value?.trim(), ignoreCase = true) } ?: GENERAL
        }
    }
}
