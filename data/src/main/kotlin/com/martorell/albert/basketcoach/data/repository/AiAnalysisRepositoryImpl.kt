package com.martorell.albert.basketcoach.data.repository

import com.martorell.albert.basketcoach.data.mapper.toDomain
import com.martorell.albert.basketcoach.data.source.local.AiAnalysisLocalDataSource
import com.martorell.albert.basketcoach.data.source.remote.AiRemoteDataSource
import com.martorell.albert.basketcoach.data.util.safeCall
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Drill
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.repository.AiAnalysisRepository

/**
 * Production implementation of [AiAnalysisRepository] coordinating [AiRemoteDataSource]
 * and [AiAnalysisLocalDataSource] to provide tactical insights and recommended practice drills.
 */
class AiAnalysisRepositoryImpl(
    private val remoteDataSource: AiRemoteDataSource,
    private val localDataSource: AiAnalysisLocalDataSource
) : AiAnalysisRepository {

    override suspend fun getInsights(gameContext: String): Result<List<Insight>, DomainError> {
        localDataSource.getCachedInsights()?.let { return Result.Success(it) }

        return when (val loadResult = loadAnalysisInternal(gameContext)) {
            is Result.Success -> Result.Success(localDataSource.getCachedInsights().orEmpty())
            is Result.Error -> loadResult
        }
    }

    override suspend fun getDrills(gameContext: String): Result<List<Drill>, DomainError> {
        localDataSource.getCachedDrills()?.let { return Result.Success(it) }

        return when (val loadResult = loadAnalysisInternal(gameContext)) {
            is Result.Success -> Result.Success(localDataSource.getCachedDrills().orEmpty())
            is Result.Error -> loadResult
        }
    }

    private suspend fun loadAnalysisInternal(gameContext: String): Result<Unit, DomainError> {
        val remoteResult = safeCall { remoteDataSource.generateAnalysis(gameContext) }
        return when (remoteResult) {
            is Result.Success -> {
                val dto = remoteResult.data
                val insights = dto.insights.map { it.toDomain() }
                val drills = dto.drills.map { it.toDomain() }
                localDataSource.saveAnalysis(insights, drills)
                Result.Success(Unit)
            }
            is Result.Error -> remoteResult
        }
    }
}
