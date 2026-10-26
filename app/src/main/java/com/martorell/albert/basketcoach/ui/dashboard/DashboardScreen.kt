package com.martorell.albert.basketcoach.ui.dashboard

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.martorell.albert.basketcoach.ui.dashboard.data.Phase1PreviewData
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardUiState
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import com.martorell.albert.basketcoach.ui.theme.BcDevicePreview
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview

/**
 * Entry composable for the Phase 1 Basketball Dashboard.
 *
 * Provides default static preview state [Phase1PreviewData.uiState] and forwards it to
 * [DashboardContent] for layout rendering.
 *
 * @param modifier Layout modifier applied to the screen container.
 * @param uiState State object containing Phase 1 summary, KPIs, evolution chart data, and match table rows.
 */
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

@BcThemePreview
@BcDevicePreview
@Composable
private fun DashboardScreenPreview() {
    BasketCoachTheme {
        DashboardScreen()
    }
}
