package com.martorell.albert.basketcoach.data.repository

import com.martorell.albert.basketcoach.data.mapper.toDomain
import com.martorell.albert.basketcoach.data.source.local.GameLocalDataSource
import com.martorell.albert.basketcoach.data.util.safeCall
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.repository.GameRepository
import javax.inject.Inject

/**
 * Production implementation of [GameRepository] that sources Phase 1 match data from a
 * [GameLocalDataSource], mapping the raw DTOs into domain [Game] entities.
 *
 * Technical exceptions raised by the data source are translated into typed [DomainError] via [safeCall].
 *
 * @param localDataSource The data source providing the bundled Phase 1 fixtures (and their caching).
 */
class GameRepositoryImpl @Inject constructor(
    private val localDataSource: GameLocalDataSource
) : GameRepository {

    /**
     * Retrieves all 14 Phase 1 competition games in ascending chronological order by round.
     *
     * @return [Result.Success] with the 14 sorted [Game] entities, or
     *   [Result.Error] with [DomainError.Local.NotFound] if the dataset cannot be opened, or
     *   the [DomainError] mapped from any parsing failure.
     */
    override suspend fun getPhase1Games(): Result<List<Game>, DomainError> = safeCall {
        localDataSource.getGames().map { it.toDomain() }.sortedBy { it.round }
    }

    /**
     * Retrieves a single game fixture by its competition [round].
     *
     * @param round The round number to query (valid Phase 1 rounds: 1 to 14).
     * @return [Result.Success] containing the matching [Game], or
     *   [Result.Error] with [DomainError.Local.NotFound] if no game exists for the given round, or
     *   the underlying error if dataset loading failed.
     */
    override suspend fun getGameByRound(round: Int): Result<Game, DomainError> = safeCall {
        localDataSource.getGames().first { it.round == round }.toDomain()
    }
}
