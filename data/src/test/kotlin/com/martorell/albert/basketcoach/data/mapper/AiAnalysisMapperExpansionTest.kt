package com.martorell.albert.basketcoach.data.mapper

import com.martorell.albert.basketcoach.data.network.dto.InsightDto
import com.martorell.albert.basketcoach.data.network.dto.InsightMetricDto
import com.martorell.albert.basketcoach.domain.model.InsightCategory
import com.martorell.albert.basketcoach.domain.model.InsightType
import com.martorell.albert.basketcoach.domain.model.MetricSentiment
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class AiAnalysisMapperExpansionTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun toDomain_mapsRichInsightDtoWithStrengthMetric() {
        val metricDto = InsightMetricDto(
            primaryLabel = "Percentatge T2 Zona",
            primaryValue = "58.4%",
            primarySubValue = "+8.2% sobre mitjana lliga",
            secondaryLabel = "Punts / Pos.",
            secondaryValue = "1.24 pts/pos",
            secondaryTag = "dataset:paint_eff",
            sentiment = "POSITIVE"
        )
        val strengthDto = InsightDto(
            id = "str_1",
            title = "Eficiència en Tir Interior",
            description = "Alta productivitat.",
            category = "OFFENSE",
            relatedGameRounds = listOf(3, 9, 10),
            type = "STRENGTH",
            contextTag = "Fase Ofensiva · Pintura",
            metric = metricDto,
            priority = "",
            footerNote = "Factor diferencial en 3 de les 3 victòries.",
            exploreChatPrompt = ""
        )

        val domain = strengthDto.toDomain()

        assertEquals(InsightType.STRENGTH, domain.type)
        assertEquals("Fase Ofensiva · Pintura", domain.contextTag)
        assertEquals("58.4%", domain.metric.primaryValue)
        assertEquals(MetricSentiment.POSITIVE, domain.metric.sentiment)
        assertEquals("1.24 pts/pos", domain.metric.secondaryValue)
        assertEquals("Factor diferencial en 3 de les 3 victòries.", domain.footerNote)
        assertEquals("", domain.exploreChatPrompt)
    }

    @Test
    fun toDomain_mapsRichInsightDtoWithImprovementMetricAndNegativeSentiment() {
        val metricDto = InsightMetricDto(
            primaryLabel = "Punts Contraatac Rivals",
            primaryValue = "16.2",
            primarySubValue = "1.31 pts/pos en transició rival",
            secondaryLabel = "Prioritat",
            secondaryValue = "Crítica #1",
            secondaryTag = "dataset:fastbreak_def",
            sentiment = "NEGATIVE"
        )
        val improvementDto = InsightDto(
            id = "imp_1",
            title = "Balanç Defensiu en Transició",
            description = "Poca comunicació en el retrocés.",
            category = "DEFENSE",
            relatedGameRounds = listOf(1, 2, 4, 6),
            type = "IMPROVEMENT",
            contextTag = "Transició · Balanç Defensiu",
            metric = metricDto,
            priority = "Crítica #1",
            footerNote = "Pilar corrector Fase 2",
            exploreChatPrompt = "Com podem millorar el balanç defensiu?"
        )

        val domain = improvementDto.toDomain()

        assertEquals(InsightType.IMPROVEMENT, domain.type)
        assertEquals(MetricSentiment.NEGATIVE, domain.metric.sentiment)
        assertEquals("Crítica #1", domain.priority)
        assertEquals("Com podem millorar el balanç defensiu?", domain.exploreChatPrompt)
    }

    @Test
    fun jsonSerialization_roundTripWithMandatoryNonDefaultFields() {
        val jsonPayload = """{
  "id": "ser_1",
  "title": "Turnover Issue",
  "description": "High turnover volume",
  "category": "SYNERGY",
  "relatedGameRounds": [1, 5],
  "type": "IMPROVEMENT",
  "contextTag": "Concentració",
  "metric": {
    "primaryLabel": "TO Ratio",
    "primaryValue": "42%",
    "primarySubValue": "0.51 AST/TO",
    "secondaryLabel": "Impacte",
    "secondaryValue": "6.4 pts",
    "secondaryTag": "dataset:to",
    "sentiment": "NEGATIVE"
  },
  "priority": "Alta",
  "footerNote": "Crític",
  "exploreChatPrompt": "Drills for TO"
}"""

        val decoded = json.decodeFromString<InsightDto>(jsonPayload)
        val domain = decoded.toDomain()

        assertEquals("ser_1", domain.id)
        assertEquals(InsightCategory.SYNERGY, domain.category)
        assertEquals("42%", domain.metric.primaryValue)
        assertEquals(MetricSentiment.NEGATIVE, domain.metric.sentiment)
        assertEquals(InsightType.IMPROVEMENT, domain.type)
    }
}
