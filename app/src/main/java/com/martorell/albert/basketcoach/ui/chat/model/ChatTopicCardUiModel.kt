package com.martorell.albert.basketcoach.ui.chat.model

import androidx.annotation.StringRes

/**
 * Presentation model for an intervention area or reflection topic card in the left workbench pane.
 *
 * @property id Unique topic identifier.
 * @property priorityBadgeRes Resource ID for the priority tag label.
 * @property isPriority Whether this topic represents an urgent tactical intervention area.
 * @property isOpenInquiry Whether this card represents an open general inquiry stream.
 * @property titleRes Resource ID for the primary tactical topic title.
 * @property descriptionRes Resource ID for the brief diagnostic summary or guidance text.
 * @property statusPillRes Resource ID for the workflow status badge text.
 * @property hasMetricPill Whether this card displays an accompanying empirical metric pill.
 * @property metricPillRes Resource ID for the accompanying empirical metric label.
 * @property metricPillIsNegative Whether the metric value indicates a negative team performance delta.
 * @property hasDiagnosticEvidence Whether this topic features an empirical half-court diagnostic breakdown view.
 */
data class ChatTopicCardUiModel(
    val id: String,
    @param:StringRes val priorityBadgeRes: Int,
    val isPriority: Boolean,
    val isOpenInquiry: Boolean,
    @param:StringRes val titleRes: Int,
    @param:StringRes val descriptionRes: Int,
    @param:StringRes val statusPillRes: Int,
    val hasMetricPill: Boolean,
    @param:StringRes val metricPillRes: Int,
    val metricPillIsNegative: Boolean,
    val hasDiagnosticEvidence: Boolean
)
