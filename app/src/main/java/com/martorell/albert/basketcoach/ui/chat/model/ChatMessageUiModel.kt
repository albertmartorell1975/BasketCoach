package com.martorell.albert.basketcoach.ui.chat.model

import androidx.annotation.StringRes
import com.martorell.albert.basketcoach.domain.model.MessageSender

/**
 * Presentation model representing an individual message bubble in the conversation stream.
 *
 * Supports both Coach Jordi reflections and AI assistant Socratic inquiries, interactive drill
 * recommendation cards, and synthesized training commitment summary cards.
 * Strictly default-parameter-free and non-nullable (Constraint #17).
 *
 * @property id Unique message identifier.
 * @property sender Role of the sender ([MessageSender.COACH] or [MessageSender.AI]).
 * @property senderNameRes String resource ID of the sender's display name.
 * @property senderRoleRes String resource ID of the sender role badge.
 * @property timestamp Time label when the message was sent or received.
 * @property text Body markdown/plain text of the message.
 * @property hasCalloutQuestion Whether this message contains an embedded tactical question callout.
 * @property calloutQuestionText Bold question text for the callout card.
 * @property calloutSubText Contextual coaching guidance or empirical observation.
 * @property hasDrillOptions Whether this message offers interactive formative drill choices.
 * @property drillOptions List of formative drill options presented for selection.
 * @property hasSummaryCard Whether this message concludes with a synthesized microcycle summary card.
 * @property summaryCard Synthesized training focus summary details ([ChatSummaryCardUiModel.EMPTY] if absent).
 */
data class ChatMessageUiModel(
    val id: String,
    val sender: MessageSender,
    @param:StringRes val senderNameRes: Int,
    @param:StringRes val senderRoleRes: Int,
    val timestamp: String,
    val text: String,
    val hasCalloutQuestion: Boolean,
    val calloutQuestionText: String,
    val calloutSubText: String,
    val hasDrillOptions: Boolean,
    val drillOptions: List<ChatDrillOptionUiModel>,
    val hasSummaryCard: Boolean,
    val summaryCard: ChatSummaryCardUiModel
)
