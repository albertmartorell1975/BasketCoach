package com.martorell.albert.basketcoach.data.mapper

import com.martorell.albert.basketcoach.data.network.dto.DrillDto
import com.martorell.albert.basketcoach.data.network.dto.InsightDto
import com.martorell.albert.basketcoach.data.network.dto.InsightMetricDto
import com.martorell.albert.basketcoach.domain.model.Drill
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.InsightCategory
import com.martorell.albert.basketcoach.domain.model.InsightMetric
import com.martorell.albert.basketcoach.domain.model.InsightType
import com.martorell.albert.basketcoach.domain.model.MetricSentiment
import java.util.UUID

/**
 * Maps an [InsightDto] into a pure domain [Insight] entity.
 *
 * @param defaultId Fallback identifier used when [InsightDto.id] is blank.
 * @return Fully populated domain [Insight] instance.
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
        metric = metric.toDomain(),
        priority = priority,
        footerNote = footerNote,
        exploreChatPrompt = exploreChatPrompt
    )
}

/**
 * Maps an [InsightMetricDto] into a pure domain [InsightMetric] value object.
 *
 * @return Fully populated domain [InsightMetric] instance.
 */
fun InsightMetricDto.toDomain(): InsightMetric {
    return InsightMetric(
        primaryLabel = primaryLabel,
        primaryValue = primaryValue,
        primarySubValue = primarySubValue,
        secondaryLabel = secondaryLabel,
        secondaryValue = secondaryValue,
        secondaryTag = secondaryTag,
        sentiment = MetricSentiment.fromString(sentiment)
    )
}

/**
 * Maps a [DrillDto] into a pure domain [Drill] entity.
 *
 * @param defaultId Fallback identifier used when [DrillDto.id] is blank.
 * @return Fully populated domain [Drill] instance.
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
