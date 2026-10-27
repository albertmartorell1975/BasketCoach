package com.martorell.albert.basketcoach.ui.chat.model

import com.martorell.albert.basketcoach.domain.model.MessageSender

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
