package com.martorell.albert.basketcoach.ui.analysis.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.martorell.albert.basketcoach.ui.theme.PiaOutline
import com.martorell.albert.basketcoach.ui.theme.PiaSurface
import com.martorell.albert.basketcoach.ui.theme.PiaTacticalOrange
import com.martorell.albert.basketcoach.ui.theme.PiaTextPrimary

/**
 * Tactical card presenting an individual tactical insight.
 *
 * Coordinates the header ([InsightCardHeader]), contextual body (title and description),
 * statistical indicators ([EmpiricalMetricPanel]), and action footer ([InsightCardFooter]).
 *
 * @param card Presentation model containing all card properties.
 * @param onExploreChat Callback invoked with the card's exploration prompt when the chat button is tapped.
 * @param modifier Composable modifier for styling and layout adjustments.
 */
@Composable
fun InsightCard(
    card: InsightCardUiModel,
    onExploreChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isImprovement = card.type == InsightType.IMPROVEMENT
    val borderColor = if (isImprovement) {
        PiaTacticalOrange.copy(alpha = 0.3f)
    } else {
        PiaOutline.copy(alpha = 0.3f)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PiaSurface)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                InsightCardHeader(card = card)

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = card.title,
                    color = PiaTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = card.description,
                    color = PiaTextPrimary.copy(alpha = 0.85f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(color = PiaOutline.copy(alpha = 0.2f), thickness = 1.dp)

                Spacer(modifier = Modifier.height(12.dp))

                EmpiricalMetricPanel(card = card)

                Spacer(modifier = Modifier.height(12.dp))

                InsightCardFooter(
                    card = card,
                    onExploreChat = onExploreChat
                )
            }
        }
    }
}

@BcThemePreview
@BcDevicePreview
@Composable
private fun InsightCardStrengthPreview() {
    BasketCoachTheme {
        InsightCard(
            card = AnalysisPreviewData.allInsights.first { it.type == InsightType.STRENGTH },
            onExploreChat = {}
        )
    }
}

@BcThemePreview
@BcDevicePreview
@Composable
private fun InsightCardImprovementPreview() {
    BasketCoachTheme {
        InsightCard(
            card = AnalysisPreviewData.allInsights.first { it.type == InsightType.IMPROVEMENT },
            onExploreChat = {}
        )
    }
}
