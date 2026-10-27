package com.martorell.albert.basketcoach.ui.analysis

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
@Config(sdk = [34], qualifiers = "w1280dp-h800dp-land")
class AnalysisScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun initialRendering_rendersHeroCardAndAllFiveInsights() {
        var dispatchedPrompt: String? = null
        composeTestRule.setContent {
            BasketCoachTheme {
                AnalysisScreen(onNavigateToChat = { dispatchedPrompt = it })
            }
        }

        composeTestRule.onNodeWithText("Tots (5)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Fortaleses (2)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Àrees de Millora (3)").assertIsDisplayed()

        composeTestRule.onNodeWithText("Eficiència en Tir Interior").assertIsDisplayed()
        composeTestRule.onNodeWithText("Protecció de Cistella").assertIsDisplayed()
        composeTestRule.onNodeWithText("Balanç Defensiu en Transició").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rebot Defensiu i Punts de 2a Oportunitat").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gestió de Pèrdues en el 3r Quart").assertIsDisplayed()
    }

    @Test
    fun filterTabs_filtersStrengthsAndImprovementsCorrectly() {
        composeTestRule.setContent {
            BasketCoachTheme {
                AnalysisScreen(onNavigateToChat = {})
            }
        }

        // Tap Fortaleses (2)
        composeTestRule.onNodeWithText("Fortaleses (2)").performClick()
        composeTestRule.onNodeWithText("Eficiència en Tir Interior").assertIsDisplayed()
        composeTestRule.onNodeWithText("Protecció de Cistella").assertIsDisplayed()
        composeTestRule.onNodeWithText("Balanç Defensiu en Transició").assertDoesNotExist()
        composeTestRule.onNodeWithText("Rebot Defensiu i Punts de 2a Oportunitat").assertDoesNotExist()
        composeTestRule.onNodeWithText("Gestió de Pèrdues en el 3r Quart").assertDoesNotExist()

        // Tap Àrees de Millora (3)
        composeTestRule.onNodeWithText("Àrees de Millora (3)").performClick()
        composeTestRule.onNodeWithText("Eficiència en Tir Interior").assertDoesNotExist()
        composeTestRule.onNodeWithText("Protecció de Cistella").assertDoesNotExist()
        composeTestRule.onNodeWithText("Balanç Defensiu en Transició").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rebot Defensiu i Punts de 2a Oportunitat").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gestió de Pèrdues en el 3r Quart").assertIsDisplayed()

        // Tap back on Tots (5)
        composeTestRule.onNodeWithText("Tots (5)").performClick()
        composeTestRule.onNodeWithText("Eficiència en Tir Interior").assertIsDisplayed()
        composeTestRule.onNodeWithText("Balanç Defensiu en Transició").assertIsDisplayed()
    }

    @Test
    fun exploreChatButton_dispatchesNavigationPrompt() {
        var dispatchedPrompt: String? = null
        composeTestRule.setContent {
            BasketCoachTheme {
                AnalysisScreen(onNavigateToChat = { dispatchedPrompt = it })
            }
        }

        composeTestRule.onAllNodesWithText("Explorar al Xat").onFirst().performClick()
        assertNotNull(dispatchedPrompt)
        assertTrue(dispatchedPrompt!!.isNotBlank())
        assertTrue(dispatchedPrompt!!.contains("balanç defensiu", ignoreCase = true))
    }
}
