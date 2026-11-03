package com.martorell.albert.basketcoach.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network request payload for the DevExpert Jev Decisions endpoint (`POST /decisions`).
 *
 * Used by the 2-step AI guardrail pipeline to pre-classify coach messages before invoking
 * the heavy generative chat completions model.
 *
 * @property model Target decision model identifier (typically [com.martorell.albert.basketcoach.data.network.config.DevExpertConfig.MODEL_JEV]).
 * @property state Contextual state key-value pairs (e.g. `app_domain`, `last_assistant_message`, `coach_message`).
 * @property questions Map of evaluation question keys to their respective [JevQuestionDto] specifications.
 */
@Serializable
data class JevDecisionRequestDto(
    @SerialName("model")
    val model: String,
    @SerialName("state")
    val state: Map<String, String>,
    @SerialName("questions")
    val questions: Map<String, JevQuestionDto>
)

/**
 * Specification of a decision question evaluated by DevExpert Jev.
 *
 * @property type Question type (e.g. `"choice"`).
 * @property instructions Guiding instructions for evaluating the decision.
 * @property criteria Map of available choice keys to their evaluation criteria descriptions.
 */
@Serializable
data class JevQuestionDto(
    @SerialName("type")
    val type: String,
    @SerialName("instructions")
    val instructions: String,
    @SerialName("criteria")
    val criteria: Map<String, String>
)

/**
 * Network response payload from the DevExpert Jev Decisions endpoint (`POST /decisions`).
 *
 * @property answers Map of evaluated question keys to their respective [JevAnswerDto] outcomes.
 */
@Serializable
data class JevDecisionResponseDto(
    @SerialName("answers")
    val answers: Map<String, JevAnswerDto>
)

/**
 * Result choice selected by DevExpert Jev for a specific question.
 *
 * @property choice Evaluated choice string (e.g. `"basketball_relevant"` or `"off_topic"`).
 */
@Serializable
data class JevAnswerDto(
    @SerialName("choice")
    val choice: String
)
