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
