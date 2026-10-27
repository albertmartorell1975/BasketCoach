package com.martorell.albert.basketcoach.ui.chat.model

/**
 * Root UI state for the Tablet Single-Thread Chat workbench ([com.martorell.albert.basketcoach.ui.chat.ChatScreen]).
 *
 * Strictly adheres to the default-free and non-nullable contract (Constraint #17).
 *
 * @property metaHeader Context metadata displayed in the top header bar.
 * @property leftPaneMode Active display mode for the left workbench pane ([ChatLeftPaneMode]).
 * @property topicList List of intervention areas and exploration threads.
 * @property activeTopicId Unique identifier of the currently active or selected topic card.
 * @property diagnosticEvidence Empirical diagnostic evidence and court spatial data for the selected topic.
 * @property messages Chronological stream of conversation messages in the right pane.
 * @property inputText Current draft text in the message input field.
 * @property isSending Whether a message dispatch operation is currently in flight.
 */
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
