package com.martorell.albert.basketcoach.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.martorell.albert.basketcoach.ui.chat.components.ChatErrorView
import com.martorell.albert.basketcoach.ui.chat.components.ChatLoadingView
import com.martorell.albert.basketcoach.ui.chat.mapper.toChatUiText
import com.martorell.albert.basketcoach.ui.chat.model.ChatDrillOptionUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatTopicCardUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatUiContent
import com.martorell.albert.basketcoach.ui.chat.model.ChatUiState
import com.martorell.albert.basketcoach.ui.theme.PiaNavy

@Composable
fun ChatScreen(
    initialPrompt: String,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(initialPrompt) {
        viewModel.loadChat(initialPrompt)
    }

    ChatScreen(
        uiState = uiState,
        onTopicClick = viewModel::onTopicSelected,
        onInputChange = viewModel::onInputTextChanged,
        onSend = { viewModel.sendMessage() },
        onDrillOptionSelect = viewModel::onDrillOptionSelected,
        onRetry = viewModel::onRetry,
        modifier = modifier
    )
}

@Composable
fun ChatScreen(
    uiState: ChatUiState,
    onTopicClick: (ChatTopicCardUiModel) -> Unit,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onDrillOptionSelect: (ChatDrillOptionUiModel) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val content = uiState.content) {
            is ChatUiContent.Loading -> {
                ChatLoadingView()
            }
            is ChatUiContent.Error -> {
                ChatErrorView(
                    message = content.error.toChatUiText(),
                    onRetry = onRetry
                )
            }
            is ChatUiContent.Success -> {
                ChatContent(
                    state = content,
                    isSending = uiState.isSending,
                    onTopicClick = onTopicClick,
                    onInputChange = onInputChange,
                    onSend = onSend,
                    onDrillOptionSelect = onDrillOptionSelect,
                    modifier = Modifier.fillMaxSize()
                )
                if (uiState.isSending) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PiaNavy)
                    }
                }
            }
        }
    }
}
