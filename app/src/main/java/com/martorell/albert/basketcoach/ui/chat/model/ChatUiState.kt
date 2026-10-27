package com.martorell.albert.basketcoach.ui.chat.model

data class ChatUiState(
    val metaHeader: ChatMetaHeaderUiModel,
    val leftPaneMode: ChatLeftPaneMode,
    val topicList: List<ChatTopicCardUiModel>,
    val activeTopicId: String,
    val diagnosticEvidence: ChatDiagnosticEvidenceUiModel,
    val messages: List<ChatMessageUiModel>,
    val inputText: String,
    val isSending: Boolean
)
