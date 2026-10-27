package com.martorell.albert.basketcoach.ui.chat.model

/**
 * Presentation model representing a synthesized Phase 2 training commitment card.
 *
 * Displays the agreed formative practice focus, competitive objective, and microcycle duration.
 *
 * @property title Primary title of the integrated training commitment.
 * @property badgeText Drill identifier badge text (e.g., "3c3 Crash").
 * @property description Core pedagogical execution rule for the upcoming microcycle.
 * @property objectiveText Concrete measurable statistical target (e.g., "< 9.5 pts 2a oportunitat").
 * @property microcycleText Target timeframe and weekly training schedule.
 */
data class ChatSummaryCardUiModel(
    val title: String,
    val badgeText: String,
    val description: String,
    val objectiveText: String,
    val microcycleText: String
) {
    companion object {
        /** Sentinel empty summary card representing absent summary state without nullables. */
        val EMPTY = ChatSummaryCardUiModel(
            title = "",
            badgeText = "",
            description = "",
            objectiveText = "",
            microcycleText = ""
        )
    }
}
