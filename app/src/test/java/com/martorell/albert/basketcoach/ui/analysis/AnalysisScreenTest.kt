package com.martorell.albert.basketcoach.ui.analysis

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.martorell.albert.basketcoach.ui.analysis.model.AnalysisFilterTab
import com.martorell.albert.basketcoach.ui.analysis.model.AnalysisUiState
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
class AnalysisScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun rendersAnalysisScreenWithHeroFilterAndInsightsGrid() {
        val state = AnalysisUiState(insights = DashboardPreviewData.sampleInsights)

        composeTestRule.setContent {
            BasketCoachTheme {
                AnalysisContent(
                    state = state,
                    onTabSelected = {},
                    onNavigateToChat = {},
                    onRefresh = {}
                )
            }
        }

        // Verify root components
        composeTestRule.onNodeWithTag("AnalysisScreen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("AnalysisHeroCard").assertIsDisplayed()
        composeTestRule.onNodeWithTag("AnalysisFilterSegmentedControl").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Analysis_InsightsGrid").assertIsDisplayed()

        // Verify all 5 insight cards are rendered in ALL tab
        composeTestRule.onNodeWithTag("InsightCard_insight-f1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("InsightCard_insight-f2").assertIsDisplayed()
        composeTestRule.onNodeWithTag("InsightCard_insight-m1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("InsightCard_insight-m2").assertIsDisplayed()
        composeTestRule.onNodeWithTag("InsightCard_insight-m3").assertIsDisplayed()
    }

    @Test
    fun tabFiltering_strengthsTab_displaysOnlyStrengths() {
        val state = AnalysisUiState(
            insights = DashboardPreviewData.sampleInsights,
            selectedTab = AnalysisFilterTab.STRENGTHS
        )

        composeTestRule.setContent {
            BasketCoachTheme {
                AnalysisContent(
                    state = state,
                    onTabSelected = {},
                    onNavigateToChat = {},
                    onRefresh = {}
                )
            }
        }

        // Strengths visible
        composeTestRule.onNodeWithTag("InsightCard_insight-f1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("InsightCard_insight-f2").assertIsDisplayed()

        // Improvements not displayed
        composeTestRule.onNodeWithTag("InsightCard_insight-m1").assertDoesNotExist()
        composeTestRule.onNodeWithTag("InsightCard_insight-m2").assertDoesNotExist()
        composeTestRule.onNodeWithTag("InsightCard_insight-m3").assertDoesNotExist()
    }

    @Test
    fun tabFiltering_improvementsTab_displaysOnlyImprovements() {
        val state = AnalysisUiState(
            insights = DashboardPreviewData.sampleInsights,
            selectedTab = AnalysisFilterTab.IMPROVEMENTS
        )

        composeTestRule.setContent {
            BasketCoachTheme {
                AnalysisContent(
                    state = state,
                    onTabSelected = {},
                    onNavigateToChat = {},
                    onRefresh = {}
                )
            }
        }

        // Improvements visible
        composeTestRule.onNodeWithTag("InsightCard_insight-m1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("InsightCard_insight-m2").assertIsDisplayed()
        composeTestRule.onNodeWithTag("InsightCard_insight-m3").assertIsDisplayed()

        // Strengths not displayed
        composeTestRule.onNodeWithTag("InsightCard_insight-f1").assertDoesNotExist()
        composeTestRule.onNodeWithTag("InsightCard_insight-f2").assertDoesNotExist()
    }

    @Test
    fun clickExploreChat_triggersCallbackWithPrompt() {
        var capturedPrompt: String? = null
        val state = AnalysisUiState(insights = DashboardPreviewData.sampleInsights)

        composeTestRule.setContent {
            BasketCoachTheme {
                AnalysisContent(
                    state = state,
                    onTabSelected = {},
                    onNavigateToChat = { prompt -> capturedPrompt = prompt },
                    onRefresh = {}
                )
            }
        }

        // Click "Explorar al Xat" on insight-m1 (Turnover improvement)
        composeTestRule.onNodeWithTag("Btn_ExploreChat_insight-m1").performClick()

        assertNotNull(capturedPrompt)
        assertTrue(capturedPrompt!!.contains("pèrdues de pilota"))
    }

    @Test
    fun segmentedControlClick_invokesOnTabSelected() {
        var selectedTab: AnalysisFilterTab? = null
        val state = AnalysisUiState(insights = DashboardPreviewData.sampleInsights)

        composeTestRule.setContent {
            BasketCoachTheme {
                AnalysisContent(
                    state = state,
                    onTabSelected = { selectedTab = it },
                    onNavigateToChat = {},
                    onRefresh = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("SegmentedControl_STRENGTHS").performClick()
        assertEquals(AnalysisFilterTab.STRENGTHS, selectedTab)
    }

    @Test
    fun displaysLoadingState() {
        val loadingState = AnalysisUiState(isLoading = true)

        composeTestRule.setContent {
            BasketCoachTheme {
                AnalysisContent(
                    state = loadingState,
                    onTabSelected = {},
                    onNavigateToChat = {},
                    onRefresh = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("Analysis_LoadingIndicator").assertIsDisplayed()
    }

    @Test
    fun displaysErrorStateAndHandlesRefresh() {
        var refreshed = false
        val errorState = AnalysisUiState(errorMessage = "AI generation failure")

        composeTestRule.setContent {
            BasketCoachTheme {
                AnalysisContent(
                    state = errorState,
                    onTabSelected = {},
                    onNavigateToChat = {},
                    onRefresh = { refreshed = true }
                )
            }
        }

        composeTestRule.onNodeWithTag("Analysis_ErrorBanner").assertIsDisplayed()
        composeTestRule.onNodeWithText("Reintentar").performClick()
        assertTrue(refreshed)
    }
}
