package com.martorell.albert.basketcoach.domain.model

/**
 * Tactical practice drill recommended to address identified performance weaknesses or reinforce strengths.
 *
 * @property id Unique identifier for the drill.
 * @property name Descriptive drill title.
 * @property objective Primary basketball skill or tactical goal addressed by the drill.
 * @property instructions Ordered step-by-step guidance for running the drill on the court.
 * @property focusArea Basketball domain focus area (e.g. "Defensive Transition & Rebounding", "Ball Protection").
 */
data class Drill(
    val id: String,
    val name: String,
    val objective: String,
    val instructions: List<String>,
    val focusArea: String
)
