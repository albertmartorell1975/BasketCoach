package com.martorell.albert.basketcoach.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.ui.dashboard.DashboardScreen
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardTab
import com.martorell.albert.basketcoach.ui.dashboard.preview.DashboardPreviewData

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
                var uiState by remember { mutableStateOf(DashboardPreviewData.sampleUiState) }

                DashboardScreen(
                    state = uiState,
                    onGameSelected = { game: Game ->
                        uiState = uiState.copy(selectedGame = game)
                    },
                    onTabSelected = { tab: DashboardTab ->
                        uiState = uiState.copy(selectedTab = tab)
                    },
                    onNavigateToChat = {
                        appState.navController.navigate(Destination.Chat)
                    },
                    onRefresh = {
                        uiState = DashboardPreviewData.sampleUiState
                    }
                )
            }

            composable<Destination.Chat> {
                // Feature 006 (Tablet Single-Thread Chat UI) will plug ChatScreen here
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
