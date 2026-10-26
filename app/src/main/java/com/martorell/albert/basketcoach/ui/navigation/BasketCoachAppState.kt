package com.martorell.albert.basketcoach.ui.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope

@Stable
class BasketCoachAppState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
    val snackbarHostState: SnackbarHostState
) {
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    @Composable
    fun isRouteSelected(route: Any): Boolean {
        val destination = currentDestination ?: return route == DashboardRoute
        return destination.hasRoute(route::class)
    }

    @Composable
    fun isDestinationSelected(destination: NavRailDestination): Boolean {
        return isRouteSelected(destination.route)
    }

    fun navigateToDashboard() {
        navController.navigate(DashboardRoute) {
            popUpTo(DashboardRoute) { inclusive = true }
            launchSingleTop = true
        }
    }

    fun navigateToAnalysis() {
        navController.navigate(AnalysisRoute) {
            launchSingleTop = true
        }
    }

    fun navigateToChat() {
        navController.navigate(ChatRoute) {
            launchSingleTop = true
        }
    }

    fun navigateToRailDestination(destination: NavRailDestination) {
        when (destination) {
            NavRailDestination.DASHBOARD -> navigateToDashboard()
            NavRailDestination.ANALYSIS -> navigateToAnalysis()
            NavRailDestination.CHAT -> navigateToChat()
        }
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
