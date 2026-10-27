package com.martorell.albert.basketcoach.ui.chat.model

import com.martorell.albert.basketcoach.domain.model.MessageSender

/**
 * Presentation model representing an individual message bubble in the conversation stream.
 *
 * Supports both Coach Jordi reflections and AI assistant Socratic inquiries, interactive drill
 * recommendation cards, and synthesized training commitment summary cards.
 *
 * @property id Unique message identifier.
 * @property sender Role of the sender ([MessageSender.COACH] or [MessageSender.AI]).
 * @property senderName Display name of the sender.
 * @property senderRole Badge title of the sender role.
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
    val senderName: String,
    val senderRole: String,
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
