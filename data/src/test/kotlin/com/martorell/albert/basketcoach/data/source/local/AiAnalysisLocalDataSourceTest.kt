package com.martorell.albert.basketcoach.data.source.local

import com.martorell.albert.basketcoach.domain.model.Drill
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.InsightCategory
import com.martorell.albert.basketcoach.domain.model.InsightMetric
import com.martorell.albert.basketcoach.domain.model.InsightType
import com.martorell.albert.basketcoach.domain.model.MetricSentiment

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AiAnalysisLocalDataSourceTest {

    @Test
    fun `returns null when cache is initially empty`() = runTest {
        val dataSource = AiAnalysisLocalDataSourceImpl()
        assertNull(dataSource.getCachedInsights())
        assertNull(dataSource.getCachedDrills())
    }

    @Test
    fun `saveAnalysis stores and retrieves insights and drills`() = runTest {
        val dataSource = AiAnalysisLocalDataSourceImpl()

        val insights = listOf(
            Insight("i1", "Title", "Desc", InsightCategory.OFFENSE, listOf(1), InsightType.STRENGTH, "Tag", InsightMetric("L", "V", "S", "SL", "SV", "ST", MetricSentiment.POSITIVE), "P", "F", "")
        )
        val drills = listOf(
            Drill("d1", "Drill", "Obj", listOf("Step 1"), "Ball handling")
        )

        dataSource.saveAnalysis(insights, drills)

        assertEquals(insights, dataSource.getCachedInsights())
        assertEquals(drills, dataSource.getCachedDrills())
    }

    @Test
    fun `clear resets cached insights and drills`() = runTest {
        val dataSource = AiAnalysisLocalDataSourceImpl()
        dataSource.saveAnalysis(
            listOf(Insight("i1", "T", "D", InsightCategory.DEFENSE, emptyList(), InsightType.IMPROVEMENT, "Tag", InsightMetric("L", "V", "S", "SL", "SV", "ST", MetricSentiment.NEGATIVE), "P", "F", "Prompt")),
            listOf(Drill("d1", "N", "O", emptyList(), "F"))
        )

        dataSource.clear()

        assertNull(dataSource.getCachedInsights())
        assertNull(dataSource.getCachedDrills())
    }
}
