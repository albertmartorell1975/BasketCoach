package com.martorell.albert.basketcoach.ui.chat.model

import com.martorell.albert.basketcoach.domain.model.DomainError

/**
 * Root UI state for the Tablet Single-Thread Chat workbench, adhering to the Hybrid UI State Pattern.
 *
 * Couples message in-flight interaction state ([isSending]) with discrete content states ([content]).
 * Strictly default-parameter-free and non-nullable (Constraint #17).
 *
 * @property content The active content state ([ChatUiContent.Loading], [ChatUiContent.Success], or [ChatUiContent.Error]).
 * @property isSending Flag indicating whether a chat completion query is currently in flight.
 */
data class ChatUiState(
    val content: ChatUiContent,
    val isSending: Boolean
)

/**
 * Sealed hierarchy representing mutually exclusive content states for the chat workbench.
 */
sealed interface ChatUiContent {
    /**
     * Initial asynchronous loading state while fetching matches and chat history.
     */
    data object Loading : ChatUiContent

    /**
     * Successfully initialized chat workbench state with active conversation stream and contextual panels.
     *
     * @property metaHeader Context metadata displayed in the top header bar.
     * @property topicList List of intervention areas and exploration threads.
     * @property activeTopicId Unique identifier of the currently active or selected topic card.
     * @property messages Chronological stream of conversation messages in the right pane.
     * @property inputText Current draft text in the message input field.
     */
    data class Success(
        val metaHeader: ChatMetaHeaderUiModel,
        val topicList: List<ChatTopicCardUiModel>,
        val activeTopicId: String,
        val messages: List<ChatMessageUiModel>,
        val inputText: String
    ) : ChatUiContent

    /**
     * Error state encountered during initial load or unrecoverable network failure.
     *
     * @property error The underlying typed domain error.
     */
    data class Error(val error: DomainError) : ChatUiContent
}
