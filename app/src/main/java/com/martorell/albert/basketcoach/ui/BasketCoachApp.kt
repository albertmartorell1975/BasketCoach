package com.martorell.albert.basketcoach.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.martorell.albert.basketcoach.ui.components.BasketCoachTopBar
import com.martorell.albert.basketcoach.ui.navigation.BasketCoachAppState
import com.martorell.albert.basketcoach.ui.navigation.BasketCoachNavRail
import com.martorell.albert.basketcoach.ui.navigation.Navigation
import com.martorell.albert.basketcoach.ui.navigation.rememberBasketCoachAppState
import com.martorell.albert.basketcoach.ui.theme.PiaBackground

@Composable
fun BasketCoachApp(
    appState: BasketCoachAppState = rememberBasketCoachAppState(),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(PiaBackground)
            .testTag("basketcoach_app_root")
    ) {
        BasketCoachNavRail(
            navOptions = BasketCoachAppState.NAV_OPTIONS,
            isRouteSelected = { appState.isRouteSelected(it) },
            onRouteSelected = { appState.onNavItemClick(it) }
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
                Navigation(appState = appState)
            }
        }
    }
}
