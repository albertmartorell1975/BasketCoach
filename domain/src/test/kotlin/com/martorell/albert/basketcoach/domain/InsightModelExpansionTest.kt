package com.martorell.albert.basketcoach.domain

import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.InsightCategory
import com.martorell.albert.basketcoach.domain.model.InsightMetric
import com.martorell.albert.basketcoach.domain.model.InsightType
import com.martorell.albert.basketcoach.domain.model.MetricSentiment
import org.junit.Assert.assertEquals
import org.junit.Test

class InsightModelExpansionTest {

    @Test
    fun model_instantiatesAllFieldsExplicitlyWithoutDefaults() {
        val metric = InsightMetric(
            primaryLabel = "Percentatge T2",
            primaryValue = "58.4%",
            primarySubValue = "+8.2% vs mitjana",
            secondaryLabel = "Punts / Pos.",
            secondaryValue = "1.24",
            secondaryTag = "dataset:paint_eff",
            sentiment = MetricSentiment.POSITIVE
        )

        val insight = Insight(
            id = "insight_strength_1",
            title = "Eficiència en Tir Interior",
            description = "Alta productivitat a la pintura.",
            category = InsightCategory.OFFENSE,
            relatedGameRounds = listOf(3, 9, 10),
            type = InsightType.STRENGTH,
            contextTag = "Fase Ofensiva · Pintura",
            metric = metric,
            priority = "Alta",
            footerNote = "Factor diferencial en victòries.",
            exploreChatPrompt = ""
        )

        assertEquals("insight_strength_1", insight.id)
        assertEquals(InsightType.STRENGTH, insight.type)
        assertEquals("Fase Ofensiva · Pintura", insight.contextTag)
        assertEquals(metric, insight.metric)
        assertEquals("58.4%", insight.metric.primaryValue)
        assertEquals(MetricSentiment.POSITIVE, insight.metric.sentiment)
        assertEquals("Alta", insight.priority)
        assertEquals("Factor diferencial en victòries.", insight.footerNote)
        assertEquals("", insight.exploreChatPrompt)
    }

    @Test
    fun enums_exposeExpectedEntriesAndFromStringFactory() {
        assertEquals(2, InsightType.entries.size)
        assertEquals(InsightType.STRENGTH, InsightType.fromString("STRENGTH"))
        assertEquals(InsightType.STRENGTH, InsightType.fromString("strength"))
        assertEquals(InsightType.IMPROVEMENT, InsightType.fromString("IMPROVEMENT"))
        assertEquals(InsightType.IMPROVEMENT, InsightType.fromString("improvement"))

        assertEquals(3, MetricSentiment.entries.size)
        assertEquals(MetricSentiment.POSITIVE, MetricSentiment.fromString("POSITIVE"))
        assertEquals(MetricSentiment.POSITIVE, MetricSentiment.fromString("positive"))
        assertEquals(MetricSentiment.NEGATIVE, MetricSentiment.fromString("NEGATIVE"))
        assertEquals(MetricSentiment.NEGATIVE, MetricSentiment.fromString("negative"))
        assertEquals(MetricSentiment.NEUTRAL, MetricSentiment.fromString("NEUTRAL"))
        assertEquals(MetricSentiment.NEUTRAL, MetricSentiment.fromString("neutral"))
    }
}
