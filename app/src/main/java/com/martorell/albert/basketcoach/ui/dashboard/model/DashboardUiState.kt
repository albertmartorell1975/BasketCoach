package com.martorell.albert.basketcoach.ui.dashboard.model

import com.martorell.albert.basketcoach.domain.model.Drill
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Insight

/**
 * UI state representing the complete presentation model for the coach dashboard screen.
 *
 * @property games Complete list of Phase 1 matches.
 * @property selectedGame Currently selected game for detailed box score display, or null if none.
 * @property insights Synthesized AI tactical insights extracted from Phase 1 performance.
 * @property drills Curated practice drills recommended for Phase 2 preparation.
 * @property selectedTab Currently active filter tab in the right pane ([DashboardTab.ALL], [DashboardTab.INSIGHTS], or [DashboardTab.DRILLS]).
 * @property isLoading Whether dashboard data is actively loading.
 * @property errorMessage Error message if data loading failed, or null if healthy.
 */
data class DashboardUiState(
    val games: List<Game> = emptyList(),
    val selectedGame: Game? = null,
    val insights: List<Insight> = emptyList(),
    val drills: List<Drill> = emptyList(),
    val selectedTab: DashboardTab = DashboardTab.ALL,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
