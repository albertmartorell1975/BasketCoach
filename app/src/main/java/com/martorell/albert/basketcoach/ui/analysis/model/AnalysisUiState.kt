package com.martorell.albert.basketcoach.ui.analysis.model

import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.ui.dashboard.model.Phase1SummaryUiModel

/**
 * Top-level UI state for the Analysis screen, adhering to the Hybrid UI State Pattern.
 *
 * Couples high-level interaction metadata ([isRefreshing]) with discrete content states
 * represented by [content]. Strictly default-parameter-free and non-nullable (Constraint #17).
 *
 * @property content The active content state ([AnalysisUiContent.Loading], [AnalysisUiContent.Success], or [AnalysisUiContent.Error]).
 * @property isRefreshing Flag indicating whether a background refresh or loading operation is currently in progress.
 */
data class AnalysisUiState(
    val content: AnalysisUiContent,
    val isRefreshing: Boolean
)

/**
 * Sealed hierarchy modeling mutually exclusive content states for the tactical analysis screen.
 */
sealed interface AnalysisUiContent {
    /**
     * State representing ongoing initial loading or asynchronous calculation of analysis data.
     */
    data object Loading : AnalysisUiContent

    /**
     * State representing successful retrieval and computation of Phase 1 metrics and tactical insight cards.
     *
     * @property summary Aggregated Phase 1 record and percentage metrics for the baseline hero card.
     * @property selectedFilter The currently selected segmented filter tab.
     * @property totalCount Total number of tactical insights available.
     * @property strengthsCount Total number of strength insights.
     * @property improvementsCount Total number of improvement insights.
     * @property allInsights Complete unfiltered collection of mapped insight cards.
     * @property displayedInsights Filtered subset of insight cards matching [selectedFilter].
     */
    data class Success(
        val summary: Phase1SummaryUiModel,
        val selectedFilter: InsightFilterTab,
        val totalCount: Int,
        val strengthsCount: Int,
        val improvementsCount: Int,
        val allInsights: List<InsightCardUiModel>,
        val displayedInsights: List<InsightCardUiModel>
    ) : AnalysisUiContent

    /**
     * State representing a failure during match retrieval or AI insight generation.
     *
     * @property error The underlying typed domain error to be rendered via UI text mapping.
     */
    data class Error(
        val error: DomainError
    ) : AnalysisUiContent
}
