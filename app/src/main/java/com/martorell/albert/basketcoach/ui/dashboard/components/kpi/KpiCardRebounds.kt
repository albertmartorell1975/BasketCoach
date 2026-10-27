package com.martorell.albert.basketcoach.ui.dashboard.components.kpi

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
import androidx.compose.material3.Icon
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
import com.martorell.albert.basketcoach.ui.theme.PiaOutline
import com.martorell.albert.basketcoach.ui.theme.PiaSurface
import com.martorell.albert.basketcoach.ui.theme.PiaTacticalOrange
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted
import com.martorell.albert.basketcoach.ui.theme.PiaTextPrimary

/**
 * Key performance indicator card displaying rebounding and balance metrics.
 *
 * Features a distinct 6dp Tactical Orange vertical accent bar along the right border,
 * average total rebounds, opponent comparison, negative balance badge, and an
 * offensive/defensive rebound volume footer breakdown.
 *
 * @param kpis Aggregated KPI metrics model.
 * @param modifier Layout modifier applied to the card.
 */
@Composable
fun KpiCardRebounds(
    kpis: DashboardKpisUiModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(PiaSurface)
            .border(1.dp, PiaOutline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
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

@BcThemePreview
@BcDevicePreview
@Composable
private fun KpiCardReboundsPreview() {
    BasketCoachTheme {
        KpiCardRebounds(kpis = Phase1PreviewData.kpis)
    }
}
