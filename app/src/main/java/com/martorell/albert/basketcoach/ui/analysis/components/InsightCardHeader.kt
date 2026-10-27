package com.martorell.albert.basketcoach.ui.analysis.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martorell.albert.basketcoach.domain.model.InsightType
import com.martorell.albert.basketcoach.ui.analysis.data.AnalysisPreviewData
import com.martorell.albert.basketcoach.ui.analysis.model.InsightCardUiModel
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import com.martorell.albert.basketcoach.ui.theme.BcDevicePreview
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview
import com.martorell.albert.basketcoach.ui.theme.PiaSuccessGreen
import com.martorell.albert.basketcoach.ui.theme.PiaTacticalOrange
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted

/**
 * Header row for an insight card containing the category badge and context tag.
 *
 * @param card Presentation model containing category and context information.
 * @param modifier Composable modifier for styling and layout adjustments.
 */
@Composable
fun InsightCardHeader(
    card: InsightCardUiModel,
    modifier: Modifier = Modifier
) {
    val isImprovement = card.type == InsightType.IMPROVEMENT
    val badgeBgColor = if (isImprovement) {
        PiaTacticalOrange.copy(alpha = 0.1f)
    } else {
        PiaSuccessGreen.copy(alpha = 0.1f)
    }
    val badgeTextColor = if (isImprovement) PiaTacticalOrange else PiaSuccessGreen

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(9999.dp))
                .background(badgeBgColor)
                .padding(horizontal = 10.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(badgeTextColor)
            )
            Text(
                text = card.categoryBadgeText,
                color = badgeTextColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Text(
            text = card.contextTag,
            color = PiaTextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@BcThemePreview
@BcDevicePreview
@Composable
private fun InsightCardHeaderStrengthPreview() {
    BasketCoachTheme {
        InsightCardHeader(
            card = AnalysisPreviewData.allInsights.first { it.type == InsightType.STRENGTH }
        )
    }
}

@BcThemePreview
@BcDevicePreview
@Composable
private fun InsightCardHeaderImprovementPreview() {
    BasketCoachTheme {
        InsightCardHeader(
            card = AnalysisPreviewData.allInsights.first { it.type == InsightType.IMPROVEMENT }
        )
    }
}
