package com.martorell.albert.basketcoach.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.martorell.albert.basketcoach.ui.dashboard.components.DashboardHeroCard
import com.martorell.albert.basketcoach.ui.dashboard.components.KpiCardRow
import com.martorell.albert.basketcoach.ui.dashboard.components.Phase1EvolutionChart
import com.martorell.albert.basketcoach.ui.dashboard.components.Phase1MatchesTable
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardUiState
import com.martorell.albert.basketcoach.ui.theme.PiaBackground

@Composable
fun DashboardContent(
    uiState: DashboardUiState,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PiaBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 32.dp, vertical = 24.dp)
            .testTag("dashboard_content"),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        DashboardHeroCard(summary = uiState.summary)
        KpiCardRow(kpis = uiState.kpis)
        Phase1EvolutionChart(evolution = uiState.evolution)
        Phase1MatchesTable(matches = uiState.matches)
    }
}
