package com.martorell.albert.basketcoach.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.ui.dashboard.components.GameListPane
import com.martorell.albert.basketcoach.ui.dashboard.components.InsightsDrillsPane
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardTab
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardUiState
import com.martorell.albert.basketcoach.ui.dashboard.preview.DashboardPreviewData
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import com.martorell.albert.basketcoach.ui.theme.LossRed
import com.martorell.albert.basketcoach.ui.theme.PiaAmber
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaPrimaryBlue
import com.martorell.albert.basketcoach.ui.theme.TabletLandscapePreview

/**
 * Stateful dashboard entry composable for the BasketCoach tablet interface.
 *
 * Manages UI interactions and local screen state during UI-first feature phase,
 * delegating pure presentation and layout rendering to [DashboardContent].
 *
 * @param onNavigateToChat Callback triggered when the coach clicks "Ask Coach AI".
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
        onGameSelected = { game: Game ->
            uiState = uiState.copy(selectedGame = game)
        },
        onTabSelected = { tab: DashboardTab ->
            uiState = uiState.copy(selectedTab = tab)
        },
        onNavigateToChat = onNavigateToChat,
        onRefresh = {
            uiState = DashboardPreviewData.sampleUiState
        },
        modifier = modifier
    )
}

/**
 * Stateless dashboard presentation composable displaying Phase 1 match analysis in a tablet landscape layout.
 *
 * Renders a responsive two-pane interface consisting of:
 * - Left pane (~42% width): [GameListPane] showing team record, selected game box score, and fixture cards.
 * - Right pane (~58% width): [InsightsDrillsPane] featuring tabbed AI tactical insights and practice drills.
 *
 * Also handles centralized loading indicator and error banner states.
 *
 * @param state Current [DashboardUiState] to render.
 * @param onGameSelected Callback when a match fixture is selected.
 * @param onTabSelected Callback when switching between All, Insights, and Drills tabs.
 * @param onNavigateToChat Callback when the user requests navigation to Coach AI.
 * @param onRefresh Callback when retrying after an error.
 * @param modifier Modifier applied to the root [Scaffold].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    state: DashboardUiState,
    onGameSelected: (Game) -> Unit,
    onTabSelected: (DashboardTab) -> Unit,
    onNavigateToChat: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("DashboardScreen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.dashboard_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = stringResource(R.string.dashboard_subtitle),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = onNavigateToChat,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PiaAmber,
                            contentColor = PiaNavy
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .testTag("AskCoachAiButton")
                    ) {
                        Text(
                            text = stringResource(R.string.ask_coach_ai),
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PiaPrimaryBlue
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                state.isLoading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("DashboardLoadingIndicator"),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = PiaPrimaryBlue)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.loading_dashboard_analysis),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                state.errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("DashboardErrorBanner")
                            .padding(32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.error_loading_dashboard),
                            style = MaterialTheme.typography.titleMedium,
                            color = LossRed,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onRefresh,
                            modifier = Modifier.testTag("DashboardRetryButton")
                        ) {
                            Text(text = stringResource(R.string.retry))
                        }
                    }
                }
                else -> {
                    Row(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        GameListPane(
                            games = state.games,
                            selectedGame = state.selectedGame,
                            onGameSelected = onGameSelected,
                            modifier = Modifier
                                .weight(0.42f)
                                .fillMaxHeight()
                        )

                        VerticalDivider(
                            modifier = Modifier.fillMaxHeight(),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        )

                        InsightsDrillsPane(
                            insights = state.insights,
                            drills = state.drills,
                            selectedTab = state.selectedTab,
                            onTabSelected = onTabSelected,
                            modifier = Modifier
                                .weight(0.58f)
                                .fillMaxHeight()
                        )
                    }
                }
            }
        }
    }
}

@TabletLandscapePreview
@Composable
private fun DashboardContentSuccessPreview() {
    BasketCoachTheme {
        DashboardContent(
            state = DashboardPreviewData.sampleUiState,
            onGameSelected = {},
            onTabSelected = {},
            onNavigateToChat = {},
            onRefresh = {}
        )
    }
}

@TabletLandscapePreview
@Composable
private fun DashboardContentLoadingPreview() {
    BasketCoachTheme {
        DashboardContent(
            state = DashboardUiState(isLoading = true),
            onGameSelected = {},
            onTabSelected = {},
            onNavigateToChat = {},
            onRefresh = {}
        )
    }
}

@TabletLandscapePreview
@Composable
private fun DashboardContentErrorPreview() {
    BasketCoachTheme {
        DashboardContent(
            state = DashboardUiState(errorMessage = "Failed to load Phase 1 dataset"),
            onGameSelected = {},
            onTabSelected = {},
            onNavigateToChat = {},
            onRefresh = {}
        )
    }
}
