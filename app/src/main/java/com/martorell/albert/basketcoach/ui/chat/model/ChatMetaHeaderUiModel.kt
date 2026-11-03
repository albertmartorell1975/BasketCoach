package com.martorell.albert.basketcoach.ui.chat.model

import androidx.annotation.StringRes

/**
 * Presentation model for the chat context metadata header bar.
 *
 * Displays competitive phase grounding, team category, and Phase 1 record balance.
 * Strictly default-parameter-free and non-nullable (Constraint #17).
 *
 * @property spaceTagRes String resource ID for space title.
 * @property phaseSubtitleRes String resource ID for competition subtitle.
 * @property wins Total Phase 1 wins.
 * @property losses Total Phase 1 losses.
 * @property teamPillRes String resource ID for team division pill.
 */
data class ChatMetaHeaderUiModel(
    @param:StringRes val spaceTagRes: Int,
    @param:StringRes val phaseSubtitleRes: Int,
    val wins: Int,
    val losses: Int,
    @param:StringRes val teamPillRes: Int
)
