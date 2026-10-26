package com.martorell.albert.basketcoach.ui.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.martorell.albert.basketcoach.domain.model.InsightType
import com.martorell.albert.basketcoach.ui.analysis.components.AnalysisFilterSegmentedControl
import com.martorell.albert.basketcoach.ui.analysis.components.AnalysisHeroCard
import com.martorell.albert.basketcoach.ui.analysis.components.InsightCard
import com.martorell.albert.basketcoach.ui.analysis.model.AnalysisFilterTab
import com.martorell.albert.basketcoach.ui.analysis.model.AnalysisUiState
import com.martorell.albert.basketcoach.ui.dashboard.preview.DashboardPreviewData
import com.martorell.albert.basketcoach.ui.theme.PiaBackground
import com.martorell.albert.basketcoach.ui.theme.PiaDangerContainer
import com.martorell.albert.basketcoach.ui.theme.PiaDangerRed
import com.martorell.albert.basketcoach.ui.theme.PiaNavy

/**
 * Tactical Analysis screen for Phase 1 performance insights.
 *
 * Displays a 3-column responsive grid conforming strictly to the sketch:
 * - Baseline Hero Card with verified Phase 1 record (3V–11D, 21.4%).
 * - Segmented Control filtering between Tots, Fortaleses, and Àrees de Millora.
 * - 3-column cards with green verified borders for strengths and tactical orange borders with
 *   "Explorar al Xat" action buttons for improvement areas.
 *
 * @param onNavigateToChat Callback with prompt triggered when coach explores an insight in AI chat.
 * @param modifier Modifier applied to the outer layout.
 */
@Composable
fun AnalysisScreen(
    onNavigateToChat: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var uiState by remember {
        mutableStateOf(
            AnalysisUiState(
                insights = DashboardPreviewData.sampleInsights,
                selectedTab = AnalysisFilterTab.ALL
            )
        )
    }

    AnalysisContent(
        state = uiState,
        onTabSelected = { tab ->
            uiState = uiState.copy(selectedTab = tab)
        },
        onNavigateToChat = onNavigateToChat,
        onRefresh = {
            uiState = uiState.copy(
                insights = DashboardPreviewData.sampleInsights,
                errorMessage = null,
                isLoading = false
            )
        },
        modifier = modifier
    )
}

/**
 * Stateless presentation composable for the tactical analysis screen.
 */
@Composable
fun AnalysisContent(
    state: AnalysisUiState,
    onTabSelected: (AnalysisFilterTab) -> Unit,
    onNavigateToChat: (String?) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalCount = state.insights.size
    val strengthsCount = state.insights.count { it.type == InsightType.STRENGTH }
    val improvementsCount = state.insights.count { it.type == InsightType.IMPROVEMENT }

    val filteredInsights = when (state.selectedTab) {
        AnalysisFilterTab.ALL -> state.insights
        AnalysisFilterTab.STRENGTHS -> state.insights.filter { it.type == InsightType.STRENGTH }
        AnalysisFilterTab.IMPROVEMENTS -> state.insights.filter { it.type == InsightType.IMPROVEMENT }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PiaBackground)
            .testTag("AnalysisScreen")
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .testTag("Analysis_LoadingIndicator"),
                    color = PiaNavy
                )
            }

            state.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(PiaDangerContainer, RoundedCornerShape(12.dp))
                        .padding(24.dp)
                        .testTag("Analysis_ErrorBanner"),
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    // Top Hero Card
                    AnalysisHeroCard()

                    Spacer(modifier = Modifier.height(16.dp))

                    // Filter Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnalysisFilterSegmentedControl(
                            selectedTab = state.selectedTab,
                            totalCount = totalCount,
                            strengthsCount = strengthsCount,
                            improvementsCount = improvementsCount,
                            onTabSelected = onTabSelected
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3-Column Grid of Insight Cards
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .testTag("Analysis_InsightsGrid"),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            items = filteredInsights,
                            key = { it.id }
                        ) { insight ->
                            InsightCard(
                                insight = insight,
                                onNavigateToChat = onNavigateToChat
                            )
                        }
                    }
                }
            }
        }
    }
}
