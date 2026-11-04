package com.martorell.albert.basketcoach.ui.analysis.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.domain.model.InsightType
import com.martorell.albert.basketcoach.ui.analysis.data.AnalysisPreviewData
import com.martorell.albert.basketcoach.ui.analysis.model.InsightCardUiModel
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import com.martorell.albert.basketcoach.ui.theme.BcDevicePreview
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaSuccessGreen
import com.martorell.albert.basketcoach.ui.theme.PiaTacticalOrange
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted
import com.martorell.albert.basketcoach.ui.theme.PiaWhite

/**
 * Footer row for an insight card containing contextual status notes and an optional chat exploration action.
 *
 * @param card Presentation model containing footer metadata and chat navigation state.
 * @param onExploreChat Callback invoked with exploration prompt when the chat button is tapped.
 * @param modifier Composable modifier for styling and layout adjustments.
 */
@Composable
fun InsightCardFooter(
    card: InsightCardUiModel,
    onExploreChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isImprovement = card.type == InsightType.IMPROVEMENT
    val footerColor = if (isImprovement) PiaTacticalOrange else PiaSuccessGreen

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (card.footerNote.isNotBlank()) {
            Text(
                text = card.footerNote,
                color = footerColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 16.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (card.canExploreChat) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { onExploreChat(card.exploreChatPrompt) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PiaNavy,
                        contentColor = PiaWhite
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Psychology,
                            contentDescription = null,
                            tint = PiaWhite,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = stringResource(R.string.analysis_btn_explore_chat),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@BcThemePreview
@BcDevicePreview
@Composable
private fun InsightCardFooterStrengthPreview() {
    BasketCoachTheme {
        InsightCardFooter(
            card = AnalysisPreviewData.allInsights.first { it.type == InsightType.STRENGTH },
            onExploreChat = {}
        )
    }
}

@BcThemePreview
@BcDevicePreview
@Composable
private fun InsightCardFooterImprovementPreview() {
    BasketCoachTheme {
        InsightCardFooter(
            card = AnalysisPreviewData.allInsights.first { it.type == InsightType.IMPROVEMENT },
            onExploreChat = {}
        )
    }
}
