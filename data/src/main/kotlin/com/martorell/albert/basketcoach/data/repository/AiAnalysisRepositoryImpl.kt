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

class AiAnalysisRepositoryImpl(
    private val remoteDataSource: AiRemoteDataSource,
    private val localDataSource: AiAnalysisLocalDataSource
) : AiAnalysisRepository {

    override suspend fun getInsights(games: List<Game>): Result<List<Insight>, DomainError> = safeCall {
        localDataSource.getCachedInsights() ?: run {
            fetchAndCacheAnalysis(games)
            localDataSource.getCachedInsights().orEmpty()
        }
    }

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
