package com.martorell.albert.basketcoach.domain.repository

import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Drill
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.Result

interface AiAnalysisRepository {
    suspend fun getInsights(): Result<List<Insight>, DomainError>
    suspend fun getDrills(): Result<List<Drill>, DomainError>
}
