package com.martorell.albert.basketcoach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.martorell.albert.basketcoach.ui.BasketCoachApp
import com.martorell.albert.basketcoach.ui.navigation.rememberBasketCoachAppState
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main application entry-point activity for BasketCoach.
 *
 * Configured in sensor-landscape mode for Android tablets, enabling edge-to-edge
 * display and hosting the root [BasketCoachApp] composable with state-managed navigation.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appState = rememberBasketCoachAppState()
            BasketCoachTheme {
                BasketCoachApp(appState = appState)
            }
        }
    }
}
