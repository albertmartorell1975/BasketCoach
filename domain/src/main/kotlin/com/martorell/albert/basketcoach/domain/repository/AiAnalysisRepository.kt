package com.martorell.albert.basketcoach.domain.repository

import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Drill
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.Result

/**
 * Domain repository contract defining operations to retrieve AI-generated tactical insights
 * and recommended practice drills grounded in match context.
 */
interface AiAnalysisRepository {
    /**
     * Retrieves AI-generated insights detailing team strengths, weaknesses, and tactical patterns.
     *
     * @param gameContext Textual summary of Phase 1 games and metrics.
     * @return [Result.Success] containing the list of [Insight] entities, or [Result.Error] with [DomainError].
     */
    suspend fun getInsights(gameContext: String): Result<List<Insight>, DomainError>

    /**
     * Retrieves tactical practice drills designed to address identified areas for improvement.
     *
     * @param gameContext Textual summary of Phase 1 games and metrics.
     * @return [Result.Success] containing the list of [Drill] entities, or [Result.Error] with [DomainError].
     */
    suspend fun getDrills(gameContext: String): Result<List<Drill>, DomainError>
}
