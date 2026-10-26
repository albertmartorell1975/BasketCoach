package com.martorell.albert.basketcoach.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.ui.components.BasketCoachTopBar
import com.martorell.albert.basketcoach.ui.dashboard.DashboardScreen
import com.martorell.albert.basketcoach.ui.navigation.BasketCoachNavRail
import com.martorell.albert.basketcoach.ui.navigation.NavDestination
import com.martorell.albert.basketcoach.ui.placeholder.PlaceholderScreen
import com.martorell.albert.basketcoach.ui.theme.PiaBackground

@Composable
fun BasketCoachApp(
    modifier: Modifier = Modifier
) {
    var selectedDestination by rememberSaveable { mutableStateOf(NavDestination.DASHBOARD) }

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(PiaBackground)
            .testTag("basketcoach_app_root")
    ) {
        BasketCoachNavRail(
            selectedDestination = selectedDestination,
            onDestinationSelected = { selectedDestination = it }
        )

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            BasketCoachTopBar()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (selectedDestination) {
                    NavDestination.DASHBOARD -> DashboardScreen()
                    NavDestination.ANALYSIS -> PlaceholderScreen(
                        title = stringResource(R.string.nav_analysis),
                        subtitle = stringResource(R.string.topbar_category)
                    )
                    NavDestination.CHAT -> PlaceholderScreen(
                        title = stringResource(R.string.nav_chat),
                        subtitle = stringResource(R.string.topbar_category)
                    )
                }
            }
        }
    }
}
