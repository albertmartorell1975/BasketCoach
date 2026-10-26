package com.martorell.albert.basketcoach.domain.model

data class Insight(
    val id: String,
    val title: String,
    val description: String,
    val category: InsightCategory = InsightCategory.GENERAL,
    val relatedGameRounds: List<Int> = emptyList(),
    val type: InsightType = InsightType.IMPROVEMENT,
    val contextTag: String = "",
    val metric: InsightMetric? = null,
    val footerNote: String? = null,
    val priority: String? = null,
    val exploreChatPrompt: String? = null
)
