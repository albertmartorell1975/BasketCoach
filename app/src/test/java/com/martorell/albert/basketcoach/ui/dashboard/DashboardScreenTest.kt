package com.martorell.albert.basketcoach.ui.dashboard

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import com.martorell.albert.basketcoach.ui.dashboard.data.Phase1PreviewData
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DashboardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun heroCard_displaysCompetitiveBalanceAndBadges() {
        composeTestRule.setContent {
            BasketCoachTheme {
                DashboardScreen(uiState = Phase1PreviewData.uiState)
            }
        }

        composeTestRule.onNodeWithTag("dashboard_hero_card").assertIsDisplayed()
        composeTestRule.onNodeWithText("Balanç Competitiu: 3 Victòries - 11 Derrotes (3V - 11D)").assertIsDisplayed()
        composeTestRule.onNodeWithText("21.4% victòries").assertIsDisplayed()
        composeTestRule.onNodeWithText("LÍNIA BASE FASE 1").assertIsDisplayed()
        composeTestRule.onNode(hasText("14") and hasAnyAncestor(hasTestTag("dashboard_hero_card"))).assertIsDisplayed()
        composeTestRule.onNode(hasText("PARTITS") and hasAnyAncestor(hasTestTag("dashboard_hero_card"))).assertIsDisplayed()
    }

    @Test
    fun kpiCards_displayAllFourMetrics() {
        composeTestRule.setContent {
            BasketCoachTheme {
                DashboardScreen(uiState = Phase1PreviewData.uiState)
            }
        }

        composeTestRule.onNodeWithTag("kpi_card_row").assertIsDisplayed()
        composeTestRule.onNodeWithTag("kpi_card_scoring").assertIsDisplayed()
        composeTestRule.onNodeWithTag("kpi_card_rebounds").assertIsDisplayed()
        composeTestRule.onNodeWithTag("kpi_card_shooting").assertIsDisplayed()
        composeTestRule.onNodeWithTag("kpi_card_game_control").assertIsDisplayed()

        // Metric values
        composeTestRule.onNodeWithText("64.6").assertIsDisplayed()
        composeTestRule.onNodeWithText("22.8").assertIsDisplayed()
        composeTestRule.onNodeWithText("40.5%").assertIsDisplayed()
        composeTestRule.onNodeWithText("0.59").assertIsDisplayed()
    }

    @Test
    fun evolutionChart_rendersAllRoundsAndWinBadges() {
        composeTestRule.setContent {
            BasketCoachTheme {
                DashboardScreen(uiState = Phase1PreviewData.uiState)
            }
        }

        composeTestRule.onNodeWithTag("phase1_evolution_chart").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("W 71-68").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("W 68-65").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("W 67-65").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun matchesTable_rendersAllFourteenGames() {
        composeTestRule.setContent {
            BasketCoachTheme {
                DashboardScreen(uiState = Phase1PreviewData.uiState)
            }
        }

        composeTestRule.onNodeWithTag("phase1_matches_table").performScrollTo().assertIsDisplayed()

        // Verify all 14 match rows exist in the table
        (1..14).forEach { round ->
            composeTestRule.onNodeWithTag("match_row_J$round").assertExists()
        }

        // Verify win scores exist in the table
        composeTestRule.onNodeWithText("71 - 68").assertExists()
        composeTestRule.onNodeWithText("68 - 65").assertExists()
        composeTestRule.onNodeWithText("65 - 67").assertExists()
    }
}
