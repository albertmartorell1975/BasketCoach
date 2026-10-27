package com.martorell.albert.basketcoach.ui.dashboard.components.kpi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted
import com.martorell.albert.basketcoach.ui.theme.PiaTextPrimary

/**
 * Key performance indicator card displaying game control and possession security.
 *
 * Visualizes the Assist-to-Turnover (AST/TO) ratio headline, assists and turnovers
 * volume badges, and a granular AST vs TO breakdown footer.
 *
 * @param kpis Aggregated KPI metrics model.
 * @param modifier Layout modifier applied to the card.
 */
@Composable
fun KpiCardGameControl(
    kpis: DashboardKpisUiModel,
    modifier: Modifier = Modifier
) {
    KpiCardBase(
        title = stringResource(R.string.kpi_gamecontrol_title),
        icon = Icons.Filled.SwapHoriz,
        iconTint = PiaNavy,
        iconBg = PiaNavy.copy(alpha = 0.1f),
        modifier = modifier
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
                    text = stringResource(R.string.kpi_stat_ast),
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
                    text = stringResource(R.string.kpi_stat_to),
                    fontSize = 12.sp,
                    color = PiaTextMuted
                )
            }
        }
    }
}

@BcThemePreview
@BcDevicePreview
@Composable
private fun KpiCardGameControlPreview() {
    BasketCoachTheme {
        KpiCardGameControl(kpis = Phase1PreviewData.kpis)
    }
}
