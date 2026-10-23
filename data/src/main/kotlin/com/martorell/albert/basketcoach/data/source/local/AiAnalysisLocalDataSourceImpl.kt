package com.martorell.albert.basketcoach.data.source.local

import com.martorell.albert.basketcoach.domain.model.Drill
import com.martorell.albert.basketcoach.domain.model.Insight
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Thread-safe in-memory implementation of [AiAnalysisLocalDataSource] protected by a [Mutex].
 */
class AiAnalysisLocalDataSourceImpl : AiAnalysisLocalDataSource {

    private val mutex = Mutex()
    private var cachedInsights: List<Insight>? = null
    private var cachedDrills: List<Drill>? = null

    override suspend fun getCachedInsights(): List<Insight>? {
        return mutex.withLock { cachedInsights?.toList() }
    }

    override suspend fun getCachedDrills(): List<Drill>? {
        return mutex.withLock { cachedDrills?.toList() }
    }

    override suspend fun saveAnalysis(insights: List<Insight>, drills: List<Drill>) {
        mutex.withLock {
            cachedInsights = insights.toList()
            cachedDrills = drills.toList()
        }
    }

    override suspend fun clear() {
        mutex.withLock {
            cachedInsights = null
            cachedDrills = null
        }
    }
}
