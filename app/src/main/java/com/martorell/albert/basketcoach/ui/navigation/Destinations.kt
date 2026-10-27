package com.martorell.albert.basketcoach.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable

/**
 * Type-safe navigation destination representing the Phase 1 team Dashboard screen.
 */
@Serializable
object DashboardRoute

/**
 * Type-safe navigation destination representing the AI Analysis screen.
 */
@Serializable
object AnalysisRoute

/**
 * Type-safe navigation destination representing the conversational AI Chat Assistant screen.
 */
@Serializable
object ChatRoute

/**
 * Metadata descriptor for a top-level destination displayed in the application shell's navigation rail.
 *
 * @param T Type of the route object (e.g. [DashboardRoute], [AnalysisRoute], [ChatRoute]).
 * @property titleRes String resource ID of the human-readable destination label.
 * @property route Target destination route object.
 * @property icon Material icon vector associated with this navigation destination.
 */
data class TopLevelRoute<T : Any>(
    @param:StringRes val titleRes: Int,
    val route: T,
    val icon: ImageVector
)

/**
 * Navigates to a top-level route while popping up to the graph's start destination to preserve
 * backstack state and avoid redundant destination instances.
 *
 * @param route The target destination route to navigate to.
 */
fun NavHostController.navigatePoppingUpToStartDestination(route: Any) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
