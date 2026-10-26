package com.martorell.albert.basketcoach.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
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
                            text = "BasketCoach",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "CE Pia Sabadell Junior Blau • Phase 1 Analysis",
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
                            text = "Ask Coach AI",
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
                            text = "Analyzing Phase 1 Performance...",
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
                            text = "Error Loading Dashboard",
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
                            Text("Retry")
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

@Preview(name = "Tablet Landscape", device = "spec:width=1280dp,height=800dp,dpi=240,orientation=landscape", showBackground = true)
@Composable
fun DashboardScreenPreview() {
    BasketCoachTheme {
        DashboardScreen(
            state = DashboardPreviewData.sampleUiState,
            onGameSelected = {},
            onTabSelected = {},
            onNavigateToChat = {},
            onRefresh = {}
        )
    }
}
