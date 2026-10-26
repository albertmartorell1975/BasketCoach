package com.martorell.albert.basketcoach.ui.dashboard.model

import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Insight

/**
 * UI state representing the presentation model for the Phase 1 dashboard overview screen.
 *
 * @property games Complete list of 14 Phase 1 matches.
 * @property insights Synthesized AI tactical insights extracted from Phase 1 performance.
 * @property selectedTab Legacy filter tab state preserved for backwards compatibility.
 * @property isLoading Whether dashboard data is actively loading.
 * @property errorMessage Error message if data loading failed, or null if healthy.
 */
data class DashboardUiState(
    val games: List<Game> = emptyList(),
    val insights: List<Insight> = emptyList(),
    val selectedTab: DashboardTab = DashboardTab.ALL,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
