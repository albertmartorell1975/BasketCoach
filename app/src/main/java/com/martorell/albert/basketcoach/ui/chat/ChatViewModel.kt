package com.martorell.albert.basketcoach.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.ui.chat.mapper.ChatUiMapper
import com.martorell.albert.basketcoach.ui.chat.model.ChatDrillOptionUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatLeftPaneMode
import com.martorell.albert.basketcoach.ui.chat.model.ChatTopicCardUiModel
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
 * ViewModel for the single-thread tactical Chat screen.
 *
 * Adheres to the Passive Initialization Mandate (performs zero work in init;
 * entry point is explicitly [loadChat]).
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
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var cachedGames: List<Game> = emptyList()
    private var initialPromptProcessed: Boolean = false

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
                            val activeTopic = currentSuccess?.activeTopicId

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

    fun onInputTextChanged(newText: String) {
        val currentSuccess = _uiState.value.content as? ChatUiContent.Success ?: return
        _uiState.update {
            it.copy(content = currentSuccess.copy(inputText = newText))
        }
    }

    fun onTopicSelected(topic: ChatTopicCardUiModel) {
        val currentSuccess = _uiState.value.content as? ChatUiContent.Success ?: return
        val targetMode = if (topic.hasDiagnosticEvidence) {
            ChatLeftPaneMode.DIAGNOSTIC_EVIDENCE
        } else {
            currentSuccess.leftPaneMode
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

    fun onLeftPaneModeChanged(mode: ChatLeftPaneMode) {
        val currentSuccess = _uiState.value.content as? ChatUiContent.Success ?: return
        _uiState.update {
            it.copy(content = currentSuccess.copy(leftPaneMode = mode))
        }
    }

    fun onDrillOptionSelected(drill: ChatDrillOptionUiModel) {
        val prompt = "${drill.optionTag}: ${drill.title}"
        sendMessage(prompt)
    }

    fun onRetry() {
        loadChat()
    }
}
