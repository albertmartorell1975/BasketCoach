package com.martorell.albert.basketcoach.ui.analysis.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martorell.albert.basketcoach.domain.model.InsightType
import com.martorell.albert.basketcoach.domain.model.MetricSentiment
import com.martorell.albert.basketcoach.ui.analysis.data.AnalysisPreviewData
import com.martorell.albert.basketcoach.ui.analysis.model.InsightCardUiModel
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import com.martorell.albert.basketcoach.ui.theme.BcDevicePreview
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview
import com.martorell.albert.basketcoach.ui.theme.PiaDangerRed
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaSuccessGreen
import com.martorell.albert.basketcoach.ui.theme.PiaSurfaceContainerLow
import com.martorell.albert.basketcoach.ui.theme.PiaTacticalOrange
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted
import com.martorell.albert.basketcoach.ui.theme.PiaTextPrimary

/**
 * Empirical metric panel displaying the primary and secondary statistical indicators of an insight card.
 *
 * @param card Presentation model containing empirical metric data.
 * @param modifier Composable modifier for styling and layout adjustments.
 */
@Composable
fun EmpiricalMetricPanel(
    card: InsightCardUiModel,
    modifier: Modifier = Modifier
) {
    val subValueColor = when (card.metricSentiment) {
        MetricSentiment.POSITIVE -> PiaSuccessGreen
        MetricSentiment.NEGATIVE -> PiaDangerRed
        MetricSentiment.NEUTRAL -> PiaTextMuted
    }
    val primaryValueColor = if (card.type == InsightType.IMPROVEMENT) PiaTacticalOrange else PiaTextPrimary
    val secondaryValueColor = if (card.type == InsightType.IMPROVEMENT) PiaTacticalOrange else PiaNavy

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(PiaSurfaceContainerLow)
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = card.primaryMetricLabel,
                    color = PiaTextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = card.secondaryMetricLabel,
                    color = PiaTextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = card.primaryMetricValue,
                    color = primaryValueColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = card.secondaryMetricValue,
                    color = secondaryValueColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                )
            }

            if (card.primaryMetricSubValue.isNotBlank()) {
                Text(
                    text = card.primaryMetricSubValue,
                    color = subValueColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@BcThemePreview
@BcDevicePreview
@Composable
private fun EmpiricalMetricPanelStrengthPreview() {
    BasketCoachTheme {
        EmpiricalMetricPanel(
            card = AnalysisPreviewData.allInsights.first { it.type == InsightType.STRENGTH }
        )
    }
}

@BcThemePreview
@BcDevicePreview
@Composable
private fun EmpiricalMetricPanelImprovementPreview() {
    BasketCoachTheme {
        EmpiricalMetricPanel(
            card = AnalysisPreviewData.allInsights.first { it.type == InsightType.IMPROVEMENT }
        )
    }
}
