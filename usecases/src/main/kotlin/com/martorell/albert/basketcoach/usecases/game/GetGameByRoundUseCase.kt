package com.martorell.albert.basketcoach.usecases.game

import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.repository.GameRepository

/**
 * Domain interactor to retrieve a specific match by its competition round number.
 *
 * @property gameRepository Domain repository providing access to match box scores.
 */
class GetGameByRoundUseCase(
    private val gameRepository: GameRepository
) {
    /**
     * Executes the use case to fetch a match by its round number.
     *
     * @param round The competition round number (1 to 14).
     * @return [Result.Success] containing the matching [Game], or [Result.Error] with [DomainError.Local.NotFound].
     */
    suspend operator fun invoke(round: Int): Result<Game, DomainError> {
        return gameRepository.getGameByRound(round)
    }
}
