package com.martorell.albert.basketcoach.domain.repository

import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Drill
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.Result

/**
 * Domain repository contract defining operations for AI-driven tactical analysis and practice drills.
 *
 * Implementations provide tactical insights and training exercises grounded in Phase 1 match history,
 * leveraging remote inference providers with local caching.
 */
interface AiAnalysisRepository {
    /**
     * Generates or retrieves cached tactical insights based on the provided list of matches.
     *
     * @param games The list of historical [Game] fixtures used to ground the analysis.
     * @return [Result.Success] containing the list of [Insight] items, or [Result.Error] with [DomainError].
     */
    suspend fun getInsights(games: List<Game>): Result<List<Insight>, DomainError>

    /**
     * Generates or retrieves cached practice drill recommendations based on the provided list of matches.
     *
     * @param games The list of historical [Game] fixtures used to ground the drills.
     * @return [Result.Success] containing the list of [Drill] items, or [Result.Error] with [DomainError].
     */
    suspend fun getDrills(games: List<Game>): Result<List<Drill>, DomainError>
}
