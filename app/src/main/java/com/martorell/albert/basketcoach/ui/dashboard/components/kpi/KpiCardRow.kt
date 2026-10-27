package com.martorell.albert.basketcoach.ui.dashboard.components.kpi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.martorell.albert.basketcoach.ui.dashboard.data.Phase1PreviewData
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardKpisUiModel
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import com.martorell.albert.basketcoach.ui.theme.BcDevicePreview
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview

/**
 * Horizontal row of four key performance indicator (KPI) metric cards:
 * 1. [KpiCardScoring] — Average points, opponent comparison, net differential, progress bar.
 * 2. [KpiCardRebounds] — Total rebounds, differential, tactical orange border accent.
 * 3. [KpiCardShooting] — Shooting efficiency (eFG%), 2P/3P/TL mini stat boxes, navy progress bar.
 * 4. [KpiCardGameControl] — Assist-to-turnover ratio, assists and turnovers volume pills.
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
        modifier = modifier.fillMaxWidth(),
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

@BcThemePreview
@BcDevicePreview
@Composable
private fun KpiCardRowPreview() {
    BasketCoachTheme {
        KpiCardRow(kpis = Phase1PreviewData.kpis)
    }
}
