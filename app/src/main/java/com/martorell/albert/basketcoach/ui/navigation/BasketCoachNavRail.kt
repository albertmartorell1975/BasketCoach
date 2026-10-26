package com.martorell.albert.basketcoach.ui.navigation

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import com.martorell.albert.basketcoach.ui.theme.BcDevicePreview
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview
import com.martorell.albert.basketcoach.ui.theme.PiaNavy

@Composable
fun BasketCoachNavRail(
    navOptions: List<TopLevelRoute<out Any>> = BasketCoachAppState.NAV_OPTIONS,
    isRouteSelected: @Composable (Any) -> Boolean,
    onRouteSelected: (Any) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(80.dp)
            .fillMaxHeight()
            .background(PiaNavy)
            .testTag("nav_rail")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            navOptions.forEach { navOption ->
                val isSelected = isRouteSelected(navOption.route)
                val backgroundColor = if (isSelected) Color.White else Color.Transparent
                val contentColor = if (isSelected) PiaNavy else Color.White.copy(alpha = 0.7f)
                val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                val testTag = when (navOption.route) {
                    DashboardRoute -> "nav_item_dashboard"
                    AnalysisRoute -> "nav_item_analysis"
                    ChatRoute -> "nav_item_chat"
                    else -> "nav_item_${navOption.route}"
                }

                Column(
                    modifier = Modifier
                        .size(width = 64.dp, height = 56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(backgroundColor)
                        .clickable { onRouteSelected(navOption.route) }
                        .padding(horizontal = 4.dp, vertical = 6.dp)
                        .testTag(testTag),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = navOption.icon,
                        contentDescription = stringResource(navOption.titleRes),
                        tint = contentColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(navOption.titleRes),
                        color = contentColor,
                        fontSize = 12.sp,
                        lineHeight = 14.sp,
                        fontWeight = fontWeight,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
                .background(Color.White.copy(alpha = 0.1f))
        )
    }
}

@BcThemePreview
@BcDevicePreview
@Composable
private fun BasketCoachNavRailPreview() {
    BasketCoachTheme {
        BasketCoachNavRail(
            isRouteSelected = { it == DashboardRoute },
            onRouteSelected = {}
        )
    }
}
