package com.martorell.albert.basketcoach.ui.dashboard.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.ui.dashboard.data.Phase1PreviewData
import com.martorell.albert.basketcoach.ui.dashboard.model.MatchRowUiModel
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import com.martorell.albert.basketcoach.ui.theme.BcDevicePreview
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview
import com.martorell.albert.basketcoach.ui.theme.PiaDangerRed
import com.martorell.albert.basketcoach.ui.theme.PiaDangerRedBg
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaOutline
import com.martorell.albert.basketcoach.ui.theme.PiaSuccessGreen
import com.martorell.albert.basketcoach.ui.theme.PiaSuccessGreenBg
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted
import com.martorell.albert.basketcoach.ui.theme.PiaTextPrimary
import java.util.Locale

/**
 * Data row rendering a single match's statistics across 16 aligned columns.
 *
 * Applies subtle green background tinting for victories, score badge color coding,
 * Pia team name emphasis based on [MatchRowUiModel.isPiaHome], and turnover alert highlights.
 *
 * @param match Immutable UI model containing statistics for a single round.
 * @param modifier Layout modifier applied to the row container.
 */
@Composable
internal fun TableDataRow(
    match: MatchRowUiModel,
    modifier: Modifier = Modifier
) {
    val rowBg = if (match.isWin) PiaSuccessGreen.copy(alpha = 0.04f) else Color.Transparent
    val roundTextColor = if (match.isWin) PiaSuccessGreen else PiaNavy

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(rowBg)
                .padding(vertical = 10.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.round_label_format, match.roundNumber),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = roundTextColor,
                modifier = Modifier.width(TableColumnWidths.round)
            )

            Column(modifier = Modifier.width(TableColumnWidths.match)) {
                val homeIsPia = match.isPiaHome
                Text(
                    text = match.homeTeam,
                    fontSize = 12.sp,
                    fontWeight = if (homeIsPia) FontWeight.Bold else FontWeight.Normal,
                    color = if (homeIsPia) PiaTextPrimary else PiaTextMuted
                )
                val awayIsPia = !match.isPiaHome
                Text(
                    text = match.awayTeam,
                    fontSize = 12.sp,
                    fontWeight = if (awayIsPia) FontWeight.Bold else FontWeight.Normal,
                    color = if (awayIsPia) PiaTextPrimary else PiaTextMuted
                )
            }

            Box(
                modifier = Modifier.width(TableColumnWidths.score),
                contentAlignment = Alignment.Center
            ) {
                val scoreBg = if (match.isWin) PiaSuccessGreenBg else PiaDangerRedBg
                val scoreTextColor = if (match.isWin) PiaSuccessGreen else PiaDangerRed
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(scoreBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = stringResource(R.string.table_score_format, match.homePoints, match.awayPoints),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = scoreTextColor
                    )
                }
            }

            val efgColor = if (match.isWin) PiaSuccessGreen else PiaNavy
            Text(
                text = stringResource(R.string.table_pct_format, match.efgPct),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = efgColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(TableColumnWidths.efg)
            )

            Text(
                text = stringResource(R.string.table_pct_format, match.fg2Pct),
                fontSize = 12.sp,
                color = PiaTextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(TableColumnWidths.fg2)
            )

            Text(
                text = stringResource(R.string.table_pct_format, match.fg3Pct),
                fontSize = 12.sp,
                color = PiaTextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(TableColumnWidths.fg3)
            )

            Text(
                text = stringResource(R.string.table_pct_format, match.ftPct),
                fontSize = 12.sp,
                color = PiaTextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(TableColumnWidths.ft)
            )

            Text(
                text = stringResource(
                    R.string.table_rebounds_format,
                    match.totalRebounds,
                    match.offensiveRebounds,
                    match.defensiveRebounds
                ),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (match.isWin) PiaSuccessGreen else PiaTextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(TableColumnWidths.reb)
            )

            Text(
                text = match.ast.toString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (match.isWin) PiaSuccessGreen else PiaTextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(TableColumnWidths.ast)
            )

            val toColor = if (match.to >= 16) PiaDangerRed else PiaTextPrimary
            val toWeight = if (match.to >= 16) FontWeight.Bold else FontWeight.Medium
            Text(
                text = match.to.toString(),
                fontSize = 12.sp,
                fontWeight = toWeight,
                color = toColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(TableColumnWidths.to)
            )

            val ratioColor = if (match.isWin) PiaSuccessGreen else if (match.to >= 16) PiaDangerRed else PiaTextMuted
            val ratioWeight = if (match.isWin) FontWeight.Bold else FontWeight.Medium
            Text(
                text = String.format(Locale.US, "%.2f", match.astTo),
                fontSize = 12.sp,
                fontWeight = ratioWeight,
                color = ratioColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(TableColumnWidths.astTo)
            )

            Text(
                text = match.stl.toString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = PiaTextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(TableColumnWidths.stl)
            )

            Text(
                text = match.blk.toString(),
                fontSize = 12.sp,
                color = PiaTextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(TableColumnWidths.blk)
            )

            Text(
                text = stringResource(R.string.table_pf_format, match.personalFouls),
                fontSize = 12.sp,
                color = PiaTextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(TableColumnWidths.pf)
            )

            Text(
                text = stringResource(
                    R.string.table_pts_vs_format,
                    match.secondChancePoints,
                    match.opponentSecondChancePoints
                ),
                fontSize = 12.sp,
                color = PiaTextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(TableColumnWidths.secondChance)
            )

            Text(
                text = stringResource(
                    R.string.table_pts_vs_format,
                    match.fastBreakPoints,
                    match.opponentFastBreakPoints
                ),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = PiaTextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(TableColumnWidths.fastBreak)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(PiaOutline.copy(alpha = 0.15f))
        )
    }
}

@BcThemePreview
@BcDevicePreview
@Composable
private fun TableDataRowWinPreview() {
    BasketCoachTheme {
        TableDataRow(match = Phase1PreviewData.matches[2]) // J3 win
    }
}

@BcThemePreview
@BcDevicePreview
@Composable
private fun TableDataRowLossPreview() {
    BasketCoachTheme {
        TableDataRow(match = Phase1PreviewData.matches[0]) // J1 loss
    }
}
