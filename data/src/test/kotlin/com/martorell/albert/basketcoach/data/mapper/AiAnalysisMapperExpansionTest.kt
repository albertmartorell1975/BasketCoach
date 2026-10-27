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
    fun toDomain_mapsStrengthAndImprovementInsightDtosWithSentiment() {
        val strengthDto = InsightDto(
            id = "str_1",
            title = "Eficiència en Tir Interior",
            description = "Alta productivitat.",
            category = "OFFENSE",
            relatedGameRounds = listOf(3, 9, 10),
            type = "STRENGTH",
            contextTag = "Fase Ofensiva · Pintura",
            metric = InsightMetricDto(
                primaryLabel = "Percentatge T2 Zona",
                primaryValue = "58.4%",
                primarySubValue = "+8.2% sobre mitjana lliga",
                secondaryLabel = "Punts / Pos.",
                secondaryValue = "1.24 pts/pos",
                secondaryTag = "dataset:paint_eff",
                sentiment = "POSITIVE"
            ),
            priority = "",
            footerNote = "Factor diferencial en 3 de les 3 victòries.",
            exploreChatPrompt = ""
        )
        val strengthDomain = strengthDto.toDomain()
        assertEquals(InsightType.STRENGTH, strengthDomain.type)
        assertEquals(MetricSentiment.POSITIVE, strengthDomain.metric.sentiment)
        assertEquals("58.4%", strengthDomain.metric.primaryValue)

        val improvementDto = strengthDto.copy(
            id = "imp_1",
            category = "DEFENSE",
            type = "IMPROVEMENT",
            metric = strengthDto.metric.copy(sentiment = "NEGATIVE"),
            priority = "Crítica #1",
            exploreChatPrompt = "Com podem millorar el balanç defensiu?"
        )
        val improvementDomain = improvementDto.toDomain()
        assertEquals(InsightType.IMPROVEMENT, improvementDomain.type)
        assertEquals(InsightCategory.DEFENSE, improvementDomain.category)
        assertEquals(MetricSentiment.NEGATIVE, improvementDomain.metric.sentiment)
        assertEquals("Crítica #1", improvementDomain.priority)
        assertEquals("Com podem millorar el balanç defensiu?", improvementDomain.exploreChatPrompt)
    }

    @Test
    fun jsonSerialization_decodesMandatoryFieldsAndMapsToDomain() {
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

        val domain = json.decodeFromString<InsightDto>(jsonPayload).toDomain()

        assertEquals("ser_1", domain.id)
        assertEquals(InsightCategory.SYNERGY, domain.category)
        assertEquals("42%", domain.metric.primaryValue)
        assertEquals(MetricSentiment.NEGATIVE, domain.metric.sentiment)
        assertEquals(InsightType.IMPROVEMENT, domain.type)
    }
}
