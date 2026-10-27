package com.martorell.albert.basketcoach.ui.chat

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog
import com.martorell.albert.basketcoach.ui.chat.components.AiMessageBubble
import com.martorell.albert.basketcoach.ui.chat.data.ChatPreviewData
import com.martorell.albert.basketcoach.ui.chat.model.ChatDrillOptionUiModel
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import org.junit.Assert.assertEquals
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
    fun chatScreen_rendersWorkbenchLayout() {
        composeTestRule.setContent {
            BasketCoachTheme {
                ChatScreen()
            }
        }

        composeTestRule.onNodeWithText("Espai de Reflexió Tàctica · 1a Fase").assertIsDisplayed()
        composeTestRule.onNodeWithText("Balanç: 3V - 11D").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tria un Fil de Treball").assertIsDisplayed()
        composeTestRule.onNodeWithText("Assistent Socràtic BasketCoach").assertIsDisplayed()
        composeTestRule.onNodeWithText("Línia Base: 14 Partits Consolidats").assertIsDisplayed()
    }

    @Test
    fun chatScreen_topicSelection_switchesToDiagnosticEvidence() {
        composeTestRule.setContent {
            BasketCoachTheme {
                ChatScreen()
            }
        }

        // Tap on Topic 1
        composeTestRule.onNodeWithText("Control del Rebot Defensiu").performClick()

        // Diagnostic Evidence shown
        composeTestRule.onNodeWithText("Àrea de Millora #1 · Prioritària").assertIsDisplayed()
        composeTestRule.onNodeWithText("14.8").assertIsDisplayed()
        composeTestRule.onNodeWithText("13.2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Regla de Zero-Suposició (Zero-Assumption)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Origen dels Rebots Concedits").assertIsDisplayed()

        // Tap toggle to return to topic list
        composeTestRule.onNodeWithText("← Tots els fils").performClick()
        composeTestRule.onNodeWithText("Tria un Fil de Treball").assertIsDisplayed()
    }

    @Test
    fun chatScreen_drillSelection_appendsConfirmationAndSummary() {
        composeTestRule.setContent {
            BasketCoachTheme {
                ChatScreen()
            }
        }

        // Initially drill option A is present in AI message
        composeTestRule.onNodeWithText("Seleccionar Opció A").assertIsDisplayed()

        // Tap Option A
        val node = composeTestRule.onNodeWithText("Seleccionar Opció A").fetchSemanticsNode()
        node.config[SemanticsActions.OnClick].action?.invoke()
        composeTestRule.waitForIdle()

        // Summary Card appended from drill selection
        composeTestRule.onNodeWithText("Capacitat de Tancar el Rebot Defensiu", substring = true).assertExists()
        composeTestRule.onNodeWithText("< 9.5 pts 2a oportunitat", substring = true).assertExists()
    }

    @Test
    fun aiMessageBubble_drillSelection_firesCallback() {
        var selectedDrill: ChatDrillOptionUiModel? = null
        composeTestRule.setContent {
            BasketCoachTheme {
                AiMessageBubble(
                    model = ChatPreviewData.initialMessages[2],
                    onDrillOptionSelect = { selectedDrill = it }
                )
            }
        }

        val node = composeTestRule.onNodeWithText("Seleccionar Opció A").fetchSemanticsNode()
        node.config[SemanticsActions.OnClick].action?.invoke()
        composeTestRule.waitForIdle()
        assertEquals("drill_option_a", selectedDrill?.id)
    }

    @Test
    fun chatScreen_textInputAndSend_appendsCoachBubble() {
        composeTestRule.setContent {
            BasketCoachTheme {
                ChatScreen()
            }
        }

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

        // Diagnostic Evidence is immediately active
        composeTestRule.onNodeWithText("Àrea de Millora #1 · Prioritària").assertIsDisplayed()
        composeTestRule.onNodeWithText("14.8").assertIsDisplayed()
        // Input text pre-populated
        composeTestRule.onNodeWithText(initialPrompt).assertIsDisplayed()
    }

    @Test
    @Config(qualifiers = "es-rES-w1280dp-h800dp-land")
    fun chatScreen_spanishLocalization() {
        composeTestRule.setContent {
            BasketCoachTheme {
                ChatScreen()
            }
        }

        composeTestRule.onNodeWithText("Espacio de Reflexión Táctica · 1ª Fase").assertIsDisplayed()
        composeTestRule.onNodeWithText("Balance: 3V - 11D").assertIsDisplayed()
        composeTestRule.onNodeWithText("Elige un Hilo de Trabajo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Asistente Socrático BasketCoach").assertIsDisplayed()
    }
}
