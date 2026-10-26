package com.martorell.albert.basketcoach.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.ui.theme.PiaDangerContainer
import com.martorell.albert.basketcoach.ui.theme.PiaDangerRed
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaOutline
import com.martorell.albert.basketcoach.ui.theme.PiaSuccessBadge
import com.martorell.albert.basketcoach.ui.theme.PiaSuccessContainer
import com.martorell.albert.basketcoach.ui.theme.PiaSuccessGreen
import com.martorell.albert.basketcoach.ui.theme.PiaSurface
import com.martorell.albert.basketcoach.ui.theme.PiaSurfaceVariant
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted
import com.martorell.albert.basketcoach.ui.theme.PiaTextPrimary

/**
 * Consolidated 14-match statistical table for Phase 1.
 *
 * Horizontally scrollable table with 16 statistical columns per game.
 * Victory rows are highlighted with a subtle green tint and badge.
 *
 * @param games The 14 Phase 1 games to display.
 * @param modifier Modifier applied to the outer [Card].
 */
@Composable
fun Phase1MatchesTable(
    games: List<Game>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("Phase1MatchesTable"),
        colors = CardDefaults.cardColors(containerColor = PiaSurface),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(PiaOutline.copy(alpha = 0.5f))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.table_title),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PiaNavy,
                            fontSize = 16.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.table_subtitle),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PiaTextMuted,
                            fontSize = 12.sp
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .background(PiaSurfaceVariant, RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.table_processed_badge),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PiaNavy,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Horizontally scrollable table container
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, PiaOutline.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                    .horizontalScroll(scrollState)
            ) {
                // Table Header Row
                Row(
                    modifier = Modifier
                        .background(PiaSurfaceVariant)
                        .height(38.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TableCell(text = stringResource(R.string.th_round), width = 65.dp, isHeader = true)
                    TableCell(text = stringResource(R.string.th_match), width = 175.dp, isHeader = true, align = TextAlign.Start)
                    TableCell(text = stringResource(R.string.th_score), width = 95.dp, isHeader = true)
                    TableCell(text = stringResource(R.string.th_efg), width = 70.dp, isHeader = true)
                    TableCell(text = stringResource(R.string.th_2p), width = 65.dp, isHeader = true)
                    TableCell(text = stringResource(R.string.th_3p), width = 65.dp, isHeader = true)
                    TableCell(text = stringResource(R.string.th_ft), width = 65.dp, isHeader = true)
                    TableCell(text = stringResource(R.string.th_reb), width = 100.dp, isHeader = true)
                    TableCell(text = stringResource(R.string.th_ast), width = 55.dp, isHeader = true)
                    TableCell(text = stringResource(R.string.th_to), width = 55.dp, isHeader = true)
                    TableCell(text = stringResource(R.string.th_ast_to), width = 70.dp, isHeader = true)
                    TableCell(text = stringResource(R.string.th_rob), width = 55.dp, isHeader = true)
                    TableCell(text = stringResource(R.string.th_tap), width = 55.dp, isHeader = true)
                    TableCell(text = stringResource(R.string.th_pf), width = 55.dp, isHeader = true)
                    TableCell(text = stringResource(R.string.th_second_chance), width = 80.dp, isHeader = true)
                    TableCell(text = stringResource(R.string.th_counterattack), width = 80.dp, isHeader = true)
                }

                // Match Rows
                games.forEachIndexed { index, game ->
                    val isWin = game.isWin
                    val rowBg = if (isWin) {
                        PiaSuccessContainer.copy(alpha = 0.35f)
                    } else {
                        if (index % 2 == 1) PiaSurfaceVariant.copy(alpha = 0.35f) else PiaSurface
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(rowBg)
                            .height(44.dp)
                            .padding(horizontal = 8.dp)
                            .testTag("MatchRow_${game.round}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Round
                        TableCell(
                            text = "J${game.round}",
                            width = 65.dp,
                            isBold = isWin,
                            color = if (isWin) PiaSuccessGreen else PiaNavy
                        )

                        // 2. Match
                        TableCell(
                            text = "${game.homeTeam} - ${game.awayTeam}",
                            width = 175.dp,
                            align = TextAlign.Start,
                            isBold = isWin
                        )

                        // 3. Score Pill
                        Box(
                            modifier = Modifier
                                .width(95.dp)
                                .padding(horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val scoreBg = if (isWin) PiaSuccessContainer else PiaDangerContainer
                            val scoreColor = if (isWin) PiaSuccessGreen else PiaDangerRed
                            val prefix = if (isWin) "W" else "L"

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(scoreBg)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "$prefix ${game.finalScore.homePoints}-${game.finalScore.awayPoints}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = scoreColor,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        val stats = game.teamStats

                        // 4. eFG%
                        TableCell(text = "%.1f%%".format(stats.efgPct), width = 70.dp)

                        // 5. 2P%
                        TableCell(text = "%.1f%%".format(stats.fg2Pct), width = 65.dp)

                        // 6. 3P%
                        TableCell(text = "%.1f%%".format(stats.fg3Pct), width = 65.dp)

                        // 7. TL%
                        TableCell(text = "%.1f%%".format(stats.ftPct), width = 65.dp)

                        // 8. REB (O/D)
                        TableCell(
                            text = "${stats.totalRebounds} (${stats.offensiveRebounds}/${stats.defensiveRebounds})",
                            width = 100.dp
                        )

                        // 9. AST
                        TableCell(text = "${stats.assists}", width = 55.dp)

                        // 10. TO
                        TableCell(text = "${stats.turnovers}", width = 55.dp)

                        // 11. AST/TO
                        TableCell(text = "%.1f".format(stats.astToRatio), width = 70.dp)

                        // 12. ROB
                        TableCell(text = "${stats.steals}", width = 55.dp)

                        // 13. TAP
                        TableCell(text = "${stats.blocks}", width = 55.dp)

                        // 14. PF
                        TableCell(text = "${stats.personalFouls}", width = 55.dp)

                        // 15. 2A OPCIÓ
                        TableCell(text = "${stats.secondChancePoints}", width = 80.dp)

                        // 16. C. ATAC
                        TableCell(text = "${stats.fastBreakPoints}", width = 80.dp)
                    }

                    if (index < games.size - 1) {
                        HorizontalDivider(color = PiaOutline.copy(alpha = 0.2f), thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun TableCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    isHeader: Boolean = false,
    align: TextAlign = TextAlign.Center,
    isBold: Boolean = false,
    color: androidx.compose.ui.graphics.Color? = null
) {
    Text(
        text = text,
        modifier = Modifier.width(width),
        textAlign = align,
        style = if (isHeader) {
            MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PiaNavy,
                fontSize = 10.sp,
                letterSpacing = 0.4.sp
            )
        } else {
            MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                color = color ?: PiaTextPrimary,
                fontSize = 11.sp
            )
        },
        maxLines = 1
    )
}
