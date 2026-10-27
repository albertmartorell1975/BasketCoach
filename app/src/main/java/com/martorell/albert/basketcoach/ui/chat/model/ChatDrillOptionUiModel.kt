package com.martorell.albert.basketcoach.ui.chat.model

/**
 * Presentation model for a formative drill recommendation card presented within an AI message.
 *
 * Enables Coach Jordi to evaluate alternative pedagogical drills (e.g., Option A vs Option B)
 * and select one directly within the conversation thread.
 *
 * @property id Unique drill option identifier.
 * @property optionTag Option badge text (e.g., "Opció A").
 * @property intensityTag Training intensity descriptor (e.g., "Intensitat Alta" or "Continuïtat").
 * @property isHighIntensity Whether this drill is flagged as high physical/tactical intensity.
 * @property title Name of the practice drill.
 * @property description Detailed tactical rule and pedagogical execution instructions.
 * @property actionButtonText Text for the selection action button.
 */
data class ChatDrillOptionUiModel(
    val id: String,
    val optionTag: String,
    val intensityTag: String,
    val isHighIntensity: Boolean,
    val title: String,
    val description: String,
    val actionButtonText: String
)
