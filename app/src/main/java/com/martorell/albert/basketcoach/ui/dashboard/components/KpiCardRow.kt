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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.ui.dashboard.data.Phase1PreviewData
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardKpisUiModel
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import com.martorell.albert.basketcoach.ui.theme.BcDevicePreview
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview
import com.martorell.albert.basketcoach.ui.theme.PiaDangerRed
import com.martorell.albert.basketcoach.ui.theme.PiaDangerRedBg
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaOutline
import com.martorell.albert.basketcoach.ui.theme.PiaSurface
import com.martorell.albert.basketcoach.ui.theme.PiaSurfaceContainer
import com.martorell.albert.basketcoach.ui.theme.PiaSurfaceContainerLow
import com.martorell.albert.basketcoach.ui.theme.PiaTacticalOrange
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted
import com.martorell.albert.basketcoach.ui.theme.PiaTextPrimary

/**
 * Horizontal row of four key performance indicator (KPI) metric cards:
 * 1. Average Scoring (points vs opponent, net differential, progress bar).
 * 2. Rebounds & Differential (total, offensive/defensive breakdown, tactical orange border accent).
 * 3. Shooting Efficiency (eFG% headline, 2P/3P/FT mini stat boxes, navy progress bar).
 * 4. Game Control (AST/TO ratio headline, AST and TO volume pills).
 *
 * @param kpis KPI metrics model for Phase 1.
 * @param modifier Layout modifier applied to the row container.
 */
@Composable
fun KpiCardRow(
    kpis: DashboardKpisUiModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("kpi_card_row"),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        KpiCardScoring(
            kpis = kpis,
            modifier = Modifier.weight(1f)
        )
        KpiCardRebounds(
            kpis = kpis,
            modifier = Modifier.weight(1f)
        )
        KpiCardShooting(
            kpis = kpis,
            modifier = Modifier.weight(1f)
        )
        KpiCardGameControl(
            kpis = kpis,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun KpiCardScoring(
    kpis: DashboardKpisUiModel,
    modifier: Modifier = Modifier
) {
    KpiCardBase(
        title = stringResource(R.string.kpi_scoring_title),
        icon = Icons.Filled.SportsBasketball,
        iconTint = PiaNavy,
        iconBg = PiaNavy.copy(alpha = 0.05f),
        modifier = modifier.testTag("kpi_card_scoring")
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = String.format("%.1f", kpis.scoring.averageScore),
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PiaTextPrimary,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = stringResource(R.string.kpi_scoring_opp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = PiaTextMuted,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(PiaDangerRedBg)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = stringResource(R.string.kpi_scoring_net_diff),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PiaDangerRed
                )
            }
            Text(
                text = stringResource(R.string.kpi_scoring_games),
                fontSize = 12.sp,
                color = PiaTextMuted
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(PiaSurfaceContainer)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(kpis.scoring.progressFraction)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(PiaDangerRed)
            )
        }
    }
}

@Composable
private fun KpiCardRebounds(
    kpis: DashboardKpisUiModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(PiaSurface)
            .border(1.dp, PiaOutline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .testTag("kpi_card_rebounds")
    ) {
        Box(
            modifier = Modifier
                .width(6.dp)
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
                .background(PiaTacticalOrange)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.kpi_rebounds_title),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PiaTextMuted,
                    letterSpacing = 0.5.sp
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PiaTacticalOrange.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Height,
                        contentDescription = null,
                        tint = PiaTacticalOrange,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = String.format("%.1f", kpis.rebounds.averageRebounds),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PiaTextPrimary,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = stringResource(R.string.kpi_rebounds_opp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = PiaTextMuted,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(PiaDangerRedBg)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = stringResource(R.string.kpi_rebounds_negative_balance),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PiaDangerRed
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(PiaOutline.copy(alpha = 0.2f))
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = String.format("%.1f", kpis.rebounds.offensiveRebounds),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PiaTextPrimary
                    )
                    Text(
                        text = stringResource(R.string.kpi_rebounds_offensive),
                        fontSize = 12.sp,
                        color = PiaTextMuted
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = String.format("%.1f", kpis.rebounds.defensiveRebounds),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PiaTextPrimary
                    )
                    Text(
                        text = stringResource(R.string.kpi_rebounds_defensive),
                        fontSize = 12.sp,
                        color = PiaTextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun KpiCardShooting(
    kpis: DashboardKpisUiModel,
    modifier: Modifier = Modifier
) {
    KpiCardBase(
        title = stringResource(R.string.kpi_shooting_title),
        icon = Icons.Filled.Radar,
        iconTint = PiaNavy,
        iconBg = PiaNavy.copy(alpha = 0.05f),
        modifier = modifier.testTag("kpi_card_shooting")
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = String.format("%.1f%%", kpis.shooting.efgPercentage),
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PiaTextPrimary,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = stringResource(R.string.kpi_shooting_global),
                fontSize = 12.sp,
                color = PiaTextMuted,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MiniStatBox(
                label = "2P",
                value = String.format("%.1f%%", kpis.shooting.fg2Percentage),
                modifier = Modifier.weight(1f)
            )
            MiniStatBox(
                label = "3P",
                value = String.format("%.1f%%", kpis.shooting.fg3Percentage),
                modifier = Modifier.weight(1f)
            )
            MiniStatBox(
                label = "TL",
                value = String.format("%.1f%%", kpis.shooting.ftPercentage),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(PiaSurfaceContainer)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((kpis.shooting.efgPercentage / 100.0).toFloat())
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(PiaNavy)
            )
        }
    }
}

@Composable
private fun MiniStatBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(PiaSurfaceContainerLow)
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = PiaTextMuted
            )
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PiaTextPrimary
            )
        }
    }
}

@Composable
private fun KpiCardGameControl(
    kpis: DashboardKpisUiModel,
    modifier: Modifier = Modifier
) {
    KpiCardBase(
        title = stringResource(R.string.kpi_gamecontrol_title),
        icon = Icons.Filled.SwapHoriz,
        iconTint = PiaNavy,
        iconBg = PiaNavy.copy(alpha = 0.1f),
        modifier = modifier.testTag("kpi_card_game_control")
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = String.format("%.2f", kpis.gameControl.astToRatio),
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PiaTextPrimary,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = stringResource(R.string.kpi_gamecontrol_ratio),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = PiaTextMuted,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(PiaDangerRedBg)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = stringResource(R.string.kpi_gamecontrol_ast_pill),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PiaDangerRed
                )
            }
            Text(
                text = stringResource(R.string.kpi_gamecontrol_to_pill),
                fontSize = 12.sp,
                color = PiaTextMuted
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(PiaOutline.copy(alpha = 0.2f))
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = String.format("%.1f", kpis.gameControl.assistsPerGame),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PiaTextPrimary
                )
                Text(
                    text = "AST",
                    fontSize = 12.sp,
                    color = PiaTextMuted
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = String.format("%.1f", kpis.gameControl.turnoversPerGame),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PiaTextPrimary
                )
                Text(
                    text = "TO",
                    fontSize = 12.sp,
                    color = PiaTextMuted
                )
            }
        }
    }
}

@Composable
private fun KpiCardBase(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(PiaSurface)
            .border(1.dp, PiaOutline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PiaTextMuted,
                    letterSpacing = 0.5.sp
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            content()
        }
    }
}

@BcThemePreview
@BcDevicePreview
@Composable
private fun KpiCardRowPreview() {
    BasketCoachTheme {
        KpiCardRow(kpis = Phase1PreviewData.kpis)
    }
}

