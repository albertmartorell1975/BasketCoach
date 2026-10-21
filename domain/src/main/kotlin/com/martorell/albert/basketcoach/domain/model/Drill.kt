package com.martorell.albert.basketcoach.domain.model

data class Drill(
    val id: String,
    val name: String,
    val objective: String,
    val instructions: List<String>,
    val focusArea: String
)
