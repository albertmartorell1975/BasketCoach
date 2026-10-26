package com.martorell.albert.basketcoach.data.mapper

import com.martorell.albert.basketcoach.data.network.dto.InsightDto
import com.martorell.albert.basketcoach.data.network.dto.InsightMetricDto
import com.martorell.albert.basketcoach.domain.model.InsightCategory
import com.martorell.albert.basketcoach.domain.model.InsightType
import com.martorell.albert.basketcoach.domain.model.MetricSentiment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class AiAnalysisMapperTest {

    @Test
    fun mapsFullInsightDtoWithMetricsCorrectly() {
        val dto = InsightDto(
            id = "ins_paint_eff",
            title = "Eficiència en Tir Interior",
            description = "Alta productivitat en finalitzacions sota cistella",
            category = "OFFENSE",
            relatedGameRounds = listOf(1, 3, 7),
            type = "STRENGTH",
            contextTag = "Fase Ofensiva · Pintura",
            metric = InsightMetricDto(
                label = "Percentatge T2 Zona",
                value = "58.4%",
                unit = "%",
                deltaText = "+8.2% sobre mitjana lliga",
                sentiment = "POSITIVE",
                evidenceLabel = "Punts / Pos.",
                evidenceValue = "1.24 pts/pos",
                datasetKey = "dataset:paint_eff"
            ),
            footerNote = "Factor diferencial en 8 de les 9 victòries obtingudes.",
            priority = null,
            exploreChatPrompt = "Com podem mantenir l'eficiència en tir interior contra defenses tancades?"
        )

        val domain = dto.toDomain()

        assertEquals("ins_paint_eff", domain.id)
        assertEquals("Eficiència en Tir Interior", domain.title)
        assertEquals(InsightCategory.OFFENSE, domain.category)
        assertEquals(InsightType.STRENGTH, domain.type)
        assertEquals("Fase Ofensiva · Pintura", domain.contextTag)
        assertNotNull(domain.metric)
        assertEquals("58.4%", domain.metric?.value)
        assertEquals(MetricSentiment.POSITIVE, domain.metric?.sentiment)
        assertEquals("dataset:paint_eff", domain.metric?.datasetKey)
        assertEquals("Factor diferencial en 8 de les 9 victòries obtingudes.", domain.footerNote)
        assertNull(domain.priority)
        assertEquals("Com podem mantenir l'eficiència en tir interior contra defenses tancades?", domain.exploreChatPrompt)
    }

    @Test
    fun mapsImprovementAreaWithPriority() {
        val dto = InsightDto(
            id = "ins_def_trans",
            title = "Balanç Defensiu en Transició",
            description = "Poca comunicació en el retrocés defensiu",
            category = "DEFENSE",
            relatedGameRounds = listOf(2, 4, 6),
            type = "IMPROVEMENT",
            contextTag = "Transició · Balanç Defensiu",
            metric = InsightMetricDto(
                label = "Punts Contraatac Rivals",
                value = "16.2",
                unit = "pts/partit",
                deltaText = "1.31 pts/pos en transició rival",
                sentiment = "NEGATIVE",
                evidenceLabel = "Prioritat",
                evidenceValue = "Crítica #1",
                datasetKey = "dataset:fastbreak_def"
            ),
            footerNote = "Pilar corrector Fase 2",
            priority = "Crítica #1",
            exploreChatPrompt = "Quins exercicis podem fer per millorar el balanç defensiu en transició?"
        )

        val domain = dto.toDomain()

        assertEquals(InsightType.IMPROVEMENT, domain.type)
        assertEquals("Crítica #1", domain.priority)
        assertEquals(MetricSentiment.NEGATIVE, domain.metric?.sentiment)
        assertEquals("Pilar corrector Fase 2", domain.footerNote)
    }
}
