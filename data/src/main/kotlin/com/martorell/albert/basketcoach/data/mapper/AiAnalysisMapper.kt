package com.martorell.albert.basketcoach.data.mapper

import com.martorell.albert.basketcoach.data.network.dto.DrillDto
import com.martorell.albert.basketcoach.data.network.dto.InsightDto
import com.martorell.albert.basketcoach.domain.model.Drill
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.InsightCategory
import java.util.UUID

/**
 * Converts [InsightDto] to domain [Insight].
 */
fun InsightDto.toDomain(defaultId: String = UUID.randomUUID().toString()): Insight {
    val domainCategory = when (category.trim().uppercase()) {
        "OFFENSE" -> InsightCategory.OFFENSE
        "DEFENSE" -> InsightCategory.DEFENSE
        "SYNERGY" -> InsightCategory.SYNERGY
        else -> InsightCategory.GENERAL
    }

    return Insight(
        id = id?.takeIf { it.isNotBlank() } ?: defaultId,
        title = title,
        description = description,
        category = domainCategory,
        relatedGameRounds = relatedGameRounds
    )
}

/**
 * Converts [DrillDto] to domain [Drill].
 */
fun DrillDto.toDomain(defaultId: String = UUID.randomUUID().toString()): Drill {
    return Drill(
        id = id?.takeIf { it.isNotBlank() } ?: defaultId,
        name = name,
        objective = objective,
        instructions = instructions,
        focusArea = focusArea
    )
}
