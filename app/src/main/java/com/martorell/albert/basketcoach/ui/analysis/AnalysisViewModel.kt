package com.martorell.albert.basketcoach.ui.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.ui.analysis.mapper.AnalysisUiMapper
import com.martorell.albert.basketcoach.ui.analysis.model.AnalysisUiContent
import com.martorell.albert.basketcoach.ui.analysis.model.AnalysisUiState
import com.martorell.albert.basketcoach.ui.analysis.model.InsightFilterTab
import com.martorell.albert.basketcoach.usecases.ai.GenerateInsightsUseCase
import com.martorell.albert.basketcoach.usecases.game.GetPhase1GamesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel orchestrating the tactical analysis screen state and user interactions.
 *
 * Adheres strictly to the Passive Initialization Mandate (`viewmodel-architecture-governance`)
 * by performing zero work in `init`. Initial UI state starts with [AnalysisUiContent.Loading]
 * and data loading is deferred until [loadAnalysis] is triggered by the UI layer
 * (e.g., via `LaunchedEffect(Unit)` in `AnalysisScreen`).
 *
 * Manages the Hybrid UI State Pattern ([AnalysisUiState] containing [AnalysisUiContent])
 * with atomic state updates via [MutableStateFlow.update]. Handles in-memory tab filtering
 * via [onFilterSelected] without re-triggering remote AI inference or reloading games.
 *
 * @param getPhase1GamesUseCase Domain interactor retrieving Phase 1 match history.
 * @param generateInsightsUseCase Domain interactor generating AI tactical insights.
 * @param mapper Presentation mapper computing UI summaries and tab-filtered insight cards.
 */
@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val getPhase1GamesUseCase: GetPhase1GamesUseCase,
    private val generateInsightsUseCase: GenerateInsightsUseCase,
    private val mapper: AnalysisUiMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AnalysisUiState(
            content = AnalysisUiContent.Loading,
            isRefreshing = false
        )
    )
    /**
     * Observable [StateFlow] exposing the current [AnalysisUiState] to the presentation layer.
     */
    val uiState: StateFlow<AnalysisUiState> = _uiState.asStateFlow()

    /**
     * Triggers asynchronous loading of Phase 1 games and AI-generated tactical insights.
     *
     * Features a concurrency guard that prevents re-entry if a refresh operation is already in progress.
     * Sequentially queries [getPhase1GamesUseCase] and [generateInsightsUseCase], mapping domain results
     * into [AnalysisUiContent.Success] while preserving the active [InsightFilterTab], or transitioning
     * into [AnalysisUiContent.Error] upon any domain failure.
     */
    fun loadAnalysis() {
        if (_uiState.value.isRefreshing) return

        _uiState.update { currentState ->
            currentState.copy(isRefreshing = true)
        }

        viewModelScope.launch {
            when (val gamesResult = getPhase1GamesUseCase()) {
                is Result.Error -> {
                    _uiState.update {
                        AnalysisUiState(
                            content = AnalysisUiContent.Error(error = gamesResult.error),
                            isRefreshing = false
                        )
                    }
                }
                is Result.Success -> {
                    val games = gamesResult.data
                    when (val insightsResult = generateInsightsUseCase()) {
                        is Result.Error -> {
                            _uiState.update {
                                AnalysisUiState(
                                    content = AnalysisUiContent.Error(error = insightsResult.error),
                                    isRefreshing = false
                                )
                            }
                        }
                        is Result.Success -> {
                            val currentContent = _uiState.value.content
                            val activeFilter = if (currentContent is AnalysisUiContent.Success) {
                                currentContent.selectedFilter
                            } else {
                                InsightFilterTab.ALL
                            }
                            val content = mapper.toContent(
                                games = games,
                                insights = insightsResult.data,
                                filter = activeFilter
                            )
                            _uiState.update {
                                AnalysisUiState(
                                    content = content,
                                    isRefreshing = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Updates the active insight tab filter in-memory when the current state is [AnalysisUiContent.Success].
     *
     * Filters [AnalysisUiContent.Success.allInsights] into [AnalysisUiContent.Success.displayedInsights]
     * atomically via [MutableStateFlow.update] without querying remote or local data sources.
     * If the selected filter is already active or content is not [AnalysisUiContent.Success], this is a no-op.
     *
     * @param filter The desired [InsightFilterTab] filter to apply.
     */
    fun onFilterSelected(filter: InsightFilterTab) {
        _uiState.update { currentState ->
            val content = currentState.content
            if (content is AnalysisUiContent.Success) {
                if (content.selectedFilter == filter) {
                    currentState
                } else {
                    val newDisplayed = mapper.filterInsights(content.allInsights, filter)
                    currentState.copy(
                        content = content.copy(
                            selectedFilter = filter,
                            displayedInsights = newDisplayed
                        )
                    )
                }
            } else {
                currentState
            }
        }
    }
}
