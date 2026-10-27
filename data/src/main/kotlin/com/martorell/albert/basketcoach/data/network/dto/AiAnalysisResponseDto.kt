package com.martorell.albert.basketcoach.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Root network response DTO returned by the AI inference service for tactical analysis.
 *
 * @property insights Collection of tactical strengths and areas of improvement ([InsightDto]).
 * @property drills Collection of practice drill recommendations ([DrillDto]).
 */
@Serializable
data class AiAnalysisResponseDto(
    @SerialName("insights")
    val insights: List<InsightDto>,
    @SerialName("drills")
    val drills: List<DrillDto>
)

/**
 * Network Data Transfer Object representing a single tactical insight.
 *
 * Adheres strictly to Constraint #14 with mandatory non-nullable properties.
 *
 * @property id Unique identifier of the insight.
 * @property title Tactical headline summarizing the finding.
 * @property description Detailed tactical analysis describing the finding.
 * @property category Category classification string matching [InsightCategory].
 * @property relatedGameRounds Competition rounds supporting this insight.
 * @property type Insight classification string ("STRENGTH" or "IMPROVEMENT").
 * @property contextTag Contextual game phase tag.
 * @property metric Empirical statistical container ([InsightMetricDto]).
 * @property priority Urgency or priority tag.
 * @property footerNote Explanatory note or evidence summary for the footer.
 * @property exploreChatPrompt Grounding prompt dispatched to conversational AI.
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
    val relatedGameRounds: List<Int>,
    @SerialName("type")
    val type: String,
    @SerialName("contextTag")
    val contextTag: String,
    @SerialName("metric")
    val metric: InsightMetricDto,
    @SerialName("priority")
    val priority: String,
    @SerialName("footerNote")
    val footerNote: String,
    @SerialName("exploreChatPrompt")
    val exploreChatPrompt: String
)

/**
 * Network Data Transfer Object representing a recommended practice drill.
 *
 * @property id Unique identifier of the drill.
 * @property name Title of the training drill.
 * @property objective Coaching objective and tactical purpose of the exercise.
 * @property instructions Step-by-step coaching instructions for running the drill.
 * @property focusArea Primary tactical focus area addressed by this drill.
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
