package com.martorell.albert.basketcoach.ui.analysis

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.martorell.albert.basketcoach.ui.analysis.components.AnalysisHeroCard
import com.martorell.albert.basketcoach.ui.analysis.components.InsightCardGrid
import com.martorell.albert.basketcoach.ui.analysis.data.AnalysisPreviewData
import com.martorell.albert.basketcoach.ui.analysis.model.AnalysisUiContent
import com.martorell.albert.basketcoach.ui.analysis.model.InsightFilterTab
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import com.martorell.albert.basketcoach.ui.theme.BcDevicePreview
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview

/**
 * Stateless root content canvas for the Tablet Landscape Analysis screen.
 *
 * Assembles the [AnalysisHeroCard] and [InsightCardGrid] within a vertically scrollable column.
 *
 * @param content Immutable root UI content ([AnalysisUiContent.Success]).
 * @param onFilterSelected Callback invoked when a segmented filter tab is selected.
 * @param onExploreChat Callback invoked when "Explorar al Xat" is tapped on an improvement card.
 * @param modifier Composable modifier for layout and styling.
 */
@Composable
fun AnalysisContent(
    content: AnalysisUiContent.Success,
    onFilterSelected: (InsightFilterTab) -> Unit,
    onExploreChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        AnalysisHeroCard(
            summary = content.summary,
            selectedFilter = content.selectedFilter,
            totalCount = content.totalCount,
            strengthsCount = content.strengthsCount,
            improvementsCount = content.improvementsCount,
            onFilterSelected = onFilterSelected
        )

        InsightCardGrid(
            insights = content.displayedInsights,
            onExploreChat = onExploreChat
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@BcDevicePreview
@BcThemePreview
@Composable
private fun AnalysisContentPreview() {
    BasketCoachTheme {
        AnalysisContent(
            content = AnalysisPreviewData.success,
            onFilterSelected = {},
            onExploreChat = {}
        )
    }
}
