package com.martorell.albert.basketcoach.ui.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.InsightCategory
import com.martorell.albert.basketcoach.ui.dashboard.preview.DashboardPreviewData
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import com.martorell.albert.basketcoach.ui.theme.ComponentPreview
import com.martorell.albert.basketcoach.ui.theme.LossRed
import com.martorell.albert.basketcoach.ui.theme.PiaAmber
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaPrimaryBlue
import com.martorell.albert.basketcoach.ui.theme.WinGreen

/**
 * Card composable presenting an AI tactical insight extracted from Phase 1 game analysis.
 *
 * Visualizes the insight category via color-coded chips (e.g., DEFENSE, OFFENSE, SYNERGY, GENERAL),
 * insight headline, explanatory tactical context, and related game round indicator chips.
 *
 * @param insight The [Insight] domain model to render.
 * @param modifier Modifier applied to the outer [Card].
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InsightCard(
    insight: Insight,
    modifier: Modifier = Modifier
) {
    val categoryColor = when (insight.category) {
        InsightCategory.DEFENSE -> LossRed
        InsightCategory.OFFENSE -> WinGreen
        InsightCategory.SYNERGY -> PiaPrimaryBlue
        InsightCategory.GENERAL -> PiaAmber
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("InsightCard_${insight.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = categoryColor.copy(alpha = 0.15f),
                    modifier = Modifier.testTag("InsightCategoryChip_${insight.category.name}")
                ) {
                    Text(
                        text = insight.category.name,
                        color = categoryColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = insight.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = insight.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (insight.relatedGameRounds.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.related_games_label),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        insight.relatedGameRounds.forEach { round ->
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = stringResource(R.string.round_chip_format, round),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PiaNavy,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@ComponentPreview
@Composable
private fun InsightCardDefensePreview() {
    BasketCoachTheme {
        InsightCard(
            insight = DashboardPreviewData.sampleInsights[0]
        )
    }
}

@ComponentPreview
@Composable
private fun InsightCardOffensePreview() {
    BasketCoachTheme {
        InsightCard(
            insight = DashboardPreviewData.sampleInsights[1]
        )
    }
}
