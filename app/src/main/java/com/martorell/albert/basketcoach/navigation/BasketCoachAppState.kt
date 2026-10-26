package com.martorell.albert.basketcoach.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

/**
 * Creates and remembers an instance of [BasketCoachAppState] across recompositions.
 *
 * @param navController The root [NavHostController] managing application navigation.
 * @param snackbarHostState The [SnackbarHostState] used to present app-level snackbars.
 * @return A remembered [BasketCoachAppState] instance.
 */
@Composable
fun rememberBasketCoachAppState(
    navController: NavHostController = rememberNavController(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
): BasketCoachAppState {
    return remember(navController, snackbarHostState) {
        BasketCoachAppState(navController, snackbarHostState)
    }
}

/**
 * Hoisted state holder for application-level navigation and UI chrome (NavRail, TopBar, snackbars).
 *
 * @property navController The underlying Jetpack Compose [NavHostController].
 * @property snackbarHostState The coordinator for displaying snackbar feedback.
 */
@Stable
class BasketCoachAppState(
    val navController: NavHostController,
    val snackbarHostState: SnackbarHostState
) {
    /**
     * Resolves the current active [Destination] observed from the back stack.
     */
    val currentDestination: Destination
        @Composable get() {
            val destination = navController.currentBackStackEntryAsState().value?.destination
            return when {
                destination?.hasRoute(Destination.Analysis::class) == true -> Destination.Analysis
                destination?.hasRoute(Destination.Chat::class) == true -> Destination.Chat()
                else -> Destination.Dashboard
            }
        }

    /**
     * Navigates to [Destination.Dashboard] clearing non-essential back stack entries.
     */
    fun navigateToDashboard() {
        navController.navigate(Destination.Dashboard) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    /**
     * Navigates to [Destination.Analysis] clearing non-essential back stack entries.
     */
    fun navigateToAnalysis() {
        navController.navigate(Destination.Analysis) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    /**
     * Navigates to [Destination.Chat] with an optional pre-populated prompt.
     */
    fun navigateToChat(prompt: String? = null) {
        navController.navigate(Destination.Chat(initialPrompt = prompt)) {
            launchSingleTop = true
        }
    }

    /**
     * Attempts to navigate up in the navigation hierarchy.
     */
    fun navigateUp() {
        navController.navigateUp()
    }
}
