package com.martorell.albert.basketcoach.ui.analysis.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.domain.model.InsightType
import com.martorell.albert.basketcoach.domain.model.MetricSentiment
import com.martorell.albert.basketcoach.ui.analysis.data.AnalysisPreviewData
import com.martorell.albert.basketcoach.ui.analysis.model.InsightCardUiModel
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import com.martorell.albert.basketcoach.ui.theme.BcDevicePreview
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview
import com.martorell.albert.basketcoach.ui.theme.PiaDangerRed
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaOutline
import com.martorell.albert.basketcoach.ui.theme.PiaSuccessGreen
import com.martorell.albert.basketcoach.ui.theme.PiaSurface
import com.martorell.albert.basketcoach.ui.theme.PiaSurfaceContainerLow
import com.martorell.albert.basketcoach.ui.theme.PiaTacticalOrange
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted
import com.martorell.albert.basketcoach.ui.theme.PiaTextPrimary
import com.martorell.albert.basketcoach.ui.theme.PiaWhite

/**
 * Tactical card presenting an individual tactical insight.
 *
 * Renders category badge with status indicator dot, context tag, bold title, description,
 * an empirical metric panel with primary/secondary benchmarks, footer notes, and an optional
 * "Explorar al Xat" button for improvement cards.
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
    val badgeBgColor = if (isImprovement) {
        PiaTacticalOrange.copy(alpha = 0.1f)
    } else {
        PiaSuccessGreen.copy(alpha = 0.1f)
    }
    val badgeTextColor = if (isImprovement) PiaTacticalOrange else PiaSuccessGreen

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isImprovement) {
                            Icon(
                                imageVector = Icons.Filled.PriorityHigh,
                                contentDescription = stringResource(R.string.analysis_cd_warning_badge),
                                tint = PiaTacticalOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = card.footerNote,
                                color = PiaTacticalOrange,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Verified,
                                contentDescription = stringResource(R.string.analysis_cd_verified_badge),
                                tint = PiaSuccessGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = card.footerNote,
                                color = PiaTextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }

                    if (card.canExploreChat) {
                        Button(
                            onClick = { onExploreChat(card.exploreChatPrompt) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PiaNavy,
                                contentColor = PiaWhite
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
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
    }
}

@Composable
private fun EmpiricalMetricPanel(
    card: InsightCardUiModel,
    modifier: Modifier = Modifier
) {
    val subValueColor = when (card.metricSentiment) {
        MetricSentiment.POSITIVE -> PiaSuccessGreen
        MetricSentiment.NEGATIVE -> PiaDangerRed
        MetricSentiment.NEUTRAL -> PiaTextMuted
    }
    val primaryValueColor = if (card.type == InsightType.IMPROVEMENT) PiaTacticalOrange else PiaTextPrimary

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(PiaSurfaceContainerLow)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = card.primaryMetricLabel,
                    color = PiaTextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = card.primaryMetricValue,
                    color = primaryValueColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = card.primaryMetricSubValue,
                    color = subValueColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = card.secondaryMetricLabel,
                    color = PiaTextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = card.secondaryMetricValue,
                    color = if (card.type == InsightType.IMPROVEMENT) PiaTacticalOrange else PiaNavy,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = card.secondaryMetricTag,
                    color = PiaTextMuted,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
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
