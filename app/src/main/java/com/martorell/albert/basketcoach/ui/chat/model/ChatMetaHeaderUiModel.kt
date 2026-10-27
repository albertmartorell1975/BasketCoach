package com.martorell.albert.basketcoach.ui.chat.model

/**
 * Presentation model for the chat context metadata header bar.
 *
 * Displays competitive phase grounding, team category, and Phase 1 record balance.
 *
 * @property spaceTag Primary space descriptor (e.g., "Espai de Reflexió Tàctica · 1a Fase").
 * @property phaseSubtitle Competition subtitle and analyzed round count.
 * @property balancePill Pill text displaying competition record (e.g., "Balanç: 3V - 11D").
 * @property teamPill Pill text displaying team division and category (e.g., "Junior A Masculí").
 */
data class ChatMetaHeaderUiModel(
    val spaceTag: String,
    val phaseSubtitle: String,
    val balancePill: String,
    val teamPill: String
)
