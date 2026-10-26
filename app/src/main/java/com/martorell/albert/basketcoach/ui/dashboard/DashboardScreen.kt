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
