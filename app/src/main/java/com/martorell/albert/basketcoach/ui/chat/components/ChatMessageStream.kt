package com.martorell.albert.basketcoach.ui.chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.martorell.albert.basketcoach.ui.chat.data.ChatPreviewData
import com.martorell.albert.basketcoach.ui.chat.model.ChatDrillOptionUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatMessageUiModel
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview

/**
 * Vertically scrollable message stream list displaying conversation bubbles.
 *
 * Automatically scrolls to the newest message whenever the message count increases.
 *
 * @param messages Ordered list of messages to display.
 * @param onDrillOptionSelect Callback fired when a formative drill choice is selected.
 * @param modifier Composable modifier for layout customization.
 * @param listState Optional hoisted [LazyListState] for scroll control.
 */
@Composable
fun ChatMessageStream(
    messages: List<ChatMessageUiModel>,
    onDrillOptionSelect: (ChatDrillOptionUiModel) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()
) {
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.size - 1)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = messages,
            key = { it.id }
        ) { message ->
            ChatMessageBubble(
                model = message,
                onDrillOptionSelect = onDrillOptionSelect
            )
        }
    }
}

@BcThemePreview
@Composable
private fun ChatMessageStreamPreview() {
    ChatMessageStream(
        messages = ChatPreviewData.initialMessages,
        onDrillOptionSelect = {}
    )
}
