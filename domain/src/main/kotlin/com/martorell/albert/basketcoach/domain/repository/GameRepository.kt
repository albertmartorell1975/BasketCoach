package com.martorell.albert.basketcoach.domain.repository

import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Result

/**
 * Domain repository contract defining access to historical Phase 1 basketball matches.
 *
 * Implementations provide match details and box score metrics sourced from bundled datasets
 * or cached local storage.
 */
interface GameRepository {
    /**
     * Retrieves the complete set of 14 Phase 1 basketball matches.
     *
     * @return [Result.Success] containing the chronological list of [Game] entities,
     * or [Result.Error] with [DomainError] if loading fails.
     */
    suspend fun getPhase1Games(): Result<List<Game>, DomainError>

    /**
     * Retrieves a single game by its competition round number.
     *
     * @param round Round number of the desired match (1 to 14).
     * @return [Result.Success] with the matching [Game], or [Result.Error] containing
     * [DomainError.Local.NotFound] if no game matches the requested round.
     */
    suspend fun getGameByRound(round: Int): Result<Game, DomainError>
}
