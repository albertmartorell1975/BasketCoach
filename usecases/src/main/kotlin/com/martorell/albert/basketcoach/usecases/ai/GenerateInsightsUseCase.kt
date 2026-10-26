package com.martorell.albert.basketcoach.usecases.ai

import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.repository.AiAnalysisRepository
import com.martorell.albert.basketcoach.domain.repository.GameRepository

/**
 * Domain interactor that orchestrates the generation of tactical AI insights grounded in Phase 1 match history.
 *
 * Retrieves historical fixtures from [GameRepository] and provides them as contextual grounding
 * to [AiAnalysisRepository]. If match retrieval fails, the error is propagated immediately
 * without querying the AI inference service.
 *
 * @property gameRepository Domain repository providing access to Phase 1 match data.
 * @property aiAnalysisRepository Domain repository managing AI tactical analysis and caching.
 */
class GenerateInsightsUseCase(
    private val gameRepository: GameRepository,
    private val aiAnalysisRepository: AiAnalysisRepository
) {
    /**
     * Executes the use case to generate tactical insights.
     *
     * @return [Result.Success] containing the list of [Insight] items, or [Result.Error] with [DomainError].
     */
    suspend operator fun invoke(): Result<List<Insight>, DomainError> {
        return when (val gamesResult = gameRepository.getPhase1Games()) {
            is Result.Error -> gamesResult
            is Result.Success -> aiAnalysisRepository.getInsights(gamesResult.data)
        }
    }
}
