package com.martorell.albert.basketcoach.data.source.local

import com.martorell.albert.basketcoach.domain.model.Drill
import com.martorell.albert.basketcoach.domain.model.Insight
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

/**
 * Thread-safe in-memory cache implementation of [AiAnalysisLocalDataSource] protected by a [Mutex].
 *
 * Serves as the Single Source of Truth (SSOT) for tactical analysis and practice drills generated during
 * the active session, avoiding repeated AI inference requests across screen navigations.
 */
class AiAnalysisLocalDataSourceImpl @Inject constructor() : AiAnalysisLocalDataSource {

    private val mutex = Mutex()
    private var cachedInsights: List<Insight>? = null
    private var cachedDrills: List<Drill>? = null

    /**
     * Retrieves the cached list of tactical insights, or null if no analysis has been saved yet.
     */
    override suspend fun getCachedInsights(): List<Insight>? {
        return mutex.withLock { cachedInsights?.toList() }
    }

    /**
     * Retrieves the cached list of practice drills, or null if no analysis has been saved yet.
     */
    override suspend fun getCachedDrills(): List<Drill>? {
        return mutex.withLock { cachedDrills?.toList() }
    }

    /**
     * Atomically stores the generated [insights] and [drills] in memory.
     *
     * @param insights List of tactical [Insight] domain models.
     * @param drills List of practice [Drill] domain models.
     */
    override suspend fun saveAnalysis(insights: List<Insight>, drills: List<Drill>) {
        mutex.withLock {
            cachedInsights = insights.toList()
            cachedDrills = drills.toList()
        }
    }

    /**
     * Atomically evicts all cached insights and drills.
     */
    override suspend fun clear() {
        mutex.withLock {
            cachedInsights = null
            cachedDrills = null
        }
    }
}
