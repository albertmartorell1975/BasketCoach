package com.martorell.albert.basketcoach.usecases.fakes

import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Drill
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.prompt.AiAnalysisPrompt
import com.martorell.albert.basketcoach.domain.repository.AiAnalysisRepository

class FakeAiAnalysisRepository : AiAnalysisRepository {
    var insightsToReturn: Result<List<Insight>, DomainError> = Result.Success(emptyList())
    var drillsToReturn: Result<List<Drill>, DomainError> = Result.Success(emptyList())
    var lastPromptForInsights: AiAnalysisPrompt? = null
    var lastPromptForDrills: AiAnalysisPrompt? = null
    var getInsightsCallCount: Int = 0
    var getDrillsCallCount: Int = 0

    override suspend fun getInsights(prompt: AiAnalysisPrompt): Result<List<Insight>, DomainError> {
        getInsightsCallCount++
        lastPromptForInsights = prompt
        return insightsToReturn
    }

    override suspend fun getDrills(prompt: AiAnalysisPrompt): Result<List<Drill>, DomainError> {
        getDrillsCallCount++
        lastPromptForDrills = prompt
        return drillsToReturn
    }
}
