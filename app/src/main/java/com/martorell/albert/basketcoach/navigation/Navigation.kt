package com.martorell.albert.basketcoach.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.martorell.albert.basketcoach.ui.dashboard.DashboardScreen

/**
 * Root navigation graph composable for the BasketCoach application.
 *
 * Configures the primary [NavHost] with [Destination.Dashboard] as the start destination
 * and wires up the global [SnackbarHost] anchored to the bottom.
 *
 * @param appState The root application state holder containing the navigation controller.
 */
@Composable
fun Navigation(
    appState: BasketCoachAppState = rememberBasketCoachAppState()
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            navController = appState.navController,
            startDestination = Destination.Dashboard
        ) {
            composable<Destination.Dashboard> {
                DashboardScreen(
                    onNavigateToChat = {
                        appState.navController.navigate(Destination.Chat)
                    }
                )
            }

            composable<Destination.Chat> {
                // Feature 006 (Tablet Single-Thread Chat UI) will plug SingleThreadChatScreen here
                Box(modifier = Modifier.fillMaxSize())
            }
        }

        SnackbarHost(
            hostState = appState.snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .systemBarsPadding()
        )
    }
}
