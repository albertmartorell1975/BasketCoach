package com.martorell.albert.basketcoach.ui.dashboard.model

import com.martorell.albert.basketcoach.domain.model.Drill
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Insight

data class DashboardUiState(
    val games: List<Game> = emptyList(),
    val selectedGame: Game? = null,
    val insights: List<Insight> = emptyList(),
    val drills: List<Drill> = emptyList(),
    val selectedTab: DashboardTab = DashboardTab.ALL,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
