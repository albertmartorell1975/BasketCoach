package com.martorell.albert.basketcoach.usecases.game

import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.repository.GameRepository
import javax.inject.Inject

/**
 * Domain interactor to retrieve the complete set of 14 Phase 1 basketball matches.
 *
 * @property gameRepository Domain repository providing access to match box scores.
 */
class GetPhase1GamesUseCase @Inject constructor(
    private val gameRepository: GameRepository
) {
    /**
     * Executes the use case to fetch all Phase 1 fixtures.
     *
     * @return [Result.Success] containing the list of [Game] instances, or [Result.Error] with [DomainError].
     */
    suspend operator fun invoke(): Result<List<Game>, DomainError> {
        return gameRepository.getPhase1Games()
    }
}
