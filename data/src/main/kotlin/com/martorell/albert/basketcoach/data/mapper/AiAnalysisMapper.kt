package com.martorell.albert.basketcoach.data.mapper

import com.martorell.albert.basketcoach.data.network.dto.DrillDto
import com.martorell.albert.basketcoach.data.network.dto.InsightDto
import com.martorell.albert.basketcoach.domain.model.Drill
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.InsightCategory
import com.martorell.albert.basketcoach.domain.model.InsightMetric
import com.martorell.albert.basketcoach.domain.model.InsightType
import com.martorell.albert.basketcoach.domain.model.MetricSentiment
import java.util.UUID

/**
 * Converts [InsightDto] to domain [Insight].
 */
fun InsightDto.toDomain(defaultId: String = UUID.randomUUID().toString()): Insight {
    return Insight(
        id = id.ifBlank { defaultId },
        title = title,
        description = description,
        category = InsightCategory.fromString(category),
        relatedGameRounds = relatedGameRounds,
        type = InsightType.fromString(type),
        contextTag = contextTag,
        metric = metric?.let {
            InsightMetric(
                label = it.label,
                value = it.value,
                unit = it.unit,
                deltaText = it.deltaText,
                sentiment = MetricSentiment.fromString(it.sentiment),
                evidenceLabel = it.evidenceLabel,
                evidenceValue = it.evidenceValue,
                datasetKey = it.datasetKey,
                comparison = it.comparison,
                delta = it.delta
            )
        },
        footerNote = footerNote,
        priority = priority,
        exploreChatPrompt = exploreChatPrompt
    )
}

/**
 * Converts [DrillDto] to domain [Drill].
 */
fun DrillDto.toDomain(defaultId: String = UUID.randomUUID().toString()): Drill {
    return Drill(
        id = id.ifBlank { defaultId },
        name = name,
        objective = objective,
        instructions = instructions,
        focusArea = focusArea
    )
}
