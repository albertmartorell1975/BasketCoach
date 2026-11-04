package com.martorell.albert.basketcoach.ui.chat.model

import com.martorell.albert.basketcoach.ui.util.UiText

/**
 * Presentation model for an intervention area or reflection topic card in the left workbench pane.
 *
 * Encapsulates tactical metadata for AI-detected improvement areas or the unconstrained open inquiry card.
 * Strictly default-parameter-free and non-nullable (Constraint #17).
 *
 * @property id Unique identifier of the topic or improvement insight.
 * @property badgeText Localized badge label representing the area category or priority (e.g., "Àrea de Millora" or "Lliure").
 * @property isPriority Indicates whether this area is flagged as a high-priority intervention focus.
 * @property isOpenInquiry Indicates whether this card represents the unconstrained open inquiry thread.
 * @property title Localized title of the tactical intervention or inquiry focus.
 * @property description Localized descriptive summary of the tactical challenge or purpose.
 * @property statusPill Localized status pill indicator (e.g., "Actiu", "Pendent", or "Disponible").
 * @property hasMetricPill Flag indicating whether a primary box-score metric pill should be rendered.
 * @property metricPill Localized primary metric text (e.g., "16.8 /partit").
 * @property metricPillIsNegative Indicates whether the metric sentiment is negative (rendered with danger styling).
 * @property exploreChatPrompt Suggested prompt or pre-grounded inquiry trigger when initiating discussion on this topic.
 */
data class ChatTopicCardUiModel(
    val id: String,
    val badgeText: UiText,
    val isPriority: Boolean,
    val isOpenInquiry: Boolean,
    val title: UiText,
    val description: UiText,
    val statusPill: UiText,
    val hasMetricPill: Boolean,
    val metricPill: UiText,
    val metricPillIsNegative: Boolean,
    val exploreChatPrompt: String
)
