package com.martorell.albert.basketcoach.ui.analysis.model

import com.martorell.albert.basketcoach.domain.model.Insight

/**
 * UI state for the tactical analysis screen.
 *
 * @property insights Full list of synthesized AI tactical insights.
 * @property selectedTab Current active filter category.
 * @property isLoading Whether analysis data is being computed/fetched.
 * @property errorMessage Error description if loading failed.
 */
data class AnalysisUiState(
    val insights: List<Insight> = emptyList(),
    val selectedTab: AnalysisFilterTab = AnalysisFilterTab.ALL,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
