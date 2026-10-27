package com.martorell.albert.basketcoach.ui.chat.model

/**
 * Presentation model for an intervention area or reflection topic card in the left workbench pane.
 *
 * @property id Unique topic identifier.
 * @property priorityBadge Priority tag label (e.g., "ÀREA #1 · PRIORITÀRIA" or "CONSULTA LLIURE").
 * @property isPriority Whether this topic represents an urgent tactical intervention area.
 * @property isOpenInquiry Whether this card represents an open general inquiry stream.
 * @property title Primary tactical topic title.
 * @property description Brief diagnostic summary or guidance text.
 * @property statusPill Workflow status badge text (e.g., "Fil obert · 1 acord preliminar").
 * @property metricPill Accompanying empirical metric label (e.g., "-8.4 net").
 * @property metricPillIsNegative Whether the metric value indicates a negative team performance delta.
 */
data class ChatTopicCardUiModel(
    val id: String,
    val priorityBadge: String,
    val isPriority: Boolean,
    val isOpenInquiry: Boolean,
    val title: String,
    val description: String,
    val statusPill: String,
    val metricPill: String,
    val metricPillIsNegative: Boolean
)
