package com.martorell.albert.basketcoach.ui.dashboard

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardUiState
import com.martorell.albert.basketcoach.ui.dashboard.preview.DashboardPreviewData
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
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
    fun rendersDashboardWithHeroKpisChartAndMatchesTable() {
        val state = DashboardPreviewData.sampleUiState

        composeTestRule.setContent {
            BasketCoachTheme {
                DashboardContent(
                    state = state,
                    onNavigateToChat = {},
                    onRefresh = {}
                )
            }
        }

        // Verify root screen and main sections
        composeTestRule.onNodeWithTag("DashboardScreen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("DashboardHeroCard").assertIsDisplayed()
        composeTestRule.onNodeWithTag("KpiCardRow").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Phase1EvolutionChart").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Phase1MatchesTable").assertIsDisplayed()

        // Verify 4 KPI cards
        composeTestRule.onNodeWithTag("KpiCard_SCORING").assertIsDisplayed()
        composeTestRule.onNodeWithTag("KpiCard_REBOUNDING").assertIsDisplayed()
        composeTestRule.onNodeWithTag("KpiCard_SHOOTING").assertIsDisplayed()
        composeTestRule.onNodeWithTag("KpiCard_CONTROL").assertIsDisplayed()

        // Verify win badges in chart (J3, J9, J10)
        composeTestRule.onNodeWithTag("Chart_WinBadge_J3").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Chart_WinBadge_J9").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Chart_WinBadge_J10").assertIsDisplayed()

        // Verify match rows in consolidated table exist
        composeTestRule.onNodeWithTag("MatchRow_1").assertExists()
        composeTestRule.onNodeWithTag("MatchRow_3").assertExists()
        composeTestRule.onNodeWithTag("MatchRow_14").assertExists()
    }

    @Test
    fun displaysLoadingIndicatorWhenLoading() {
        val state = DashboardUiState(isLoading = true)

        composeTestRule.setContent {
            BasketCoachTheme {
                DashboardContent(
                    state = state,
                    onNavigateToChat = {},
                    onRefresh = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("Dashboard_LoadingIndicator").assertIsDisplayed()
    }

    @Test
    fun displaysErrorBannerAndHandlesRefresh() {
        var refreshed = false
        val state = DashboardUiState(errorMessage = "Network connection failed")

        composeTestRule.setContent {
            BasketCoachTheme {
                DashboardContent(
                    state = state,
                    onNavigateToChat = {},
                    onRefresh = { refreshed = true }
                )
            }
        }

        composeTestRule.onNodeWithTag("Dashboard_ErrorBanner").assertIsDisplayed()
        composeTestRule.onNodeWithText("Network connection failed").assertIsDisplayed()
        composeTestRule.onNodeWithText("Reintentar").performClick()
        assertTrue(refreshed)
    }
}
