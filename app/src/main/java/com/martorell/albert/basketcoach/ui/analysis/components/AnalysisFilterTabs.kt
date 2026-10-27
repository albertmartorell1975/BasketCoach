package com.martorell.albert.basketcoach.ui.analysis.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.ui.analysis.model.InsightFilterTab
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import com.martorell.albert.basketcoach.ui.theme.BcDevicePreview
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaSuccessGreen
import com.martorell.albert.basketcoach.ui.theme.PiaSurfaceContainerLow
import com.martorell.albert.basketcoach.ui.theme.PiaTacticalOrange
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted
import com.martorell.albert.basketcoach.ui.theme.PiaWhite

/**
 * Segmented filter tabs control for toggling between All, Strengths, and Areas of Improvement.
 *
 * Renders inside a container with active Navy pill styling and semantic dot indicators
 * (green for strengths, tactical orange for improvements).
 *
 * @param selectedTab Currently active filter tab.
 * @param totalCount Count of all available insights.
 * @param strengthsCount Count of strength insights.
 * @param improvementsCount Count of improvement insights.
 * @param onTabSelected Callback invoked when the user selects a filter tab.
 * @param modifier Composable modifier for styling and layout adjustments.
 */
@Composable
fun AnalysisFilterTabs(
    selectedTab: InsightFilterTab,
    totalCount: Int,
    strengthsCount: Int,
    improvementsCount: Int,
    onTabSelected: (InsightFilterTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(PiaSurfaceContainerLow)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterTabButton(
            text = stringResource(R.string.analysis_filter_all, totalCount),
            isSelected = selectedTab == InsightFilterTab.ALL,
            indicatorColor = null,
            onClick = { onTabSelected(InsightFilterTab.ALL) }
        )
        FilterTabButton(
            text = stringResource(R.string.analysis_filter_strengths, strengthsCount),
            isSelected = selectedTab == InsightFilterTab.STRENGTHS,
            indicatorColor = PiaSuccessGreen,
            onClick = { onTabSelected(InsightFilterTab.STRENGTHS) }
        )
        FilterTabButton(
            text = stringResource(R.string.analysis_filter_improvements, improvementsCount),
            isSelected = selectedTab == InsightFilterTab.IMPROVEMENTS,
            indicatorColor = PiaTacticalOrange,
            onClick = { onTabSelected(InsightFilterTab.IMPROVEMENTS) }
        )
    }
}

@Composable
private fun FilterTabButton(
    text: String,
    isSelected: Boolean,
    indicatorColor: Color?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) PiaNavy else Color.Transparent
    val textColor = if (isSelected) PiaWhite else PiaTextMuted
    val fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (indicatorColor != null) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(indicatorColor)
            )
        }
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = fontWeight,
            letterSpacing = 0.02.sp
        )
    }
}

@BcThemePreview
@BcDevicePreview
@Composable
private fun AnalysisFilterTabsPreview() {
    BasketCoachTheme {
        AnalysisFilterTabs(
            selectedTab = InsightFilterTab.ALL,
            totalCount = 5,
            strengthsCount = 2,
            improvementsCount = 3,
            onTabSelected = {}
        )
    }
}
