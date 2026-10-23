package com.martorell.albert.basketcoach.data.source.local

import com.martorell.albert.basketcoach.domain.model.Drill
import com.martorell.albert.basketcoach.domain.model.Insight

/**
 * Local data source contract acting as the Single Source of Truth (SSOT) for tactical analysis.
 */
interface AiAnalysisLocalDataSource {
    /**
     * Retrieves cached tactical insights, or null if no analysis has been performed.
     */
    suspend fun getCachedInsights(): List<Insight>?

    /**
     * Retrieves cached practice drills, or null if no analysis has been performed.
     */
    suspend fun getCachedDrills(): List<Drill>?

    /**
     * Atomically stores the latest tactical insights and drills in the local cache.
     */
    suspend fun saveAnalysis(insights: List<Insight>, drills: List<Drill>)

    /**
     * Clears all cached analysis data.
     */
    suspend fun clear()
}
