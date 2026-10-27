package com.martorell.albert.basketcoach.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.ui.dashboard.DashboardScreen
import com.martorell.albert.basketcoach.ui.placeholder.PlaceholderScreen

/**
 * Primary navigation host composable defining top-level destination graphs and routes.
 *
 * Hosts destinations for [DashboardRoute], [AnalysisRoute], and [ChatRoute].
 *
 * @param appState Application state holder containing the [NavHostController].
 * @param modifier Layout modifier applied to the root [NavHost].
 */
@Composable
fun Navigation(
    appState: BasketCoachAppState,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = appState.navController,
        startDestination = DashboardRoute,
        modifier = modifier
    ) {
        composable<DashboardRoute> {
            DashboardScreen()
        }
        composable<AnalysisRoute> {
            PlaceholderScreen(
                title = stringResource(R.string.nav_analysis),
                subtitle = stringResource(R.string.topbar_category)
            )
        }
        composable<ChatRoute> {
            PlaceholderScreen(
                title = stringResource(R.string.nav_chat),
                subtitle = stringResource(R.string.topbar_category)
            )
        }
    }
}
