package com.martorell.albert.basketcoach.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.martorell.albert.basketcoach.ui.analysis.AnalysisScreen
import com.martorell.albert.basketcoach.ui.chat.ChatScreen
import com.martorell.albert.basketcoach.ui.dashboard.DashboardScreen

@Composable
fun Navigation(
    appState: BasketCoachAppState,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = appState.navController,
        startDestination = DashboardRoute,
        modifier = modifier
    ) {
        composable<DashboardRoute> {
            DashboardScreen()
        }
        composable<AnalysisRoute> {
            AnalysisScreen(
                onNavigateToChat = { prompt ->
                    appState.navigateToChat(prompt)
                }
            )
        }
        composable<ChatRoute> { backStackEntry ->
            val chatRoute = backStackEntry.toRoute<ChatRoute>()
            ChatScreen(initialPrompt = chatRoute.initialPrompt)
        }
    }
}
