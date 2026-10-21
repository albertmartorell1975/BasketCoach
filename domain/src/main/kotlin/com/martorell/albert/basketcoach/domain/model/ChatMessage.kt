package com.martorell.albert.basketcoach.domain.model

data class ChatMessage(
    val id: String,
    val sender: MessageSender,
    val text: String,
    val timestamp: Long
) {
    val isFromCoach: Boolean
        get() = sender == MessageSender.COACH

    val isFromAi: Boolean
        get() = sender == MessageSender.AI
}
