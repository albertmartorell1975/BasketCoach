package com.martorell.albert.basketcoach.ui.analysis.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.ui.analysis.model.AnalysisFilterTab
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaSuccessBadge
import com.martorell.albert.basketcoach.ui.theme.PiaSurface
import com.martorell.albert.basketcoach.ui.theme.PiaSurfaceVariant
import com.martorell.albert.basketcoach.ui.theme.PiaTacticalOrange
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted

/**
 * Segmented pill control for filtering tactical insights between All, Strengths, and Improvements.
 *
 * @param selectedTab Current active filter.
 * @param totalCount Total number of insights.
 * @param strengthsCount Count of strengths.
 * @param improvementsCount Count of improvement areas.
 * @param onTabSelected Callback invoked when a tab is selected.
 */
@Composable
fun AnalysisFilterSegmentedControl(
    selectedTab: AnalysisFilterTab,
    totalCount: Int,
    strengthsCount: Int,
    improvementsCount: Int,
    onTabSelected: (AnalysisFilterTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(PiaSurfaceVariant)
            .padding(4.dp)
            .testTag("AnalysisFilterSegmentedControl"),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SegmentPill(
            text = stringResource(R.string.tab_all_format, totalCount),
            dotColor = null,
            isSelected = selectedTab == AnalysisFilterTab.ALL,
            testTag = "SegmentedControl_ALL",
            onClick = { onTabSelected(AnalysisFilterTab.ALL) }
        )

        SegmentPill(
            text = stringResource(R.string.tab_strengths_format, strengthsCount),
            dotColor = PiaSuccessBadge,
            isSelected = selectedTab == AnalysisFilterTab.STRENGTHS,
            testTag = "SegmentedControl_STRENGTHS",
            onClick = { onTabSelected(AnalysisFilterTab.STRENGTHS) }
        )

        SegmentPill(
            text = stringResource(R.string.tab_improvements_format, improvementsCount),
            dotColor = PiaTacticalOrange,
            isSelected = selectedTab == AnalysisFilterTab.IMPROVEMENTS,
            testTag = "SegmentedControl_IMPROVEMENTS",
            onClick = { onTabSelected(AnalysisFilterTab.IMPROVEMENTS) }
        )
    }
}

@Composable
private fun SegmentPill(
    text: String,
    dotColor: Color?,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    val pillModifier = if (isSelected) {
        Modifier
            .shadow(2.dp, RoundedCornerShape(8.dp))
            .background(PiaSurface, RoundedCornerShape(8.dp))
    } else {
        Modifier
            .background(Color.Transparent, RoundedCornerShape(8.dp))
    }

    Row(
        modifier = pillModifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (dotColor != null) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(dotColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) PiaNavy else PiaTextMuted
            )
        )
    }
}
