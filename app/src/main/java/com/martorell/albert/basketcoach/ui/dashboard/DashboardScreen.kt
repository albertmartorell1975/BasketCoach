package com.martorell.albert.basketcoach.ui.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.martorell.albert.basketcoach.ui.dashboard.components.DashboardErrorView
import com.martorell.albert.basketcoach.ui.dashboard.components.DashboardLoadingView
import com.martorell.albert.basketcoach.ui.dashboard.data.Phase1PreviewData
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardUiContent
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardUiState
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import com.martorell.albert.basketcoach.ui.theme.BcDevicePreview
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview

/**
 * Stateful entry composable for the Phase 1 Basketball Dashboard.
 *
 * Injects [DashboardViewModel], triggers passive data loading via [LaunchedEffect], and
 * delegates presentation rendering to the stateless overload.
 *
 * @param modifier Layout modifier applied to the screen container.
 * @param viewModel Hilt-injected ViewModel managing dashboard state.
 */
@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDashboard()
    }

    DashboardScreen(
        uiState = uiState,
        modifier = modifier,
        onRetry = viewModel::loadDashboard
    )
}

/**
 * Stateless presentation composable for the Phase 1 Basketball Dashboard.
 *
 * Renders [DashboardLoadingView], [DashboardErrorView], or [DashboardContent] based on
 * the active [DashboardUiContent] phase.
 *
 * @param uiState Immutable UI state coordinating mutually exclusive content and refresh flags.
 * @param modifier Layout modifier applied to the screen container.
 * @param onRetry Callback triggered when the coach taps the retry button on error.
 */
@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {}
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val content = uiState.content) {
            is DashboardUiContent.Loading -> {
                DashboardLoadingView()
            }
            is DashboardUiContent.Error -> {
                DashboardErrorView(
                    errorMessage = content.message,
                    onRetry = onRetry
                )
            }
            is DashboardUiContent.Success -> {
                DashboardContent(
                    uiState = content,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@BcThemePreview
@BcDevicePreview
@Composable
private fun DashboardScreenPreview() {
    BasketCoachTheme {
        DashboardScreen(uiState = Phase1PreviewData.uiState)
    }
}

@BcThemePreview
@Composable
private fun DashboardScreenLoadingPreview() {
    BasketCoachTheme {
        DashboardScreen(
            uiState = DashboardUiState(
                content = DashboardUiContent.Loading,
                isRefreshing = false
            )
        )
    }
}

@BcThemePreview
@Composable
private fun DashboardScreenErrorPreview() {
    BasketCoachTheme {
        DashboardScreen(
            uiState = DashboardUiState(
                content = DashboardUiContent.Error("No s'han pogut carregar les dades de la 1a Fase."),
                isRefreshing = false
            )
        )
    }
}
