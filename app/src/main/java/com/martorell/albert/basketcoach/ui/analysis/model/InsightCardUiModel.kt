package com.martorell.albert.basketcoach.ui.analysis.model

import com.martorell.albert.basketcoach.domain.model.InsightType
import com.martorell.albert.basketcoach.domain.model.MetricSentiment

/**
 * UI presentation state representing a single tactical insight card.
 *
 * Strictly adheres to Constraint #17 (zero default parameter values, zero nullable fields).
 *
 * @property id Unique identifier for card item keys in Compose layouts.
 * @property type Insight classification ([InsightType]) driving card border styling and accent colors.
 * @property categoryBadgeText Localized badge label (e.g., "Fortalesa Tàctica" or "Àrea de Millora").
 * @property contextTag Contextual game phase tag displayed on the top right.
 * @property title Bold headline summarizing the tactical finding.
 * @property description Detailed tactical analysis text.
 * @property primaryMetricLabel Label for the primary statistical metric.
 * @property primaryMetricValue Formatted primary metric value (e.g., "58.4%").
 * @property primaryMetricSubValue Contextual delta or sub-value for the primary metric.
 * @property secondaryMetricLabel Label for the secondary benchmark metric.
 * @property secondaryMetricValue Formatted secondary benchmark value.
 * @property secondaryMetricTag Technical dataset tag (e.g., "dataset:paint_eff").
 * @property metricSentiment Sentiment governing color highlights of the primary sub-value.
 * @property footerNote Explanatory note or evidence summary displayed in the card footer.
 * @property canExploreChat Indicates whether the "Explorar al Xat" action button is displayed.
 * @property exploreChatPrompt Tactical prompt dispatched to the chat assistant upon button click.
 */
data class InsightCardUiModel(
    val id: String,
    val type: InsightType,
    val categoryBadgeText: String,
    val contextTag: String,
    val title: String,
    val description: String,
    val primaryMetricLabel: String,
    val primaryMetricValue: String,
    val primaryMetricSubValue: String,
    val secondaryMetricLabel: String,
    val secondaryMetricValue: String,
    val secondaryMetricTag: String,
    val metricSentiment: MetricSentiment,
    val footerNote: String,
    val canExploreChat: Boolean,
    val exploreChatPrompt: String
)
