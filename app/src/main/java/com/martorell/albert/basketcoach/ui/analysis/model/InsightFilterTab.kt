package com.martorell.albert.basketcoach.ui.analysis.model

/**
 * Segmented filter tabs for categorizing tactical insights on the Analysis screen.
 */
enum class InsightFilterTab {
    /**
     * Displays all insights without filtering.
     */
    ALL,

    /**
     * Filters display to only tactical strengths ([com.martorell.albert.basketcoach.domain.model.InsightType.STRENGTH]).
     */
    STRENGTHS,

    /**
     * Filters display to only areas of improvement ([com.martorell.albert.basketcoach.domain.model.InsightType.IMPROVEMENT]).
     */
    IMPROVEMENTS
}
