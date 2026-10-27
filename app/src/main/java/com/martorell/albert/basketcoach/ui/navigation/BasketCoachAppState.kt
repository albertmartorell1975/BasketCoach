package com.martorell.albert.basketcoach.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.martorell.albert.basketcoach.R
import kotlinx.coroutines.CoroutineScope

@Stable
class BasketCoachAppState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
    val snackbarHostState: SnackbarHostState
) {
    companion object {
        val NAV_OPTIONS = listOf(
            TopLevelRoute(
                titleRes = R.string.nav_dashboard,
                route = DashboardRoute,
                icon = Icons.Filled.Dashboard
            ),
            TopLevelRoute(
                titleRes = R.string.nav_analysis,
                route = AnalysisRoute,
                icon = Icons.Filled.Insights
            ),
            TopLevelRoute(
                titleRes = R.string.nav_chat,
                route = ChatRoute(),
                icon = Icons.Filled.Forum
            )
        )
    }

    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    @Composable
    fun isRouteSelected(route: Any): Boolean =
        currentDestination?.hierarchy?.any { it.hasRoute(route::class) } == true

    fun onNavItemClick(route: Any) {
        navController.navigatePoppingUpToStartDestination(route)
    }

    fun navigateToDashboard() {
        navController.navigatePoppingUpToStartDestination(DashboardRoute)
    }

    fun navigateToAnalysis() {
        navController.navigatePoppingUpToStartDestination(AnalysisRoute)
    }

    fun navigateToChat(prompt: String = "") {
        navController.navigatePoppingUpToStartDestination(ChatRoute(initialPrompt = prompt))
    }
}

@Composable
fun rememberBasketCoachAppState(
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
): BasketCoachAppState {
    return remember(navController, coroutineScope, snackbarHostState) {
        BasketCoachAppState(
            navController = navController,
            coroutineScope = coroutineScope,
            snackbarHostState = snackbarHostState
        )
    }
}
