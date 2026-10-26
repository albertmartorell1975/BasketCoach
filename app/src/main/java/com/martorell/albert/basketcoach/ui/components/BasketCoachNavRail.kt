package com.martorell.albert.basketcoach.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.navigation.Destination
import com.martorell.albert.basketcoach.ui.theme.PiaNavy

enum class NavItem(
    val titleRes: Int,
    val icon: ImageVector,
    val testTag: String
) {
    DASHBOARD(R.string.nav_dashboard, Icons.Default.Dashboard, "NavRail_Dashboard"),
    ANALYSIS(R.string.nav_analysis, Icons.AutoMirrored.Filled.ShowChart, "NavRail_Analysis"),
    CHAT(R.string.nav_chat, Icons.Default.ChatBubbleOutline, "NavRail_Chat")
}

@Composable
fun BasketCoachNavRail(
    currentDestination: Destination,
    onNavigateToDashboard: () -> Unit,
    onNavigateToAnalysis: () -> Unit,
    onNavigateToChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(80.dp)
            .fillMaxHeight()
            .background(PiaNavy)
            .testTag("BasketCoachNavRail")
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        NavItemView(
            item = NavItem.DASHBOARD,
            isSelected = currentDestination is Destination.Dashboard,
            onClick = onNavigateToDashboard
        )

        Spacer(modifier = Modifier.height(16.dp))

        NavItemView(
            item = NavItem.ANALYSIS,
            isSelected = currentDestination is Destination.Analysis,
            onClick = onNavigateToAnalysis
        )

        Spacer(modifier = Modifier.height(16.dp))

        NavItemView(
            item = NavItem.CHAT,
            isSelected = currentDestination is Destination.Chat,
            onClick = onNavigateToChat
        )
    }
}

@Composable
private fun NavItemView(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerBg = if (isSelected) Color.White else Color.Transparent
    val contentColor = if (isSelected) PiaNavy else Color.White.copy(alpha = 0.70f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(containerBg)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
            .testTag(item.testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = stringResource(item.titleRes),
                tint = contentColor,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(item.titleRes),
                color = contentColor,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
