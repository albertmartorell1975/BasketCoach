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
    val insights: List<InsightDto> = emptyList(),
    @SerialName("drills")
    val drills: List<DrillDto> = emptyList()
)

/**
 * DTO representing a single tactical insight.
 *
 * @property id Optional unique identifier; generated if missing.
 * @property title Concise title of the insight.
 * @property description Detailed tactical assessment.
 * @property category Category string ("OFFENSE", "DEFENSE", "SYNERGY", "GENERAL").
 * @property relatedGameRounds Competition round numbers where this pattern was observed.
 */
@Serializable
data class InsightDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("category")
    val category: String,
    @SerialName("relatedGameRounds")
    val relatedGameRounds: List<Int> = emptyList()
)

/**
 * DTO representing a tactical practice drill.
 *
 * @property id Optional unique identifier; generated if missing.
 * @property name Title of the drill.
 * @property objective Primary skill or tactical objective.
 * @property instructions Step-by-step instructions.
 * @property focusArea Basketball focus area.
 */
@Serializable
data class DrillDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("name")
    val name: String,
    @SerialName("objective")
    val objective: String,
    @SerialName("instructions")
    val instructions: List<String> = emptyList(),
    @SerialName("focusArea")
    val focusArea: String
)
