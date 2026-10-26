package com.martorell.albert.basketcoach.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.martorell.albert.basketcoach.ui.analysis.AnalysisScreen
import com.martorell.albert.basketcoach.ui.components.BasketCoachNavRail
import com.martorell.albert.basketcoach.ui.components.BasketCoachTopBar
import com.martorell.albert.basketcoach.ui.dashboard.DashboardScreen

/**
 * Root navigation graph and layout shell for the BasketCoach tablet application.
 *
 * Implements the tablet landscape shell architecture:
 * - Left: 80dp [BasketCoachNavRail] for persistent section switching (Dashboard, Anàlisi, Chat).
 * - Right: 64dp [BasketCoachTopBar] + [NavHost] displaying the active screen.
 *
 * @param appState The root application state holder.
 */
@Composable
fun Navigation(
    appState: BasketCoachAppState = rememberBasketCoachAppState()
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        // Persistent Left Navigation Rail (80dp)
        BasketCoachNavRail(
            currentDestination = appState.currentDestination,
            onNavigateToDashboard = appState::navigateToDashboard,
            onNavigateToAnalysis = appState::navigateToAnalysis,
            onNavigateToChat = { appState.navigateToChat(null) }
        )

        // Main Content Area with TopBar + NavHost
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            // White Top Navigation Bar (64dp)
            BasketCoachTopBar()

            // Dynamic Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                NavHost(
                    navController = appState.navController,
                    startDestination = Destination.Dashboard
                ) {
                    composable<Destination.Dashboard> {
                        DashboardScreen(
                            onNavigateToChat = {
                                appState.navigateToChat(null)
                            }
                        )
                    }

                    composable<Destination.Analysis> {
                        AnalysisScreen(
                            onNavigateToChat = { prompt ->
                                appState.navigateToChat(prompt)
                            }
                        )
                    }

                    composable<Destination.Chat> {
                        // Feature 006 (Tablet Single-Thread Chat UI) will plug SingleThreadChatScreen here
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("ChatScreenPlaceholder")
                        )
                    }
                }

                SnackbarHost(
                    hostState = appState.snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
}
