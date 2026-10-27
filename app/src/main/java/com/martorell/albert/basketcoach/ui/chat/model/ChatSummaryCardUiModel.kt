package com.martorell.albert.basketcoach.ui.chat.model

data class ChatSummaryCardUiModel(
    val title: String,
    val badgeText: String,
    val description: String,
    val objectiveText: String,
    val microcycleText: String
) {
    companion object {
        val EMPTY = ChatSummaryCardUiModel(
            title = "",
            badgeText = "",
            description = "",
            objectiveText = "",
            microcycleText = ""
        )
    }
}
