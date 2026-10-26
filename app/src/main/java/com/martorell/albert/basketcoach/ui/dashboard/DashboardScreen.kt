package com.martorell.albert.basketcoach.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.ui.dashboard.components.DashboardHeroCard
import com.martorell.albert.basketcoach.ui.dashboard.components.KpiCardRow
import com.martorell.albert.basketcoach.ui.dashboard.components.Phase1EvolutionChart
import com.martorell.albert.basketcoach.ui.dashboard.components.Phase1MatchesTable
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardUiState
import com.martorell.albert.basketcoach.ui.dashboard.preview.DashboardPreviewData
import com.martorell.albert.basketcoach.ui.theme.PiaBackground
import com.martorell.albert.basketcoach.ui.theme.PiaDangerContainer
import com.martorell.albert.basketcoach.ui.theme.PiaDangerRed
import com.martorell.albert.basketcoach.ui.theme.PiaNavy

/**
 * Stateful Dashboard screen entry composable for the BasketCoach tablet interface.
 *
 * Provides the consolidated Phase 1 overview strictly adhering to the design sketch:
 * 1. Competitive balance hero card (3V–11D, 21.4%).
 * 2. 4-column KPI cards row (Scoring, Rebounds, Shooting Efficiency, Game Control).
 * 3. 14-round scoring evolution bar chart highlighting wins in green.
 * 4. Consolidated 16-column matches table with all 14 rounds.
 *
 * @param onNavigateToChat Callback when coach requests Socratic AI chat.
 * @param modifier Modifier applied to the outer layout.
 */
@Composable
fun DashboardScreen(
    onNavigateToChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    var uiState by remember { mutableStateOf(DashboardPreviewData.sampleUiState) }

    DashboardContent(
        state = uiState,
        onNavigateToChat = onNavigateToChat,
        onRefresh = {
            uiState = DashboardPreviewData.sampleUiState
        },
        modifier = modifier
    )
}

/**
 * Stateless dashboard presentation composable.
 */
@Composable
fun DashboardContent(
    state: DashboardUiState,
    onNavigateToChat: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PiaBackground)
            .testTag("DashboardScreen")
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .testTag("Dashboard_LoadingIndicator"),
                    color = PiaNavy
                )
            }

            state.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(PiaDangerContainer, RoundedCornerShape(12.dp))
                        .padding(24.dp)
                        .testTag("Dashboard_ErrorBanner"),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.errorMessage,
                        color = PiaDangerRed,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onRefresh,
                        colors = ButtonDefaults.buttonColors(containerColor = PiaNavy)
                    ) {
                        Text(text = stringResource(R.string.retry))
                    }
                }
            }

            else -> {
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Hero Card
                    DashboardHeroCard()

                    // 2. Row of 4 KPI Cards
                    KpiCardRow()

                    // 3. 14-Round Scoring Evolution Chart
                    Phase1EvolutionChart(games = state.games)

                    // 4. Consolidated 16-Column 14-Match Table
                    Phase1MatchesTable(games = state.games)
                }
            }
        }
    }
}
