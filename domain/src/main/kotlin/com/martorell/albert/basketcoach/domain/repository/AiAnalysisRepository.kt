package com.martorell.albert.basketcoach.domain.repository

import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Drill
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.prompt.AiAnalysisPrompt

/**
 * Domain repository contract defining operations for AI-driven tactical analysis and practice drills.
 *
 * Implementations provide tactical insights and training exercises grounded in Phase 1 match history,
 * leveraging remote inference providers with local caching.
 */
interface AiAnalysisRepository {
    /**
     * Generates or retrieves cached tactical insights based on the provided [prompt].
     *
     * @param prompt Parameter object containing system prompt, user prompt, and sampling temperature.
     * @return [Result.Success] containing the list of [Insight] items, or [Result.Error] with [DomainError].
     */
    suspend fun getInsights(prompt: AiAnalysisPrompt): Result<List<Insight>, DomainError>

    /**
     * Generates or retrieves cached practice drill recommendations based on the provided [prompt].
     *
     * @param prompt Parameter object containing system prompt, user prompt, and sampling temperature.
     * @return [Result.Success] containing the list of [Drill] items, or [Result.Error] with [DomainError].
     */
    suspend fun getDrills(prompt: AiAnalysisPrompt): Result<List<Drill>, DomainError>
}
