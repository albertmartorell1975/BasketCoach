package com.martorell.albert.basketcoach.ui.chat.model

data class ChatDrillOptionUiModel(
    val id: String,
    val optionTag: String,
    val intensityTag: String,
    val isHighIntensity: Boolean,
    val title: String,
    val description: String,
    val actionButtonText: String
)
