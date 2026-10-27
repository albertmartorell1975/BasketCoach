package com.martorell.albert.basketcoach.ui.analysis

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.martorell.albert.basketcoach.ui.analysis.data.AnalysisPreviewData
import com.martorell.albert.basketcoach.ui.analysis.model.InsightFilterTab

/**
 * Stateful entry composable for the Tablet Landscape Analysis destination.
 *
 * Manages the remembered filter tab state and delegates presentation to [AnalysisContent].
 *
 * @param onNavigateToChat Callback hoisted to navigation routing, passing the card's exploration prompt to the chat assistant.
 * @param modifier Composable modifier for layout and styling.
 */
@Composable
fun AnalysisScreen(
    onNavigateToChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by rememberSaveable { mutableStateOf(InsightFilterTab.ALL) }
    val uiState = AnalysisPreviewData.createUiState(selectedFilter)

    AnalysisContent(
        uiState = uiState,
        onFilterSelected = { selectedFilter = it },
        onExploreChat = onNavigateToChat,
        modifier = modifier
    )
}
