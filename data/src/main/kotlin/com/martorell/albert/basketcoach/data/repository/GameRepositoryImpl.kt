package com.martorell.albert.basketcoach.data.repository

import com.martorell.albert.basketcoach.data.parser.GameJsonParser
import com.martorell.albert.basketcoach.data.parser.GameJsonParserImpl
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.repository.GameRepository
import java.io.InputStream

/**
 * Production implementation of [GameRepository] that sources Phase 1 match data from a bundled
 * classpath resource (`dataset.json`).
 *
 * Implements an in-memory caching strategy to ensure that deserialization and sorting occur only once
 * across application execution, serving as the Single Source of Truth (SSOT) for static historical fixtures.
 *
 * @param parser The [GameJsonParser] instance responsible for deserializing JSON streams.
 * @param jsonStreamProvider Provider function supplying an [InputStream] to the bundled JSON dataset.
 *   Defaults to loading `dataset.json` via the classloader.
 */
class GameRepositoryImpl(
    private val parser: GameJsonParser = GameJsonParserImpl(),
    private val jsonStreamProvider: () -> InputStream? = {
        GameRepositoryImpl::class.java.classLoader?.getResourceAsStream("dataset.json")
    }
) : GameRepository {

    private var cachedGames: List<Game>? = null

    /**
     * Retrieves all 14 Phase 1 competition games in ascending chronological order by round.
     *
     * Returns cached models if previously parsed. If un-cached, reads the bundled dataset stream,
     * parses via [GameJsonParser], sorts chronologically by [Game.round], caches the list in memory,
     * and returns the result.
     *
     * @return [Result.Success] with the 14 sorted [Game] entities, or
     *   [Result.Error] with [DomainError.Local.NotFound] if the resource stream cannot be opened, or
     *   [DomainError.Local.DatabaseError] if JSON parsing fails.
     */
    override suspend fun getPhase1Games(): Result<List<Game>, DomainError> {
        cachedGames?.let { return Result.Success(it) }

        val inputStream = jsonStreamProvider()
            ?: return Result.Error(DomainError.Local.NotFound)

        return when (val parseResult = parser.parseGames(inputStream)) {
            is Result.Success -> {
                val sortedGames = parseResult.data.sortedBy { it.round }
                cachedGames = sortedGames
                Result.Success(sortedGames)
            }
            is Result.Error -> parseResult
        }
    }

    /**
     * Retrieves a single game fixture by its competition [round].
     *
     * Sourced via [getPhase1Games], taking advantage of in-memory caching.
     *
     * @param round The round number to query (valid Phase 1 rounds: 1 to 14).
     * @return [Result.Success] containing the matching [Game], or
     *   [Result.Error] with [DomainError.Local.NotFound] if no game exists for the given round, or
     *   the underlying error if dataset loading failed.
     */
    override suspend fun getGameByRound(round: Int): Result<Game, DomainError> {
        return when (val gamesResult = getPhase1Games()) {
            is Result.Success -> {
                val game = gamesResult.data.find { it.round == round }
                if (game != null) {
                    Result.Success(game)
                } else {
                    Result.Error(DomainError.Local.NotFound)
                }
            }
            is Result.Error -> gamesResult
        }
    }
}
