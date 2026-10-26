package com.martorell.albert.basketcoach.ui.dashboard

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.martorell.albert.basketcoach.ui.dashboard.data.Phase1PreviewData
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardUiState

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    uiState: DashboardUiState = Phase1PreviewData.uiState
) {
    DashboardContent(
        uiState = uiState,
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen")
    )
}
