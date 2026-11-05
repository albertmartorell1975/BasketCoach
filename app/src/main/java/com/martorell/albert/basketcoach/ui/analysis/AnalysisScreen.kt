package com.martorell.albert.basketcoach.ui.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.ui.analysis.components.AnalysisErrorView
import com.martorell.albert.basketcoach.ui.analysis.components.AnalysisLoadingView
import com.martorell.albert.basketcoach.ui.analysis.data.AnalysisPreviewData
import com.martorell.albert.basketcoach.ui.analysis.mapper.toAnalysisUiText
import com.martorell.albert.basketcoach.ui.analysis.model.AnalysisUiContent
import com.martorell.albert.basketcoach.ui.analysis.model.AnalysisUiState
import com.martorell.albert.basketcoach.ui.analysis.model.InsightFilterTab
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import com.martorell.albert.basketcoach.ui.theme.BcDevicePreview
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaBackground

/**
 * Stateful entry point for the tactical analysis screen.
 *
 * Injects [AnalysisViewModel] via Hilt, observes [AnalysisUiState] via [collectAsState],
 * and dispatches initial loading via [LaunchedEffect] adhering to the Passive Initialization Mandate.
 * Hoists navigation events ([onNavigateToChat]) to the app navigation graph.
 *
 * @param onNavigateToChat Callback invoked when tapping 'Explorar al Xat' with the prompt payload.
 * @param modifier Optional [Modifier] for screen positioning.
 * @param viewModel Injected [AnalysisViewModel] managing state and operations.
 */
@Composable
fun AnalysisScreen(
    onNavigateToChat: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AnalysisViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAnalysis()
    }

    AnalysisScreen(
        uiState = uiState,
        onFilterSelected = viewModel::onFilterSelected,
        onExploreChat = onNavigateToChat,
        onRetry = { viewModel.loadAnalysis(forceRefresh = true) },
        modifier = modifier
    )
}

/**
 * Stateless container rendering the appropriate content state ([AnalysisUiContent]) for the analysis screen.
 *
 * Switches between [AnalysisLoadingView], [AnalysisErrorView], and [AnalysisContent] (success) based on [AnalysisUiState.content].
 * If [AnalysisUiState.isRefreshing] is active while displaying [AnalysisUiContent.Success], a subtle overlay indicator is rendered.
 *
 * @param uiState Current immutable UI state.
 * @param onFilterSelected Callback invoked when a segmented filter tab is clicked.
 * @param onExploreChat Callback invoked when the coach taps 'Explorar al Xat' on an improvement card.
 * @param onRetry Callback invoked when the retry button is pressed in the error state.
 * @param modifier Optional [Modifier] for screen layout.
 */
@Composable
fun AnalysisScreen(
    uiState: AnalysisUiState,
    onFilterSelected: (InsightFilterTab) -> Unit,
    onExploreChat: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PiaBackground),
        contentAlignment = Alignment.Center
    ) {
        when (val content = uiState.content) {
            is AnalysisUiContent.Loading -> {
                AnalysisLoadingView()
            }
            is AnalysisUiContent.Error -> {
                AnalysisErrorView(
                    message = content.error.toAnalysisUiText(),
                    onRetry = onRetry
                )
            }
            is AnalysisUiContent.Success -> {
                AnalysisContent(
                    content = content,
                    onFilterSelected = onFilterSelected,
                    onExploreChat = onExploreChat,
                    modifier = Modifier.fillMaxSize()
                )
                if (uiState.isRefreshing) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PiaNavy)
                    }
                }
            }
        }
    }
}

@BcThemePreview
@BcDevicePreview
@Composable
private fun AnalysisScreenSuccessPreview() {
    BasketCoachTheme {
        AnalysisScreen(
            uiState = AnalysisPreviewData.uiState,
            onFilterSelected = {},
            onExploreChat = {},
            onRetry = {}
        )
    }
}

@BcThemePreview
@Composable
private fun AnalysisScreenLoadingPreview() {
    BasketCoachTheme {
        AnalysisScreen(
            uiState = AnalysisUiState(
                content = AnalysisUiContent.Loading,
                isRefreshing = false
            ),
            onFilterSelected = {},
            onExploreChat = {},
            onRetry = {}
        )
    }
}

@BcThemePreview
@Composable
private fun AnalysisScreenErrorPreview() {
    BasketCoachTheme {
        AnalysisScreen(
            uiState = AnalysisUiState(
                content = AnalysisUiContent.Error(DomainError.Network.NoInternet),
                isRefreshing = false
            ),
            onFilterSelected = {},
            onExploreChat = {},
            onRetry = {}
        )
    }
}
