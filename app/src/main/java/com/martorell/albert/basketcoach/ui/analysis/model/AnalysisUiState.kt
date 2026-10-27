package com.martorell.albert.basketcoach.ui.analysis.model

import com.martorell.albert.basketcoach.ui.dashboard.model.Phase1SummaryUiModel

/**
 * Root UI presentation state for the Tablet Landscape Analysis screen.
 *
 * Strictly adheres to Constraint #17 (zero default parameter values, zero nullable fields).
 *
 * @property summary Phase 1 competitive baseline summary (record, win percentage, team info).
 * @property selectedFilter Active segmented filter tab ([InsightFilterTab]).
 * @property totalCount Total number of tactical insights available.
 * @property strengthsCount Number of strength insights available.
 * @property improvementsCount Number of area of improvement insights available.
 * @property displayedInsights Filtered collection of insight cards ([InsightCardUiModel]) rendered in the grid.
 */
data class AnalysisUiState(
    val summary: Phase1SummaryUiModel,
    val selectedFilter: InsightFilterTab,
    val totalCount: Int,
    val strengthsCount: Int,
    val improvementsCount: Int,
    val displayedInsights: List<InsightCardUiModel>
)
