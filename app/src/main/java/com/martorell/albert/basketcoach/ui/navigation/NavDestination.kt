package com.martorell.albert.basketcoach.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Insights
import androidx.compose.ui.graphics.vector.ImageVector
import com.martorell.albert.basketcoach.R

enum class NavDestination(
    @param:StringRes val titleRes: Int,
    val icon: ImageVector,
    val route: String
) {
    DASHBOARD(
        titleRes = R.string.nav_dashboard,
        icon = Icons.Filled.Dashboard,
        route = "dashboard"
    ),
    ANALYSIS(
        titleRes = R.string.nav_analysis,
        icon = Icons.Filled.Insights,
        route = "analysis"
    ),
    CHAT(
        titleRes = R.string.nav_chat,
        icon = Icons.Filled.Forum,
        route = "chat"
    )
}
