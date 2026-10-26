package com.martorell.albert.basketcoach.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Insights
import androidx.compose.ui.graphics.vector.ImageVector
import com.martorell.albert.basketcoach.R
import kotlinx.serialization.Serializable

@Serializable
object DashboardRoute

@Serializable
object AnalysisRoute

@Serializable
object ChatRoute

enum class NavRailDestination(
    @param:StringRes val titleRes: Int,
    val icon: ImageVector,
    val route: Any
) {
    DASHBOARD(
        titleRes = R.string.nav_dashboard,
        icon = Icons.Filled.Dashboard,
        route = DashboardRoute
    ),
    ANALYSIS(
        titleRes = R.string.nav_analysis,
        icon = Icons.Filled.Insights,
        route = AnalysisRoute
    ),
    CHAT(
        titleRes = R.string.nav_chat,
        icon = Icons.Filled.Forum,
        route = ChatRoute
    )
}
