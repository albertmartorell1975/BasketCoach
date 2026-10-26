package com.martorell.albert.basketcoach.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
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
 * Hoisted state holder for application-level navigation and UI chrome (snackbars).
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
     * Resolves the current active [NavDestination] observed from the back stack.
     */
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    /**
     * Attempts to navigate up in the navigation hierarchy.
     */
    fun navigateUp() {
        navController.navigateUp()
    }
}
