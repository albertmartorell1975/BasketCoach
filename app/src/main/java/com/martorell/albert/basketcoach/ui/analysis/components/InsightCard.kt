package com.martorell.albert.basketcoach.ui.analysis.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.InsightType
import com.martorell.albert.basketcoach.domain.model.MetricSentiment
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaOutline
import com.martorell.albert.basketcoach.ui.theme.PiaSuccessBadge
import com.martorell.albert.basketcoach.ui.theme.PiaSuccessContainer
import com.martorell.albert.basketcoach.ui.theme.PiaSuccessGreen
import com.martorell.albert.basketcoach.ui.theme.PiaSurface
import com.martorell.albert.basketcoach.ui.theme.PiaSurfaceVariant
import com.martorell.albert.basketcoach.ui.theme.PiaTacticalOrange
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted
import com.martorell.albert.basketcoach.ui.theme.PiaTextPrimary

/**
 * Insight presentation card adhering to the tactical sketch layout.
 *
 * Supports both [InsightType.STRENGTH] and [InsightType.IMPROVEMENT] cards,
 * rendering context tags, detailed metrics panels, and Socratic chat exploration buttons.
 *
 * @param insight The tactical insight model to render.
 * @param onNavigateToChat Callback invoked when clicking "Explorar al Xat".
 * @param modifier Modifier applied to the outer [Card].
 */
@Composable
fun InsightCard(
    insight: Insight,
    onNavigateToChat: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val isStrength = insight.type == InsightType.STRENGTH
    val borderColor = if (isStrength) {
        PiaOutline.copy(alpha = 0.5f)
    } else {
        PiaTacticalOrange.copy(alpha = 0.45f)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("InsightCard_${insight.id}"),
        colors = CardDefaults.cardColors(containerColor = PiaSurface),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(borderColor),
            width = if (isStrength) 1.dp else 1.5.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Category Pill + Context Tag + Optional Priority
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Type Pill
                    val pillBg = if (isStrength) PiaSuccessContainer else PiaTacticalOrange.copy(alpha = 0.12f)
                    val pillColor = if (isStrength) PiaSuccessGreen else PiaTacticalOrange
                    val pillText = if (isStrength) stringResource(R.string.chip_strength) else stringResource(R.string.chip_improvement)

                    Box(
                        modifier = Modifier
                            .background(pillBg, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = pillText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = pillColor,
                                fontSize = 11.sp
                            )
                        )
                    }

                    // Priority Pill (for Improvements)
                    insight.priority?.let { priority ->
                        Box(
                            modifier = Modifier
                                .background(PiaTacticalOrange.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = priority,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PiaTacticalOrange,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                // Context Tag
                insight.contextTag?.let { tag ->
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PiaTextMuted,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Text(
                text = insight.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PiaNavy,
                    fontSize = 15.sp,
                    lineHeight = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = insight.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PiaTextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            )

            // Metric Panel
            insight.metric?.let { metric ->
                Spacer(modifier = Modifier.height(12.dp))
                MetricPanel(metric = metric)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer Section
            if (isStrength) {
                // Verified Strength Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = PiaSuccessBadge,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = insight.footerNote ?: stringResource(R.string.data_consolidated_badge),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PiaSuccessGreen,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    )
                }
            } else {
                // Improvement Footer Note + "Explorar al Xat" Action Button
                insight.footerNote?.let { note ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = PiaTacticalOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = note,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = PiaTacticalOrange,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Button(
                    onClick = { onNavigateToChat(insight.exploreChatPrompt) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .testTag("Btn_ExploreChat_${insight.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PiaNavy,
                        contentColor = androidx.compose.ui.graphics.Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.btn_explore_chat),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricPanel(
    metric: com.martorell.albert.basketcoach.domain.model.InsightMetric
) {
    val valueColor = when (metric.sentiment) {
        MetricSentiment.POSITIVE -> PiaSuccessBadge
        MetricSentiment.NEGATIVE -> PiaTacticalOrange
        MetricSentiment.NEUTRAL -> PiaNavy
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(PiaSurfaceVariant)
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = metric.label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PiaNavy,
                        fontSize = 12.sp
                    )
                )
                Text(
                    text = metric.value,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = valueColor,
                        fontSize = 13.sp
                    )
                )
            }

            if (!metric.comparison.isNullOrBlank() || !metric.delta.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                val subtitle = listOfNotNull<String>(metric.comparison, metric.delta).joinToString(" · ")
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = PiaTextMuted,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}
