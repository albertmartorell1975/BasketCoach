package com.martorell.albert.basketcoach.usecases.fakes

import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Drill
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.repository.AiAnalysisRepository

class FakeAiAnalysisRepository : AiAnalysisRepository {
    var insightsToReturn: Result<List<Insight>, DomainError> = Result.Success(emptyList())
    var drillsToReturn: Result<List<Drill>, DomainError> = Result.Success(emptyList())
    var lastGamesForInsights: List<Game>? = null
    var lastGamesForDrills: List<Game>? = null
    var getInsightsCallCount: Int = 0
    var getDrillsCallCount: Int = 0

    override suspend fun getInsights(games: List<Game>): Result<List<Insight>, DomainError> {
        getInsightsCallCount++
        lastGamesForInsights = games
        return insightsToReturn
    }

    override suspend fun getDrills(games: List<Game>): Result<List<Drill>, DomainError> {
        getDrillsCallCount++
        lastGamesForDrills = games
        return drillsToReturn
    }
}
