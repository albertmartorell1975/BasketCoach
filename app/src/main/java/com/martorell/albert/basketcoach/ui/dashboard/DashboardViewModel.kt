package com.martorell.albert.basketcoach.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.ui.dashboard.mapper.DashboardErrorMapper
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

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getPhase1GamesUseCase: GetPhase1GamesUseCase,
    private val mapper: DashboardUiMapper,
    private val errorMapper: DashboardErrorMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DashboardUiState(
            content = DashboardUiContent.Loading,
            isRefreshing = false
        )
    )
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

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
                                    messageRes = R.string.dashboard_error_empty
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
                    val messageRes = errorMapper.toMessageRes(result.error)
                    _uiState.update {
                        DashboardUiState(
                            content = DashboardUiContent.Error(messageRes = messageRes),
                            isRefreshing = false
                        )
                    }
                }
            }
        }
    }
}
