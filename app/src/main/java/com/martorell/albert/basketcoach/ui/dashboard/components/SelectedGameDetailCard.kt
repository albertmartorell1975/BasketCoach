package com.martorell.albert.basketcoach.ui.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.ui.dashboard.preview.DashboardPreviewData
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import com.martorell.albert.basketcoach.ui.theme.ComponentPreview
import com.martorell.albert.basketcoach.ui.theme.LossRed
import com.martorell.albert.basketcoach.ui.theme.WinGreen

/**
 * Detailed box score metrics inspector card for the currently selected game.
 *
 * Breaks down four core tactical categories:
 * - Rebounds: offensive, defensive, and total.
 * - Playmaking: assists, turnovers, and AST/TO ratio.
 * - Shooting: 2PT% and 3PT%.
 * - Discipline: personal fouls committed and FT%.
 *
 * @param game The selected [Game] whose team statistics will be displayed.
 * @param modifier Modifier applied to the outer [Card].
 */
@Composable
fun SelectedGameDetailCard(
    game: Game,
    modifier: Modifier = Modifier
) {
    val stats = game.teamStats
    val venueText = if (game.isHomeGame) stringResource(R.string.home_title_case) else stringResource(R.string.away_title_case)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("SelectedGameDetailCard"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
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
                Column {
                    Text(
                        text = stringResource(R.string.round_box_score_format, game.round),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.vs_opponent_venue_format, game.opponentName, venueText),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = stringResource(R.string.score_format, game.teamScore, game.opponentScore),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (game.isWin) WinGreen else LossRed
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricColumn(
                    title = stringResource(R.string.metric_rebounds),
                    primaryValue = stringResource(R.string.rebounds_detail_format, stats.offensiveRebounds, stats.defensiveRebounds),
                    secondaryValue = stringResource(R.string.rebounds_total_format, stats.totalRebounds)
                )
                MetricColumn(
                    title = stringResource(R.string.metric_playmaking),
                    primaryValue = stringResource(R.string.ast_to_counts_format, stats.assists, stats.turnovers),
                    secondaryValue = stringResource(R.string.ast_to_ratio_format, stats.astToRatio)
                )
                MetricColumn(
                    title = stringResource(R.string.metric_shooting),
                    primaryValue = stringResource(R.string.fg2_pct_format, stats.fg2Pct),
                    secondaryValue = stringResource(R.string.fg3_pct_format, stats.fg3Pct)
                )
                MetricColumn(
                    title = stringResource(R.string.metric_discipline),
                    primaryValue = stringResource(R.string.fouls_count_format, stats.personalFouls),
                    secondaryValue = stringResource(R.string.ft_pct_format, stats.ftPct)
                )
            }
        }
    }
}

@Composable
private fun MetricColumn(
    title: String,
    primaryValue: String,
    secondaryValue: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = primaryValue,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = secondaryValue,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@ComponentPreview
@Composable
private fun SelectedGameDetailCardPreview() {
    BasketCoachTheme {
        SelectedGameDetailCard(
            game = DashboardPreviewData.sampleGames[2]
        )
    }
}
