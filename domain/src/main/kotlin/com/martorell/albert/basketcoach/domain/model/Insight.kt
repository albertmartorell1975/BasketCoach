package com.martorell.albert.basketcoach.domain.model

data class Insight(
    val id: String,
    val title: String,
    val description: String,
    val category: InsightCategory,
    val relatedGameRounds: List<Int> = emptyList()
)
