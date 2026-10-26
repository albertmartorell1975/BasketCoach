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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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
import com.martorell.albert.basketcoach.ui.theme.PiaSurface
import com.martorell.albert.basketcoach.ui.theme.PiaSurfaceContainerLow
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted
import com.martorell.albert.basketcoach.ui.theme.PiaTextPrimary

@Composable
fun Phase1MatchesTable(
    matches: List<MatchRowUiModel>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PiaSurface)
            .border(1.dp, PiaOutline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(20.dp)
            .testTag("phase1_matches_table")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.table_title),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PiaTextPrimary,
                            letterSpacing = (-0.3).sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(percent = 50))
                                .background(PiaNavy.copy(alpha = 0.1f))
                                .padding(horizontal = 10.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.table_processed_badge),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PiaNavy
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.table_subtitle),
                        fontSize = 12.sp,
                        color = PiaTextMuted
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PiaSuccessGreenBg)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(PiaSuccessGreen)
                            )
                            Text(
                                text = stringResource(R.string.table_legend_wins),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PiaSuccessGreen
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PiaDangerRedBg)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(PiaDangerRed)
                            )
                            Text(
                                text = stringResource(R.string.table_legend_losses),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PiaDangerRed
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(PiaOutline.copy(alpha = 0.2f))
            )

            Spacer(modifier = Modifier.height(12.dp))

            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
            ) {
                TableHeaderRow()

                matches.forEach { match ->
                    TableDataRow(match = match)
                }
            }
        }
    }
}

@Composable
private fun TableHeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(PiaSurfaceContainerLow)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderCell(text = stringResource(R.string.table_col_round), width = 55.dp, textAlign = TextAlign.Start)
        HeaderCell(text = stringResource(R.string.table_col_match), width = 160.dp, textAlign = TextAlign.Start)
        HeaderCell(text = stringResource(R.string.table_col_score), width = 90.dp, textAlign = TextAlign.Center)
        HeaderCell(text = stringResource(R.string.table_col_efg), width = 65.dp, textAlign = TextAlign.Center, isPrimary = true)
        HeaderCell(text = stringResource(R.string.table_col_2p), width = 55.dp, textAlign = TextAlign.Center)
        HeaderCell(text = stringResource(R.string.table_col_3p), width = 55.dp, textAlign = TextAlign.Center)
        HeaderCell(text = stringResource(R.string.table_col_ft), width = 55.dp, textAlign = TextAlign.Center)
        HeaderCell(text = stringResource(R.string.table_col_reb), width = 90.dp, textAlign = TextAlign.Center)
        HeaderCell(text = stringResource(R.string.table_col_ast), width = 45.dp, textAlign = TextAlign.Center)
        HeaderCell(text = stringResource(R.string.table_col_to), width = 45.dp, textAlign = TextAlign.Center)
        HeaderCell(text = stringResource(R.string.table_col_ast_to), width = 60.dp, textAlign = TextAlign.Center)
        HeaderCell(text = stringResource(R.string.table_col_stl), width = 45.dp, textAlign = TextAlign.Center)
        HeaderCell(text = stringResource(R.string.table_col_blk), width = 45.dp, textAlign = TextAlign.Center)
        HeaderCell(text = stringResource(R.string.table_col_pf), width = 60.dp, textAlign = TextAlign.Center)
        HeaderCell(text = stringResource(R.string.table_col_second_chance), width = 105.dp, textAlign = TextAlign.Center)
        HeaderCell(text = stringResource(R.string.table_col_fast_break), width = 105.dp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun HeaderCell(
    text: String,
    width: Dp,
    textAlign: TextAlign,
    isPrimary: Boolean = false
) {
    Text(
        text = text,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp,
        color = if (isPrimary) PiaNavy else PiaTextMuted,
        textAlign = textAlign,
        modifier = Modifier.width(width)
    )
}

@Composable
private fun TableDataRow(match: MatchRowUiModel) {
    val rowBg = if (match.isWin) PiaSuccessGreen.copy(alpha = 0.04f) else Color.Transparent
    val roundTextColor = if (match.isWin) PiaSuccessGreen else PiaNavy

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBg)
            .padding(vertical = 10.dp, horizontal = 8.dp)
            .testTag("match_row_${match.roundLabel}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = match.roundLabel,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = roundTextColor,
            modifier = Modifier.width(55.dp)
        )

        Column(modifier = Modifier.width(160.dp)) {
            val homeIsPia = match.homeTeam.contains("Pia")
            Text(
                text = match.homeTeam,
                fontSize = 12.sp,
                fontWeight = if (homeIsPia) FontWeight.Bold else FontWeight.Normal,
                color = if (homeIsPia) PiaTextPrimary else PiaTextMuted
            )
            val awayIsPia = match.awayTeam.contains("Pia")
            Text(
                text = match.awayTeam,
                fontSize = 11.sp,
                fontWeight = if (awayIsPia) FontWeight.Bold else FontWeight.Normal,
                color = if (awayIsPia) PiaTextPrimary else PiaTextMuted
            )
        }

        Box(
            modifier = Modifier.width(90.dp),
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
                    text = match.scoreDisplay,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = scoreTextColor
                )
            }
        }

        val efgColor = if (match.isWin) PiaSuccessGreen else PiaNavy
        Text(
            text = match.efg,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = efgColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(65.dp)
        )

        Text(
            text = match.fg2,
            fontSize = 12.sp,
            color = PiaTextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(55.dp)
        )

        Text(
            text = match.fg3,
            fontSize = 12.sp,
            color = PiaTextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(55.dp)
        )

        Text(
            text = match.ft,
            fontSize = 12.sp,
            color = PiaTextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(55.dp)
        )

        Text(
            text = match.rebounds,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (match.isWin) PiaSuccessGreen else PiaTextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(90.dp)
        )

        Text(
            text = match.ast.toString(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (match.isWin) PiaSuccessGreen else PiaTextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(45.dp)
        )

        val toColor = if (match.to >= 16) PiaDangerRed else PiaTextPrimary
        val toWeight = if (match.to >= 16) FontWeight.Bold else FontWeight.Medium
        Text(
            text = match.to.toString(),
            fontSize = 12.sp,
            fontWeight = toWeight,
            color = toColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(45.dp)
        )

        val ratioColor = if (match.isWin) PiaSuccessGreen else if (match.to >= 16) PiaDangerRed else PiaTextMuted
        val ratioWeight = if (match.isWin) FontWeight.Bold else FontWeight.Medium
        Text(
            text = match.astTo,
            fontSize = 12.sp,
            fontWeight = ratioWeight,
            color = ratioColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(60.dp)
        )

        Text(
            text = match.stl.toString(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = PiaTextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(45.dp)
        )

        Text(
            text = match.blk.toString(),
            fontSize = 12.sp,
            color = PiaTextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(45.dp)
        )

        Text(
            text = match.pf,
            fontSize = 12.sp,
            color = PiaTextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(60.dp)
        )

        Text(
            text = match.secondChance,
            fontSize = 11.sp,
            color = PiaTextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(105.dp)
        )

        Text(
            text = match.fastBreak,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = PiaTextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(105.dp)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(PiaOutline.copy(alpha = 0.15f))
    )
}

@BcThemePreview
@BcDevicePreview
@Composable
private fun Phase1MatchesTablePreview() {
    BasketCoachTheme {
        Phase1MatchesTable(matches = Phase1PreviewData.matches)
    }
}

