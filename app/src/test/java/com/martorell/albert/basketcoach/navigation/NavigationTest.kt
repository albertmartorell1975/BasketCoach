package com.martorell.albert.basketcoach.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w1280dp-h800dp-land")
class NavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun startDestination_isDashboard() {
        composeTestRule.setContent {
            BasketCoachTheme {
                Navigation()
            }
        }

        composeTestRule.onNodeWithTag("DashboardScreen").assertIsDisplayed()
    }

    @Test
    fun clickAskCoachAi_navigatesToChat() {
        var appState: BasketCoachAppState? = null

        composeTestRule.setContent {
            BasketCoachTheme {
                val state = rememberBasketCoachAppState()
                appState = state
                Navigation(appState = state)
            }
        }

        composeTestRule.onNodeWithTag("AskCoachAiButton").performClick()
        composeTestRule.waitForIdle()
        assertEquals(Destination.Chat::class.qualifiedName, appState?.navController?.currentDestination?.route)
    }
}
