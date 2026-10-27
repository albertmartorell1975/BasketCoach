package com.martorell.albert.basketcoach.ui.dashboard.components.kpi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Radar
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
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaSurfaceContainer
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted
import com.martorell.albert.basketcoach.ui.theme.PiaTextPrimary

/**
 * Key performance indicator card displaying shooting efficiency metrics.
 *
 * Shows overall effective Field Goal percentage (eFG%), 2P/3P/TL mini stat boxes,
 * and a navy progress bar corresponding to the eFG% metric.
 *
 * @param kpis Aggregated KPI metrics model.
 * @param modifier Layout modifier applied to the card.
 */
@Composable
fun KpiCardShooting(
    kpis: DashboardKpisUiModel,
    modifier: Modifier = Modifier
) {
    KpiCardBase(
        title = stringResource(R.string.kpi_shooting_title),
        icon = Icons.Filled.Radar,
        iconTint = PiaNavy,
        iconBg = PiaNavy.copy(alpha = 0.05f),
        modifier = modifier
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
                label = stringResource(R.string.kpi_stat_2p),
                value = String.format("%.1f%%", kpis.shooting.fg2Percentage),
                modifier = Modifier.weight(1f)
            )
            MiniStatBox(
                label = stringResource(R.string.kpi_stat_3p),
                value = String.format("%.1f%%", kpis.shooting.fg3Percentage),
                modifier = Modifier.weight(1f)
            )
            MiniStatBox(
                label = stringResource(R.string.kpi_stat_ft),
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

@BcThemePreview
@BcDevicePreview
@Composable
private fun KpiCardShootingPreview() {
    BasketCoachTheme {
        KpiCardShooting(kpis = Phase1PreviewData.kpis)
    }
}
