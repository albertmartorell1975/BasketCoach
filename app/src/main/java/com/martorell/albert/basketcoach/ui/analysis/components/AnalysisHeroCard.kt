package com.martorell.albert.basketcoach.ui.analysis.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Insights
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
import com.martorell.albert.basketcoach.ui.analysis.data.AnalysisPreviewData
import com.martorell.albert.basketcoach.ui.analysis.model.InsightFilterTab
import com.martorell.albert.basketcoach.ui.dashboard.model.Phase1SummaryUiModel
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import com.martorell.albert.basketcoach.ui.theme.BcDevicePreview
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaOutline
import com.martorell.albert.basketcoach.ui.theme.PiaSuccessGreen
import com.martorell.albert.basketcoach.ui.theme.PiaSurface
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted
import com.martorell.albert.basketcoach.ui.theme.PiaTextPrimary

/**
 * Top baseline Hero Card for the Tablet Landscape Analysis screen.
 *
 * Displays the authoritative Phase 1 record (3V-11D, 21.4%), 14/14 rounds completed badge,
 * and hosts the interactive [AnalysisFilterTabs] on the right.
 *
 * @param summary Phase 1 competition summary model.
 * @param selectedFilter Currently active filter tab.
 * @param totalCount Total insight count.
 * @param strengthsCount Strength insight count.
 * @param improvementsCount Improvement insight count.
 * @param onFilterSelected Callback invoked when a filter tab is selected.
 * @param modifier Composable modifier for styling and layout adjustments.
 */
@Composable
fun AnalysisHeroCard(
    summary: Phase1SummaryUiModel,
    selectedFilter: InsightFilterTab,
    totalCount: Int,
    strengthsCount: Int,
    improvementsCount: Int,
    onFilterSelected: (InsightFilterTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PiaSurface)
            .border(1.dp, PiaOutline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PiaNavy.copy(alpha = 0.06f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Insights,
                        contentDescription = null,
                        tint = PiaNavy,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(PiaSuccessGreen)
                        )
                        Text(
                            text = "${stringResource(R.string.hero_baseline_tag)} · ${stringResource(R.string.hero_verified_tag)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PiaTextMuted,
                            letterSpacing = 0.04.sp
                        )
                    }
                    Text(
                        text = "Balanç Competitiu: ${summary.wins} Victòries - ${summary.losses} Derrotes (${summary.winPercentage}% de victòries)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PiaTextPrimary
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${summary.winPercentage}% victòries",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PiaSuccessGreen
                        )
                        Text(
                            text = "· ${stringResource(summary.categoryRes)} · ${stringResource(summary.teamNameRes)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = PiaTextMuted
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.analysis_hero_phase1_register),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PiaTextMuted
                    )
                    Text(
                        text = stringResource(R.string.analysis_hero_phase1_rounds),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PiaNavy
                    )
                }

                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .background(PiaOutline.copy(alpha = 0.3f))
                        .padding(horizontal = 0.5.dp)
                )

                AnalysisFilterTabs(
                    selectedTab = selectedFilter,
                    totalCount = totalCount,
                    strengthsCount = strengthsCount,
                    improvementsCount = improvementsCount,
                    onTabSelected = onFilterSelected
                )
            }
        }
    }
}

@BcThemePreview
@BcDevicePreview
@Composable
private fun AnalysisHeroCardPreview() {
    BasketCoachTheme {
        AnalysisHeroCard(
            summary = AnalysisPreviewData.summary,
            selectedFilter = InsightFilterTab.ALL,
            totalCount = 5,
            strengthsCount = 2,
            improvementsCount = 3,
            onFilterSelected = {}
        )
    }
}
