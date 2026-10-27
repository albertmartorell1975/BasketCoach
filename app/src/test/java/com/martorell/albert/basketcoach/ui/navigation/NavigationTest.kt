package com.martorell.albert.basketcoach.ui.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.martorell.albert.basketcoach.ui.BasketCoachApp
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w1280dp-h800dp-land")
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

        composeTestRule.onNodeWithContentDescription("Dashboard").assertIsDisplayed()
        composeTestRule.onNodeWithText("BasketCoach").assertIsDisplayed()
        composeTestRule.onNodeWithText("LÍNIA BASE FASE 1").assertIsDisplayed()
    }

    @Test
    fun navRail_destinationsSwitchContent() {
        composeTestRule.setContent {
            BasketCoachTheme {
                BasketCoachApp()
            }
        }

        // Starts on Dashboard
        composeTestRule.onNodeWithText("LÍNIA BASE FASE 1").assertIsDisplayed()

        // Tap on Analysis
        composeTestRule.onNodeWithContentDescription("Anàlisi").performClick()
        composeTestRule.onNodeWithText("REGISTRE FASE 1").assertIsDisplayed()

        // Tap on Chat
        composeTestRule.onNodeWithContentDescription("Chat").performClick()
        composeTestRule.onNodeWithText("Aquesta funcionalitat s'activarà a la Fase 2.").assertIsDisplayed()

        // Tap back on Dashboard
        composeTestRule.onNodeWithContentDescription("Dashboard").performClick()
        composeTestRule.onNodeWithText("LÍNIA BASE FASE 1").assertIsDisplayed()
    }

    @Test
    fun appState_navigatesViaTypeSafeRoutes() {
        lateinit var navController: TestNavHostController
        lateinit var appState: BasketCoachAppState

        composeTestRule.setContent {
            val context = LocalContext.current
            navController = TestNavHostController(context).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            appState = remember(navController) {
                BasketCoachAppState(
                    navController = navController,
                    coroutineScope = CoroutineScope(Dispatchers.Unconfined),
                    snackbarHostState = SnackbarHostState()
                )
            }
            BasketCoachTheme {
                BasketCoachApp(appState = appState)
            }
        }

        assertTrue(navController.currentDestination?.hasRoute(DashboardRoute::class) == true)

        composeTestRule.runOnUiThread {
            appState.navigateToAnalysis()
        }
        assertTrue(navController.currentDestination?.hasRoute(AnalysisRoute::class) == true)

        composeTestRule.runOnUiThread {
            appState.navigateToChat()
        }
        assertTrue(navController.currentDestination?.hasRoute(ChatRoute::class) == true)

        composeTestRule.runOnUiThread {
            appState.navigateToDashboard()
        }
        assertTrue(navController.currentDestination?.hasRoute(DashboardRoute::class) == true)
    }
}
