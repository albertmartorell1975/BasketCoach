package com.martorell.albert.basketcoach.ui.chat

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w1280dp-h800dp-land")
class ChatScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun chatScreen_rendersWorkbenchAndSwitchesTopicDiagnosticEvidence() {
        composeTestRule.setContent {
            BasketCoachTheme {
                ChatScreen(initialPrompt = "")
            }
        }

        composeTestRule.onNodeWithText("Espai de Reflexió Tàctica · 1a Fase").assertIsDisplayed()
        composeTestRule.onNodeWithText("Balanç: 3V - 11D").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tria un Fil de Treball").assertIsDisplayed()
        composeTestRule.onNodeWithText("Assistent Socràtic BasketCoach").assertIsDisplayed()

        // Tap on Topic 1 -> switches to Diagnostic Evidence
        composeTestRule.onNodeWithText("Control del Rebot Defensiu").performClick()
        composeTestRule.onNodeWithText("Àrea de Millora #1 · Prioritària").assertIsDisplayed()
        composeTestRule.onNodeWithText("14.8").assertIsDisplayed()
        composeTestRule.onNodeWithText("Regla de Zero-Suposició (Zero-Assumption)").assertIsDisplayed()

        // Tap toggle to return to topic list
        composeTestRule.onNodeWithText("← Tots els fils").performClick()
        composeTestRule.onNodeWithText("Tria un Fil de Treball").assertIsDisplayed()
    }

    @Test
    fun chatScreen_drillSelectionAndTextInput_appendMessagesAndSummary() {
        composeTestRule.setContent {
            BasketCoachTheme {
                ChatScreen(initialPrompt = "")
            }
        }

        val node = composeTestRule.onNodeWithText("Seleccionar Opció A").fetchSemanticsNode()
        node.config[SemanticsActions.OnClick].action?.invoke()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Capacitat de Tancar el Rebot Defensiu", substring = true).assertExists()
        composeTestRule.onNodeWithText("< 9.5 pts 2a oportunitat", substring = true).assertExists()

        val testReflection = "Hem de tancar millor la línia de passada al pivot rival."
        composeTestRule.onNodeWithText("Escriu una pregunta o reflexió lliure sobre la 1a fase...").performTextInput(testReflection)
        composeTestRule.onNodeWithContentDescription("Enviar missatge").performClick()

        composeTestRule.onNodeWithText(testReflection).assertIsDisplayed()
    }

    @Test
    fun chatScreen_navigatingWithInitialPrompt_prepopulatesAndSelectsEvidence() {
        val initialPrompt = "Rebot Defensiu i Punts de 2a Oportunitat"
        composeTestRule.setContent {
            BasketCoachTheme {
                ChatScreen(initialPrompt = initialPrompt)
            }
        }

        composeTestRule.onNodeWithText("Àrea de Millora #1 · Prioritària").assertIsDisplayed()
        composeTestRule.onNodeWithText("14.8").assertIsDisplayed()
        composeTestRule.onNodeWithText(initialPrompt).assertIsDisplayed()
    }
}
