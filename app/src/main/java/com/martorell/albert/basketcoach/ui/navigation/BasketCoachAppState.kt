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

/**
 * Hoisted state holder governing top-level navigation, active route resolution, and coroutine
 * coordination across the BasketCoach tablet application shell.
 *
 * @property navController Primary navigation controller governing screen transitions.
 * @property coroutineScope Coroutine scope tied to the application shell lifecycle.
 * @property snackbarHostState State holder for displaying transient snackbars across screens.
 */
@Stable
class BasketCoachAppState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
    val snackbarHostState: SnackbarHostState
) {
    companion object {
        /**
         * Authoritative list of top-level navigation destinations shown in the application navigation rail.
         */
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
                route = ChatRoute,
                icon = Icons.Filled.Forum
            )
        )
    }

    /**
     * Currently active [NavDestination] in the navigation backstack hierarchy, observed reactively.
     */
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    /**
     * Checks whether the given [route] is currently selected or present in the current destination hierarchy.
     *
     * @param route Target destination object or class to evaluate.
     * @return `true` if the route matches the active destination hierarchy; `false` otherwise.
     */
    @Composable
    fun isRouteSelected(route: Any): Boolean =
        currentDestination?.hierarchy?.any { it.hasRoute(route::class) } == true

    /**
     * Handles navigation rail item selection by popping up to start destination with state restoration.
     *
     * @param route Destination route to navigate to.
     */
    fun onNavItemClick(route: Any) {
        navController.navigatePoppingUpToStartDestination(route)
    }

    /**
     * Navigates directly to the [DashboardRoute] top-level destination.
     */
    fun navigateToDashboard() {
        navController.navigatePoppingUpToStartDestination(DashboardRoute)
    }

    /**
     * Navigates directly to the [AnalysisRoute] top-level destination.
     */
    fun navigateToAnalysis() {
        navController.navigatePoppingUpToStartDestination(AnalysisRoute)
    }

    /**
     * Navigates directly to the [ChatRoute] top-level destination.
     */
    fun navigateToChat() {
        navController.navigatePoppingUpToStartDestination(ChatRoute)
    }
}

/**
 * Creates and remembers an instance of [BasketCoachAppState] scoped to the current composable lifecycle.
 *
 * @param navController Navigation host controller instance.
 * @param coroutineScope Coroutine scope for launching UI-driven asynchronous tasks.
 * @param snackbarHostState Host state for showing snackbar notifications.
 * @return Remembered [BasketCoachAppState] instance.
 */
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
