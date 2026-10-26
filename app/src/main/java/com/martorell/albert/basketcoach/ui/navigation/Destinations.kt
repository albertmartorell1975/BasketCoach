package com.martorell.albert.basketcoach.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable

@Serializable
object DashboardRoute

@Serializable
object AnalysisRoute

@Serializable
object ChatRoute

data class TopLevelRoute<T : Any>(
    @param:StringRes val titleRes: Int,
    val route: T,
    val icon: ImageVector
)

fun NavHostController.navigatePoppingUpToStartDestination(route: Any) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
