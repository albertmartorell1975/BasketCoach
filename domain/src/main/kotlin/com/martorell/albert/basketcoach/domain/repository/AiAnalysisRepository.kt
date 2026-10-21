package com.martorell.albert.basketcoach.domain.repository

import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Drill
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.Result

/**
 * Domain repository contract defining operations to retrieve AI-generated tactical insights
 * and recommended practice drills.
 */
interface AiAnalysisRepository {
    /**
     * Retrieves AI-generated insights detailing team strengths, weaknesses, and tactical patterns.
     *
     * @return [Result.Success] containing the list of [Insight] entities, or [Result.Error] with [DomainError].
     */
    suspend fun getInsights(): Result<List<Insight>, DomainError>

    /**
     * Retrieves tactical practice drills designed to address identified areas for improvement.
     *
     * @return [Result.Success] containing the list of [Drill] entities, or [Result.Error] with [DomainError].
     */
    suspend fun getDrills(): Result<List<Drill>, DomainError>
}
