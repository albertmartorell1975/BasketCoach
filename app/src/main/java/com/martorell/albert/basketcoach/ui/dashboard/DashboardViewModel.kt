package com.martorell.albert.basketcoach.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.ui.dashboard.mapper.DashboardUiMapper
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardUiContent
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardUiState
import com.martorell.albert.basketcoach.usecases.game.GetPhase1GamesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel orchestrating presentation state for the Phase 1 Basketball Dashboard.
 *
 * Adheres strictly to:
 * - **Passive Initialization Mandate**: Constructor and `init` blocks trigger zero coroutine launches
 *   or repository calls. Initial state defaults passively to [DashboardUiContent.Loading].
 * - **Hybrid UI State Pattern**: [DashboardUiState] encapsulates mutually exclusive content
 *   ([DashboardUiContent.Loading], [DashboardUiContent.Success], [DashboardUiContent.Error])
 *   alongside additive overlay flags ([DashboardUiState.isRefreshing]).
 * - **Atomic UDF Updates**: All state mutations execute via [MutableStateFlow.update] to prevent race conditions.
 * - **Zero Resource Leakage**: Exposes typed [DomainError] on error states without referencing Android `R.string`.
 *
 * @property getPhase1GamesUseCase Interactor retrieving all 14 Phase 1 competition fixtures.
 * @property mapper Mathematical transformation engine producing [DashboardUiContent.Success].
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getPhase1GamesUseCase: GetPhase1GamesUseCase,
    private val mapper: DashboardUiMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DashboardUiState(
            content = DashboardUiContent.Loading,
            isRefreshing = false
        )
    )

    /**
     * Reactive stream of immutable [DashboardUiState] observed by the UI layer.
     */
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    /**
     * Asynchronously loads Phase 1 game fixtures and computes dashboard performance metrics.
     *
     * Protected by an idempotent concurrency guard: concurrent invocations while [DashboardUiState.isRefreshing]
     * is `true` are safely ignored. Transitions to [DashboardUiContent.Success] on successful non-empty data,
     * or [DashboardUiContent.Error] on use case failure or empty fixture datasets.
     */
    fun loadDashboard() {
        if (_uiState.value.isRefreshing) return

        _uiState.update { currentState ->
            currentState.copy(isRefreshing = true)
        }

        viewModelScope.launch {
            when (val result = getPhase1GamesUseCase()) {
                is Result.Success -> {
                    if (result.data.isEmpty()) {
                        _uiState.update {
                            DashboardUiState(
                                content = DashboardUiContent.Error(
                                    error = DomainError.Local.NotFound
                                ),
                                isRefreshing = false
                            )
                        }
                    } else {
                        val successContent = mapper.toSuccess(result.data)
                        _uiState.update {
                            DashboardUiState(
                                content = successContent,
                                isRefreshing = false
                            )
                        }
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        DashboardUiState(
                            content = DashboardUiContent.Error(error = result.error),
                            isRefreshing = false
                        )
                    }
                }
            }
        }
    }
}
