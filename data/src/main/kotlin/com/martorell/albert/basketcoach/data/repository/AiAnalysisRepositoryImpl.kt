package com.martorell.albert.basketcoach.data.repository

import com.martorell.albert.basketcoach.data.mapper.toDomain
import com.martorell.albert.basketcoach.data.network.prompt.PromptTemplates
import com.martorell.albert.basketcoach.data.source.local.AiAnalysisLocalDataSource
import com.martorell.albert.basketcoach.data.source.remote.AiRemoteDataSource
import com.martorell.albert.basketcoach.data.util.safeCall
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Drill
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.repository.AiAnalysisRepository
import javax.inject.Inject

/**
 * Repository implementation managing AI tactical analysis and practice drills.
 *
 * Implements [AiAnalysisRepository] adhering to the Single Source of Truth (SSOT) pattern
 * by consulting the in-memory cache [AiAnalysisLocalDataSource] first. If unpopulated,
 * queries [AiRemoteDataSource] using grounded match summaries, maps DTOs to domain entities,
 * and caches results locally.
 *
 * All operations are wrapped in [safeCall] for typed [DomainError] translation and [kotlinx.coroutines.CancellationException] safety.
 *
 * @param remoteDataSource Remote data source communicating with the AI inference endpoint.
 * @param localDataSource In-memory local cache storing generated insights and drills.
 */
class AiAnalysisRepositoryImpl @Inject constructor(
    private val remoteDataSource: AiRemoteDataSource,
    private val localDataSource: AiAnalysisLocalDataSource
) : AiAnalysisRepository {

    /**
     * Retrieves tactical insights for the provided [games], returning cached data if available
     * or generating new insights via remote AI inference.
     *
     * @param games The list of historical games to ground the analysis.
     * @return [Result.Success] containing the list of [Insight] items, or [Result.Error] with [DomainError].
     */
    override suspend fun getInsights(games: List<Game>): Result<List<Insight>, DomainError> = safeCall {
        localDataSource.getCachedInsights() ?: run {
            fetchAndCacheAnalysis(games)
            localDataSource.getCachedInsights().orEmpty()
        }
    }

    /**
     * Retrieves recommended practice drills for the provided [games], returning cached data if available
     * or generating new drills via remote AI inference.
     *
     * @param games The list of historical games to ground drill recommendations.
     * @return [Result.Success] containing the list of [Drill] items, or [Result.Error] with [DomainError].
     */
    override suspend fun getDrills(games: List<Game>): Result<List<Drill>, DomainError> = safeCall {
        localDataSource.getCachedDrills() ?: run {
            fetchAndCacheAnalysis(games)
            localDataSource.getCachedDrills().orEmpty()
        }
    }

    private suspend fun fetchAndCacheAnalysis(games: List<Game>) {
        val gameContext = PromptTemplates.formatGameSummary(games)
        val dto = remoteDataSource.generateAnalysis(gameContext)
        val insights = dto.insights.map { it.toDomain() }
        val drills = dto.drills.map { it.toDomain() }
        localDataSource.saveAnalysis(insights, drills)
    }
}
