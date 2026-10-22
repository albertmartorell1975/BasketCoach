package com.martorell.albert.basketcoach.data.repository

import com.martorell.albert.basketcoach.data.mapper.toDomain
import com.martorell.albert.basketcoach.data.network.client.DevExpertApiClient
import com.martorell.albert.basketcoach.data.network.prompt.PromptTemplates
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Drill
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.repository.AiAnalysisRepository
import com.martorell.albert.basketcoach.domain.repository.GameRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Implementation of [AiAnalysisRepository] that fetches tactical insights and recommended practice
 * drills from the [DevExpertApiClient] grounded in Phase 1 match context from [GameRepository].
 *
 * Uses a [Mutex] and shared in-memory caching to ensure that concurrent or sequential invocations
 * of [getInsights] and [getDrills] trigger only a single remote AI request.
 */
class AiAnalysisRepositoryImpl(
    private val gameRepository: GameRepository,
    private val apiClient: DevExpertApiClient
) : AiAnalysisRepository {

    private val mutex = Mutex()
    private var cachedInsights: List<Insight>? = null
    private var cachedDrills: List<Drill>? = null

    override suspend fun getInsights(): Result<List<Insight>, DomainError> {
        mutex.withLock {
            cachedInsights?.let { return Result.Success(it) }

            return when (val loadResult = loadAnalysisInternal()) {
                is Result.Success -> Result.Success(cachedInsights.orEmpty())
                is Result.Error -> loadResult
            }
        }
    }

    override suspend fun getDrills(): Result<List<Drill>, DomainError> {
        mutex.withLock {
            cachedDrills?.let { return Result.Success(it) }

            return when (val loadResult = loadAnalysisInternal()) {
                is Result.Success -> Result.Success(cachedDrills.orEmpty())
                is Result.Error -> loadResult
            }
        }
    }

    private suspend fun loadAnalysisInternal(): Result<Unit, DomainError> {
        val gamesResult = gameRepository.getPhase1Games()
        val gameSummary = when (gamesResult) {
            is Result.Success -> PromptTemplates.formatGameSummary(gamesResult.data)
            is Result.Error -> return gamesResult
        }

        return when (val analysisResult = apiClient.generateAnalysis(gameSummary)) {
            is Result.Success -> {
                val dto = analysisResult.data
                cachedInsights = dto.insights.map { it.toDomain() }
                cachedDrills = dto.drills.map { it.toDomain() }
                Result.Success(Unit)
            }
            is Result.Error -> analysisResult
        }
    }
}
