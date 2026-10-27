package com.martorell.albert.basketcoach.ui.chat.model

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
