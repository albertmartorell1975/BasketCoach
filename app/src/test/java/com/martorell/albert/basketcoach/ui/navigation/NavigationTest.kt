package com.martorell.albert.basketcoach.ui.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.martorell.albert.basketcoach.ui.BasketCoachApp
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun appShell_rendersNavRailAndTopBar() {
        composeTestRule.setContent {
            BasketCoachTheme {
                BasketCoachApp()
            }
        }

        composeTestRule.onNodeWithTag("nav_rail").assertIsDisplayed()
        composeTestRule.onNodeWithTag("top_bar").assertIsDisplayed()
        composeTestRule.onNodeWithTag("dashboard_screen").assertIsDisplayed()
    }

    @Test
    fun navRail_destinationsSwitchContent() {
        composeTestRule.setContent {
            BasketCoachTheme {
                BasketCoachApp()
            }
        }

        // Starts on Dashboard
        composeTestRule.onNodeWithTag("dashboard_screen").assertIsDisplayed()

        // Tap on Analysis
        composeTestRule.onNodeWithTag("nav_item_analysis").performClick()
        composeTestRule.onNodeWithTag("placeholder_screen").assertIsDisplayed()
        composeTestRule.onNode(hasText("Anàlisi") and hasAnyAncestor(hasTestTag("placeholder_screen"))).assertIsDisplayed()

        // Tap on Chat
        composeTestRule.onNodeWithTag("nav_item_chat").performClick()
        composeTestRule.onNodeWithTag("placeholder_screen").assertIsDisplayed()
        composeTestRule.onNode(hasText("Chat") and hasAnyAncestor(hasTestTag("placeholder_screen"))).assertIsDisplayed()

        // Tap back on Dashboard
        composeTestRule.onNodeWithTag("nav_item_dashboard").performClick()
        composeTestRule.onNodeWithTag("dashboard_screen").assertIsDisplayed()
    }
}
