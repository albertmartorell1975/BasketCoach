package com.martorell.albert.basketcoach.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun rememberBasketCoachAppState(
    navController: NavHostController = rememberNavController(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
): BasketCoachAppState {
    return remember(navController, snackbarHostState) {
        BasketCoachAppState(navController, snackbarHostState)
    }
}

@Stable
class BasketCoachAppState(
    val navController: NavHostController,
    val snackbarHostState: SnackbarHostState
) {
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    fun navigateUp() {
        navController.navigateUp()
    }
}
