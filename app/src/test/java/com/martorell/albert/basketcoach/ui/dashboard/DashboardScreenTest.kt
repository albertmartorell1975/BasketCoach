package com.martorell.albert.basketcoach.ui.dashboard

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.ui.dashboard.data.Phase1PreviewData
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardUiContent
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardUiState
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w1280dp-h800dp-land")
class DashboardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dashboardScreen_rendersLoadingState() {
        val loadingState = DashboardUiState(
            content = DashboardUiContent.Loading,
            isRefreshing = false
        )
        composeTestRule.setContent {
            BasketCoachTheme {
                DashboardScreen(uiState = loadingState)
            }
        }

        composeTestRule.onNodeWithText("Carregant dades de la 1a Fase...").assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_rendersErrorStateAndTriggersRetry() {
        var retried = false
        val errorState = DashboardUiState(
            content = DashboardUiContent.Error(R.string.dashboard_error_empty),
            isRefreshing = false
        )
        composeTestRule.setContent {
            BasketCoachTheme {
                DashboardScreen(
                    uiState = errorState,
                    onRetry = { retried = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Error en carregar les dades").assertIsDisplayed()
        composeTestRule.onNodeWithText("No s'han trobat partits de la 1a Fase al conjunt de dades.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Reintentar").assertIsDisplayed().performClick()
        assertTrue(retried)
    }

    @Test
    fun dashboardScreen_rendersHeroBalanceAndFourKpiMetrics() {
        composeTestRule.setContent {
            BasketCoachTheme {
                DashboardScreen(uiState = Phase1PreviewData.uiState)
            }
        }

        composeTestRule.onNodeWithText("Balanç Competitiu: 3 Victòries - 11 Derrotes (3V - 11D)").assertIsDisplayed()
        composeTestRule.onNodeWithText("21.4% victòries").assertIsDisplayed()
        composeTestRule.onNodeWithText("LÍNIA BASE FASE 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("ANOTACIÓ MITJANA").assertIsDisplayed()
        composeTestRule.onNodeWithText("64.6").assertIsDisplayed()
        composeTestRule.onNodeWithText("REBOTS & BALANÇ").assertIsDisplayed()
        composeTestRule.onNodeWithText("22.8").assertIsDisplayed()
        composeTestRule.onNodeWithText("EFICIÈNCIA TIR (EFG%)").assertIsDisplayed()
        composeTestRule.onNodeWithText("40.5%").assertIsDisplayed()
        composeTestRule.onNodeWithText("CONTROL DE JOC").assertIsDisplayed()
        composeTestRule.onNodeWithText("0.59").assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_scrollsToEvolutionChartAndMatchesTable() {
        composeTestRule.setContent {
            BasketCoachTheme {
                DashboardScreen(uiState = Phase1PreviewData.uiState)
            }
        }

        composeTestRule.onNodeWithText("W 71-68").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("W 68-65").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("W 67-65").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Partits de la 1a Fase (14 Jornades Consolidades)").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("71 - 68").assertExists()
    }
}
