package com.martorell.albert.basketcoach.domain.model

/**
 * Identifies the participant originating a chat message in the single-thread assistant.
 */
enum class MessageSender {
    /** The basketball coach interacting with the assistant. */
    COACH,

    /** The AI basketball coaching assistant generating responses. */
    AI
}
