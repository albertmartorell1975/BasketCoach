package com.martorell.albert.basketcoach.ui.analysis.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.ui.analysis.data.AnalysisPreviewData
import com.martorell.albert.basketcoach.ui.analysis.model.InsightCardUiModel
import com.martorell.albert.basketcoach.ui.theme.BasketCoachTheme
import com.martorell.albert.basketcoach.ui.theme.BcDevicePreview
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted

/**
 * Responsive 3-column grid container for tactical insight cards.
 *
 * Renders cards chunked in rows of 3, balancing remaining spaces with spacers.
 * Shows an empty state message if no cards match the selected filter.
 *
 * @param insights List of insight cards to display.
 * @param onExploreChat Callback invoked with an exploration prompt when the chat button on an improvement card is tapped.
 * @param modifier Composable modifier for styling and layout adjustments.
 */
@Composable
fun InsightCardGrid(
    insights: List<InsightCardUiModel>,
    onExploreChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (insights.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.analysis_empty_state),
                fontSize = 14.sp,
                color = PiaTextMuted
            )
        }
        return
    }

    val chunkedInsights = remember(insights) { insights.chunked(3) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        chunkedInsights.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                rowItems.forEach { card ->
                    Box(modifier = Modifier.weight(1f)) {
                        InsightCard(
                            card = card,
                            onExploreChat = onExploreChat,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                repeat(3 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@BcThemePreview
@BcDevicePreview
@Composable
private fun InsightCardGridPreview() {
    BasketCoachTheme {
        InsightCardGrid(
            insights = AnalysisPreviewData.allInsights,
            onExploreChat = {}
        )
    }
}
