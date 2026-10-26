package com.martorell.albert.basketcoach.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
    fun startDestination_isDashboard_withNavRailAndTopBar() {
        composeTestRule.setContent {
            BasketCoachTheme {
                Navigation()
            }
        }

        // Verify shell components
        composeTestRule.onNodeWithTag("BasketCoachNavRail").assertIsDisplayed()
        composeTestRule.onNodeWithTag("BasketCoachTopBar").assertIsDisplayed()
        composeTestRule.onNodeWithTag("TopBar_Logo").assertIsDisplayed()
        composeTestRule.onNodeWithTag("TopBar_ConsolidatedBadge").assertIsDisplayed()
        composeTestRule.onNodeWithTag("TopBar_CoachProfile").assertIsDisplayed()

        // Verify dashboard screen is displayed as start destination
        composeTestRule.onNodeWithTag("DashboardScreen").assertIsDisplayed()
    }

    @Test
    fun navRail_switchBetweenDashboardAndAnalysis() {
        composeTestRule.setContent {
            BasketCoachTheme {
                Navigation()
            }
        }

        // Start at Dashboard
        composeTestRule.onNodeWithTag("DashboardScreen").assertIsDisplayed()

        // Navigate to Analysis
        composeTestRule.onNodeWithTag("NavRail_Analysis").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("AnalysisScreen").assertIsDisplayed()

        // Navigate back to Dashboard
        composeTestRule.onNodeWithTag("NavRail_Dashboard").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("DashboardScreen").assertIsDisplayed()
    }

    @Test
    fun navRail_navigateToChatPlaceholder() {
        composeTestRule.setContent {
            BasketCoachTheme {
                Navigation()
            }
        }

        // Navigate to Chat
        composeTestRule.onNodeWithTag("NavRail_Chat").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("ChatScreenPlaceholder").assertIsDisplayed()
    }
}
