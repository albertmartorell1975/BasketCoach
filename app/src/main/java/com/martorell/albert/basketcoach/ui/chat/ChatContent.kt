package com.martorell.albert.basketcoach.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.martorell.albert.basketcoach.ui.chat.components.ChatLeftPane
import com.martorell.albert.basketcoach.ui.chat.components.ChatMetaHeader
import com.martorell.albert.basketcoach.ui.chat.components.ChatRightPane
import com.martorell.albert.basketcoach.ui.chat.data.ChatPreviewData
import com.martorell.albert.basketcoach.ui.chat.model.ChatDrillOptionUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatTopicCardUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatUiState
import com.martorell.albert.basketcoach.ui.theme.BcDevicePreview
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview
import com.martorell.albert.basketcoach.ui.theme.PiaBackground

/**
 * Stateless main workbench layout for the Tablet Landscape Single-Thread Chat screen.
 *
 * Assembles the context meta header, the 4-column left pane ([ChatLeftPane]), and the 8-column
 * right conversational pane ([ChatRightPane]) in a tablet-optimized landscape grid.
 *
 * @param state Current immutable UI state ([ChatUiState]).
 * @param onTopicClick Callback triggered when an intervention topic card is tapped.
 * @param onBackToTopics Callback triggered to return from diagnostic evidence to the topics list.
 * @param onInputChange Callback triggered when the input text changes.
 * @param onSend Callback triggered to dispatch a new coach message.
 * @param onDrillOptionSelect Callback triggered when a formative drill option is selected.
 * @param modifier Composable modifier for outer layout customization.
 */
@Composable
fun ChatContent(
    state: ChatUiState,
    onTopicClick: (ChatTopicCardUiModel) -> Unit,
    onBackToTopics: () -> Unit,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onDrillOptionSelect: (ChatDrillOptionUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PiaBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ChatMetaHeader(
            model = state.metaHeader,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ChatLeftPane(
                mode = state.leftPaneMode,
                topics = state.topicList,
                activeTopicId = state.activeTopicId,
                diagnosticEvidence = state.diagnosticEvidence,
                onTopicClick = onTopicClick,
                onBackToTopics = onBackToTopics,
                modifier = Modifier.weight(0.38f)
            )

            ChatRightPane(
                messages = state.messages,
                inputText = state.inputText,
                isSending = state.isSending,
                onInputChange = onInputChange,
                onSend = onSend,
                onDrillOptionSelect = onDrillOptionSelect,
                modifier = Modifier.weight(0.62f)
            )
        }
    }
}

@BcThemePreview
@BcDevicePreview
@Composable
private fun ChatContentPreview() {
    ChatContent(
        state = ChatPreviewData.previewUiState,
        onTopicClick = {},
        onBackToTopics = {},
        onInputChange = {},
        onSend = {},
        onDrillOptionSelect = {}
    )
}
