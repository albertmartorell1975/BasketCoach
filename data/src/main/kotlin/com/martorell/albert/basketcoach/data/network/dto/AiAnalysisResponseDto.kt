package com.martorell.albert.basketcoach.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Structured DTO representing AI-generated tactical analysis and recommended practice drills.
 * All fields are strictly mandatory.
 *
 * @property insights Analytical findings extracted from past games.
 * @property drills Actionable practice drills targeting identified weaknesses or strengths.
 */
@Serializable
data class AiAnalysisResponseDto(
    @SerialName("insights")
    val insights: List<InsightDto>,
    @SerialName("drills")
    val drills: List<DrillDto>
)

/**
 * DTO representing a single tactical insight.
 * All fields are strictly mandatory.
 *
 * @property id Unique identifier.
 * @property title Concise title of the insight.
 * @property description Detailed tactical assessment.
 * @property category Category string ("OFFENSE", "DEFENSE", "SYNERGY", "GENERAL").
 * @property relatedGameRounds Competition round numbers where this pattern was observed.
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
    val relatedGameRounds: List<Int>
)

/**
 * DTO representing a tactical practice drill.
 * All fields are strictly mandatory.
 *
 * @property id Unique identifier.
 * @property name Title of the drill.
 * @property objective Primary skill or tactical objective.
 * @property instructions Step-by-step instructions.
 * @property focusArea Basketball focus area.
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
