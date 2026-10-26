package com.martorell.albert.basketcoach.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.ui.dashboard.data.Phase1PreviewData
import com.martorell.albert.basketcoach.ui.dashboard.model.RoundEvolutionUiModel
import com.martorell.albert.basketcoach.ui.dashboard.model.ScoringEvolutionUiModel
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import com.martorell.albert.basketcoach.ui.theme.BcDevicePreview
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaOutline
import com.martorell.albert.basketcoach.ui.theme.PiaSlate
import com.martorell.albert.basketcoach.ui.theme.PiaSuccessGreen
import com.martorell.albert.basketcoach.ui.theme.PiaSurface
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted
import com.martorell.albert.basketcoach.ui.theme.PiaTextPrimary

@Composable
fun Phase1EvolutionChart(
    evolution: ScoringEvolutionUiModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PiaSurface)
            .border(1.dp, PiaOutline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(20.dp)
            .testTag("phase1_evolution_chart")
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
                    Text(
                        text = stringResource(R.string.chart_title),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PiaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.chart_subtitle),
                        fontSize = 12.sp,
                        color = PiaTextMuted
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ChartLegendItem(
                        color = PiaSuccessGreen,
                        label = stringResource(R.string.chart_legend_win),
                        textColor = PiaSuccessGreen,
                        isBold = true
                    )
                    ChartLegendItem(
                        color = PiaNavy,
                        label = stringResource(R.string.chart_legend_team),
                        textColor = PiaTextPrimary,
                        isBold = false
                    )
                    ChartLegendItem(
                        color = PiaSlate,
                        label = stringResource(R.string.chart_legend_opp),
                        textColor = PiaTextMuted,
                        isBold = false
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(PiaOutline.copy(alpha = 0.2f))
            )

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                evolution.rounds.forEach { round ->
                    RoundBarColumn(
                        round = round,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(PiaOutline.copy(alpha = 0.2f))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                evolution.rounds.forEach { round ->
                    val labelText = if (round.isWin) "${round.roundLabel} (V)" else round.roundLabel
                    val labelColor = if (round.isWin) PiaSuccessGreen else PiaTextMuted
                    val labelWeight = if (round.isWin) FontWeight.Bold else FontWeight.SemiBold

                    Text(
                        text = labelText,
                        fontSize = 11.sp,
                        fontWeight = labelWeight,
                        color = labelColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun RoundBarColumn(
    round: RoundEvolutionUiModel,
    modifier: Modifier = Modifier
) {
    val maxScore = 90f
    val piaFraction = (round.piaScore / maxScore).coerceIn(0.1f, 1f)
    val rivalFraction = (round.rivalScore / maxScore).coerceIn(0.1f, 1f)
    val piaColor = if (round.isWin) PiaSuccessGreen else PiaNavy

    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        if (round.isWin && round.scoreBadge != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(PiaSuccessGreen)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .offset(y = (-4).dp)
            ) {
                Text(
                    text = round.scoreBadge,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 11.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Row(
            modifier = Modifier.height(150.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(10.dp)
                    .fillMaxHeight(piaFraction)
                    .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                    .background(piaColor)
            )

            Box(
                modifier = Modifier
                    .width(10.dp)
                    .fillMaxHeight(rivalFraction)
                    .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                    .background(PiaSlate)
            )
        }
    }
}

@Composable
private fun ChartLegendItem(
    color: Color,
    label: String,
    textColor: Color,
    isBold: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = textColor
        )
    }
}

@BcThemePreview
@BcDevicePreview
@Composable
private fun Phase1EvolutionChartPreview() {
    BasketCoachTheme {
        Phase1EvolutionChart(evolution = Phase1PreviewData.evolution)
    }
}

