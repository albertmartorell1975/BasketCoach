package com.martorell.albert.basketcoach.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JevDecisionRequestDto(
    @SerialName("model")
    val model: String,
    @SerialName("state")
    val state: Map<String, String>,
    @SerialName("questions")
    val questions: Map<String, JevQuestionDto>
)

@Serializable
data class JevQuestionDto(
    @SerialName("type")
    val type: String,
    @SerialName("instructions")
    val instructions: String,
    @SerialName("criteria")
    val criteria: Map<String, String>
)

@Serializable
data class JevDecisionResponseDto(
    @SerialName("answers")
    val answers: Map<String, JevAnswerDto>
)

@Serializable
data class JevAnswerDto(
    @SerialName("choice")
    val choice: String
)
