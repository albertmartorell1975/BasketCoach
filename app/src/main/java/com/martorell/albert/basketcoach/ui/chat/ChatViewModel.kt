package com.martorell.albert.basketcoach.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.InsightType
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.repository.OPEN_INQUIRY_TOPIC_ID
import com.martorell.albert.basketcoach.ui.chat.mapper.ChatUiMapper
import com.martorell.albert.basketcoach.ui.chat.model.ChatDrillOptionUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatTopicCardUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatUiContent
import com.martorell.albert.basketcoach.ui.chat.model.ChatUiState
import com.martorell.albert.basketcoach.usecases.ai.GenerateInsightsUseCase
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
 * ViewModel orchestrating state and user interactions for the topic-scoped tactical Chat workbench.
 *
 * Adheres strictly to the **Passive Initialization Mandate** (`viewmodel-architecture-governance`):
 * performs zero work in `init`. Initial UI state starts with [ChatUiContent.Loading] and
 * data loading is deferred until [loadChat] is explicitly triggered by Compose
 * (e.g., via `LaunchedEffect(initialPrompt)` in `ChatScreen`).
 *
 * Coordinates:
 * - Dynamic loading of AI-detected improvement areas ([InsightType.IMPROVEMENT]) via [GenerateInsightsUseCase]
 *   sharing the in-memory cache populated by `AnalysisScreen`.
 * - Topic-scoped conversation threads isolated by `topicId: String`.
 * - "Explorar al Xat" hand-off from `AnalysisScreen`, matching `initialPrompt` against improvement topics and auto-dispatching.
 * - The **Hybrid UI State Pattern** ([ChatUiState] wrapping [ChatUiContent] alongside the additive [ChatUiState.isSending]
 *   in-flight overlay flag) with atomic state updates via [MutableStateFlow.update].
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getPhase1GamesUseCase: GetPhase1GamesUseCase,
    private val generateInsightsUseCase: GenerateInsightsUseCase,
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
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var cachedGames: List<Game> = emptyList()
    private var cachedInsights: List<Insight> = emptyList()
    private var initialPromptProcessed: Boolean = false

    /**
     * Passively initializes or refreshes the chat workbench.
     *
     * Loads Phase 1 games and AI tactical insights, resolves the active topic (matching [initialPrompt]
     * if provided via "Explorar al Xat"), loads that topic's message thread, and automatically dispatches
     * [initialPrompt] if the thread has not yet processed it.
     *
     * @param initialPrompt Optional pre-grounded prompt or topic identifier passed from navigation.
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

            if (cachedInsights.isEmpty()) {
                when (val insightsResult = generateInsightsUseCase()) {
                    is Result.Error -> {
                        _uiState.update { it.copy(content = ChatUiContent.Error(insightsResult.error)) }
                        return@launch
                    }
                    is Result.Success -> {
                        cachedInsights = insightsResult.data
                    }
                }
            }

            val targetTopicId = if (initialPrompt.isNotBlank()) {
                val matchingInsight = cachedInsights
                    .filter { it.type == InsightType.IMPROVEMENT }
                    .find { it.exploreChatPrompt == initialPrompt || it.id == initialPrompt }
                matchingInsight?.id
                    ?: cachedInsights.firstOrNull { it.type == InsightType.IMPROVEMENT }?.id
                    ?: OPEN_INQUIRY_TOPIC_ID
            } else {
                val currentSuccess = _uiState.value.content as? ChatUiContent.Success
                currentSuccess?.activeTopicId
                    ?: cachedInsights.firstOrNull { it.type == InsightType.IMPROVEMENT }?.id
                    ?: OPEN_INQUIRY_TOPIC_ID
            }

            when (val messagesResult = getChatMessagesUseCase(targetTopicId)) {
                is Result.Error -> {
                    _uiState.update { it.copy(content = ChatUiContent.Error(messagesResult.error)) }
                }
                is Result.Success -> {
                    val content = chatUiMapper.mapToContent(
                        games = cachedGames,
                        insights = cachedInsights,
                        messages = messagesResult.data,
                        activeTopicId = targetTopicId,
                        inputText = ""
                    )

                    _uiState.update {
                        it.copy(content = content)
                    }

                    if (initialPrompt.isNotBlank() && !initialPromptProcessed) {
                        initialPromptProcessed = true
                        if (messagesResult.data.isEmpty()) {
                            sendMessage(initialPrompt)
                        }
                    }
                }
            }
        }
    }

    /**
     * Submits a message to the AI coaching assistant within the currently active topic thread.
     *
     * Short-circuits if the message is blank or if another message is already in flight ([ChatUiState.isSending]).
     *
     * @param messageText Optional explicit message text; defaults to the current [ChatUiContent.Success.inputText].
     */
    fun sendMessage(messageText: String = "") {
        val currentSuccess = _uiState.value.content as? ChatUiContent.Success
        val activeTopicId = currentSuccess?.activeTopicId ?: OPEN_INQUIRY_TOPIC_ID
        val textToSend = if (messageText.isNotBlank()) messageText else currentSuccess?.inputText.orEmpty()
        val trimmed = textToSend.trim()

        if (trimmed.isBlank() || _uiState.value.isSending) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSending = true) }

            when (val sendResult = sendChatMessageUseCase(trimmed, activeTopicId)) {
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isSending = false,
                            content = ChatUiContent.Error(sendResult.error)
                        )
                    }
                }
                is Result.Success -> {
                    when (val reloadResult = getChatMessagesUseCase(activeTopicId)) {
                        is Result.Error -> {
                            _uiState.update {
                                it.copy(
                                    isSending = false,
                                    content = ChatUiContent.Error(reloadResult.error)
                                )
                            }
                        }
                        is Result.Success -> {
                            val updatedContent = chatUiMapper.mapToContent(
                                games = cachedGames,
                                insights = cachedInsights,
                                messages = reloadResult.data,
                                activeTopicId = activeTopicId,
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
     * Updates the draft text in the message input field.
     *
     * @param newText Updated draft string entered by the coach.
     */
    fun onInputTextChanged(newText: String) {
        val currentSuccess = _uiState.value.content as? ChatUiContent.Success ?: return
        _uiState.update {
            it.copy(content = currentSuccess.copy(inputText = newText))
        }
    }

    /**
     * Switches the active conversation thread to the selected [topic] without leaving the workbench view.
     *
     * Loads the persisted in-memory message history for [topic.id] and preserves the coach's current input draft.
     *
     * @param topic The selected [ChatTopicCardUiModel].
     */
    fun onTopicSelected(topic: ChatTopicCardUiModel) {
        val currentSuccess = _uiState.value.content as? ChatUiContent.Success ?: return
        if (currentSuccess.activeTopicId == topic.id) return

        viewModelScope.launch {
            when (val messagesResult = getChatMessagesUseCase(topic.id)) {
                is Result.Error -> {
                    _uiState.update { it.copy(content = ChatUiContent.Error(messagesResult.error)) }
                }
                is Result.Success -> {
                    val updatedContent = chatUiMapper.mapToContent(
                        games = cachedGames,
                        insights = cachedInsights,
                        messages = messagesResult.data,
                        activeTopicId = topic.id,
                        inputText = currentSuccess.inputText
                    )
                    _uiState.update {
                        it.copy(content = updatedContent)
                    }
                }
            }
        }
    }

    /**
     * Submits a predefined drill option selected by the coach from a Socratic assistant callout card.
     *
     * @param drill The selected [ChatDrillOptionUiModel].
     */
    fun onDrillOptionSelected(drill: ChatDrillOptionUiModel) {
        val prompt = "${drill.optionTag}: ${drill.title}"
        sendMessage(prompt)
    }

    /**
     * Retries loading the chat workbench following an error state.
     */
    fun onRetry() {
        loadChat()
    }
}
