package com.martorell.albert.basketcoach.ui.dashboard

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardTab
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardUiState
import com.martorell.albert.basketcoach.ui.dashboard.preview.DashboardPreviewData
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w1280dp-h800dp-land")
class DashboardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun rendersTwoPaneLandscapeLayoutWithGamesAndInsights() {
        val state = DashboardPreviewData.sampleUiState

        composeTestRule.setContent {
            BasketCoachTheme {
                DashboardScreen(
                    state = state,
                    onGameSelected = {},
                    onTabSelected = {},
                    onNavigateToChat = {},
                    onRefresh = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("DashboardScreen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("GameListPane").assertIsDisplayed()
        composeTestRule.onNodeWithTag("InsightsDrillsPane").assertIsDisplayed()
        composeTestRule.onNodeWithTag("TeamRecordCard").assertIsDisplayed()
        composeTestRule.onNodeWithTag("AskCoachAiButton").assertIsDisplayed()

        // Check Round 3 is displayed
        composeTestRule.onNodeWithTag("GameCard_Round_3").assertIsDisplayed()
    }

    @Test
    fun gameCardClick_triggersOnGameSelected() {
        var selected: Game? = null
        val state = DashboardPreviewData.sampleUiState

        composeTestRule.setContent {
            BasketCoachTheme {
                DashboardScreen(
                    state = state,
                    onGameSelected = { selected = it },
                    onTabSelected = {},
                    onNavigateToChat = {},
                    onRefresh = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("GameCard_Round_1").performClick()
        assertNotNull(selected)
        assertEquals(1, selected?.round)
    }

    @Test
    fun tabSelection_switchesFilter() {
        var clickedTab: DashboardTab? = null
        val state = DashboardPreviewData.sampleUiState

        composeTestRule.setContent {
            BasketCoachTheme {
                DashboardScreen(
                    state = state,
                    onGameSelected = {},
                    onTabSelected = { clickedTab = it },
                    onNavigateToChat = {},
                    onRefresh = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("DashboardTab_DRILLS").performClick()
        assertEquals(DashboardTab.DRILLS, clickedTab)
    }

    @Test
    fun chatButton_triggersNavigateToChat() {
        var chatNavigated = false
        val state = DashboardPreviewData.sampleUiState

        composeTestRule.setContent {
            BasketCoachTheme {
                DashboardScreen(
                    state = state,
                    onGameSelected = {},
                    onTabSelected = {},
                    onNavigateToChat = { chatNavigated = true },
                    onRefresh = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("AskCoachAiButton").performClick()
        assertTrue(chatNavigated)
    }

    @Test
    fun loadingState_displaysProgressIndicator() {
        val state = DashboardUiState(isLoading = true)

        composeTestRule.setContent {
            BasketCoachTheme {
                DashboardScreen(
                    state = state,
                    onGameSelected = {},
                    onTabSelected = {},
                    onNavigateToChat = {},
                    onRefresh = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("DashboardLoadingIndicator").assertIsDisplayed()
    }

    @Test
    fun errorState_displaysErrorMessageAndRetry() {
        var refreshed = false
        val state = DashboardUiState(errorMessage = "Failed to load Phase 1 dataset")

        composeTestRule.setContent {
            BasketCoachTheme {
                DashboardScreen(
                    state = state,
                    onGameSelected = {},
                    onTabSelected = {},
                    onNavigateToChat = {},
                    onRefresh = { refreshed = true }
                )
            }
        }

        composeTestRule.onNodeWithTag("DashboardErrorBanner").assertIsDisplayed()
        composeTestRule.onNodeWithText("Failed to load Phase 1 dataset").assertIsDisplayed()
        composeTestRule.onNodeWithTag("DashboardRetryButton").performClick()
        assertTrue(refreshed)
    }
}
