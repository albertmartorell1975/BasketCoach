package com.martorell.albert.basketcoach.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Structured DTO representing AI-generated tactical analysis and recommended practice drills.
 *
 * @property insights Analytical findings extracted from past games.
 * @property drills Actionable practice drills targeting identified weaknesses or strengths.
 */
@Serializable
data class AiAnalysisResponseDto(
    @SerialName("insights")
    val insights: List<InsightDto>,
    @SerialName("drills")
    val drills: List<DrillDto> = emptyList()
)

/**
 * Structured metric data attached to a tactical insight.
 */
@Serializable
data class InsightMetricDto(
    @SerialName("label")
    val label: String,
    @SerialName("value")
    val value: String,
    @SerialName("unit")
    val unit: String = "",
    @SerialName("deltaText")
    val deltaText: String? = null,
    @SerialName("sentiment")
    val sentiment: String = "NEUTRAL",
    @SerialName("evidenceLabel")
    val evidenceLabel: String? = null,
    @SerialName("evidenceValue")
    val evidenceValue: String? = null,
    @SerialName("datasetKey")
    val datasetKey: String? = null,
    @SerialName("comparison")
    val comparison: String? = null,
    @SerialName("delta")
    val delta: String? = null
)

/**
 * DTO representing a single tactical insight.
 */
@Serializable
data class InsightDto(
    @SerialName("id")
    val id: String,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("category")
    val category: String,
    @SerialName("relatedGameRounds")
    val relatedGameRounds: List<Int> = emptyList(),
    @SerialName("type")
    val type: String = "IMPROVEMENT",
    @SerialName("contextTag")
    val contextTag: String = "",
    @SerialName("metric")
    val metric: InsightMetricDto? = null,
    @SerialName("footerNote")
    val footerNote: String? = null,
    @SerialName("priority")
    val priority: String? = null,
    @SerialName("exploreChatPrompt")
    val exploreChatPrompt: String? = null
)

/**
 * DTO representing a tactical practice drill.
 */
@Serializable
data class DrillDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("objective")
    val objective: String,
    @SerialName("instructions")
    val instructions: List<String>,
    @SerialName("focusArea")
    val focusArea: String
)
