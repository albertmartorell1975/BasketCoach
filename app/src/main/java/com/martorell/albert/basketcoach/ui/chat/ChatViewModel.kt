package com.martorell.albert.basketcoach.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.ui.chat.mapper.ChatUiMapper
import com.martorell.albert.basketcoach.ui.chat.model.ChatDrillOptionUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatLeftPaneMode
import com.martorell.albert.basketcoach.ui.chat.model.ChatTopicCardUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatTopicId
import com.martorell.albert.basketcoach.ui.chat.model.ChatUiContent
import com.martorell.albert.basketcoach.ui.chat.model.ChatUiState
import com.martorell.albert.basketcoach.usecases.chat.GetChatMessagesUseCase
import com.martorell.albert.basketcoach.usecases.chat.SendChatMessageUseCase
import com.martorell.albert.basketcoach.usecases.game.GetPhase1GamesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel orchestrating state and user interactions for the single-thread tactical Chat screen.
 *
 * Adheres strictly to the **Passive Initialization Mandate** (`viewmodel-architecture-governance`):
 * performs zero work in `init`. Initial UI state starts with [ChatUiContent.Loading] and
 * data loading is deferred until [loadChat] is explicitly triggered by Compose
 * (e.g., via `LaunchedEffect(initialPrompt)` in `ChatScreen`).
 *
 * Manages the **Hybrid UI State Pattern** ([ChatUiState] wrapping [ChatUiContent] alongside
 * the additive [ChatUiState.isSending] in-flight overlay flag) with atomic state updates via
 * [MutableStateFlow.update].
 *
 * Implements the "Explorar al Xat" navigation hand-off: when an [initialPrompt] is passed,
 * sets the left workbench pane to [ChatLeftPaneMode.DIAGNOSTIC_EVIDENCE] and automatically
 * dispatches the prompt once, guarding against duplicate dispatches on recomposition.
 *
 * @param getPhase1GamesUseCase Domain interactor retrieving Phase 1 match history for context grounding.
 * @param getChatMessagesUseCase Domain interactor retrieving recorded conversation messages.
 * @param sendChatMessageUseCase Domain interactor dispatching coach queries and receiving AI responses.
 * @param chatUiMapper Presentation mapper transforming domain data to immutable UI models.
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getPhase1GamesUseCase: GetPhase1GamesUseCase,
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val chatUiMapper: ChatUiMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ChatUiState(
            content = ChatUiContent.Loading,
            isSending = false
        )
    )
    /**
     * Observable [StateFlow] exposing the immutable [ChatUiState] to the presentation layer.
     */
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var cachedGames: List<Game> = emptyList()
    private var initialPromptProcessed: Boolean = false

    /**
     * Passively initializes or refreshes the chat workbench.
     *
     * Fetches Phase 1 match context (caching in-memory) and conversation history.
     * When [initialPrompt] is non-blank (via "Explorar al Xat"), automatically configures
     * the left pane to [ChatLeftPaneMode.DIAGNOSTIC_EVIDENCE] and dispatches the prompt once.
     *
     * @param initialPrompt Pre-grounded tactical question or empty string for standard entry.
     */
    fun loadChat(initialPrompt: String = "") {
        viewModelScope.launch {
            if (cachedGames.isEmpty()) {
                when (val gamesResult = getPhase1GamesUseCase()) {
                    is Result.Error -> {
                        _uiState.update { it.copy(content = ChatUiContent.Error(gamesResult.error)) }
                        return@launch
                    }
                    is Result.Success -> {
                        cachedGames = gamesResult.data
                    }
                }
            }

            when (val messagesResult = getChatMessagesUseCase()) {
                is Result.Error -> {
                    _uiState.update { it.copy(content = ChatUiContent.Error(messagesResult.error)) }
                }
                is Result.Success -> {
                    val targetLeftPane = if (initialPrompt.isNotBlank()) {
                        ChatLeftPaneMode.DIAGNOSTIC_EVIDENCE
                    } else {
                        ChatLeftPaneMode.TOPIC_LIST
                    }

                    val content = chatUiMapper.mapToContent(
                        games = cachedGames,
                        messages = messagesResult.data,
                        leftPaneMode = targetLeftPane,
                        activeTopicId = ChatTopicId.REBOUND_CONTROL,
                        inputText = ""
                    )

                    _uiState.update {
                        it.copy(content = content)
                    }

                    if (initialPrompt.isNotBlank() && !initialPromptProcessed) {
                        initialPromptProcessed = true
                        sendMessage(initialPrompt)
                    }
                }
            }
        }
    }

    /**
     * Dispatches a message to the conversational AI assistant.
     *
     * Incorporates an idempotent concurrency guard (`if (trimmed.isBlank() || _uiState.value.isSending) return`).
     * Sets [ChatUiState.isSending] to true while in flight, reloads conversation messages upon
     * successful dispatch, and updates UI state atomically.
     *
     * @param messageText Explicit text to send, or empty string to use current [ChatUiContent.Success.inputText].
     */
    fun sendMessage(messageText: String = "") {
        val currentSuccess = _uiState.value.content as? ChatUiContent.Success
        val textToSend = if (messageText.isNotBlank()) messageText else currentSuccess?.inputText.orEmpty()
        val trimmed = textToSend.trim()

        if (trimmed.isBlank() || _uiState.value.isSending) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSending = true) }

            when (val sendResult = sendChatMessageUseCase(trimmed)) {
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isSending = false,
                            content = ChatUiContent.Error(sendResult.error)
                        )
                    }
                }
                is Result.Success -> {
                    when (val reloadResult = getChatMessagesUseCase()) {
                        is Result.Error -> {
                            _uiState.update {
                                it.copy(
                                    isSending = false,
                                    content = ChatUiContent.Error(reloadResult.error)
                                )
                            }
                        }
                        is Result.Success -> {
                            val activeLeftPane = currentSuccess?.leftPaneMode ?: ChatLeftPaneMode.TOPIC_LIST
                            val activeTopic = currentSuccess?.activeTopicId ?: ChatTopicId.REBOUND_CONTROL

                            val updatedContent = chatUiMapper.mapToContent(
                                games = cachedGames,
                                messages = reloadResult.data,
                                leftPaneMode = activeLeftPane,
                                activeTopicId = activeTopic,
                                inputText = ""
                            )

                            _uiState.update {
                                it.copy(
                                    isSending = false,
                                    content = updatedContent
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Updates draft input text in the message input field.
     *
     * @param newText Newly typed text value.
     */
    fun onInputTextChanged(newText: String) {
        val currentSuccess = _uiState.value.content as? ChatUiContent.Success ?: return
        _uiState.update {
            it.copy(content = currentSuccess.copy(inputText = newText))
        }
    }

    /**
     * Handles selection of a tactical intervention topic card.
     *
     * Switches left pane mode to [ChatLeftPaneMode.DIAGNOSTIC_EVIDENCE] if the topic contains
     * diagnostic evidence, or back to [ChatLeftPaneMode.TOPIC_LIST] if it does not.
     *
     * @param topic Selected topic card model.
     */
    fun onTopicSelected(topic: ChatTopicCardUiModel) {
        val currentSuccess = _uiState.value.content as? ChatUiContent.Success ?: return
        val targetMode = if (topic.hasDiagnosticEvidence) {
            ChatLeftPaneMode.DIAGNOSTIC_EVIDENCE
        } else {
            ChatLeftPaneMode.TOPIC_LIST
        }
        _uiState.update {
            it.copy(
                content = currentSuccess.copy(
                    activeTopicId = topic.id,
                    leftPaneMode = targetMode
                )
            )
        }
    }

    /**
     * Switches the display mode of the left workbench pane.
     *
     * @param mode Target display mode ([ChatLeftPaneMode.TOPIC_LIST] or [ChatLeftPaneMode.DIAGNOSTIC_EVIDENCE]).
     */
    fun onLeftPaneModeChanged(mode: ChatLeftPaneMode) {
        val currentSuccess = _uiState.value.content as? ChatUiContent.Success ?: return
        _uiState.update {
            it.copy(content = currentSuccess.copy(leftPaneMode = mode))
        }
    }

    /**
     * Handles coach selection of an interactive formative drill option embedded in an AI bubble.
     *
     * Formats the selection prompt (e.g. "Opció A: Tancar el Rebot Defensiu") and dispatches it
     * via [sendMessage].
     *
     * @param drill Selected formative drill option model.
     */
    fun onDrillOptionSelected(drill: ChatDrillOptionUiModel) {
        val prompt = "${drill.optionTag}: ${drill.title}"
        sendMessage(prompt)
    }

    /**
     * Retries loading chat data following an error state.
     */
    fun onRetry() {
        loadChat()
    }
}
