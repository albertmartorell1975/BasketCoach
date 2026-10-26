package com.martorell.albert.basketcoach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.ui.dashboard.DashboardScreen
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardTab
import com.martorell.albert.basketcoach.ui.dashboard.preview.DashboardPreviewData
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BasketCoachTheme {
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
                        // Single-thread chat navigation hooked in feature_006/feature_008
                    },
                    onRefresh = {
                        uiState = DashboardPreviewData.sampleUiState
                    }
                )
            }
        }
    }
}
