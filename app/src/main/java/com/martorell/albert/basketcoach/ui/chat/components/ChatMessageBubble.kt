package com.martorell.albert.basketcoach.ui.chat.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.martorell.albert.basketcoach.domain.model.MessageSender
import com.martorell.albert.basketcoach.ui.chat.model.ChatDrillOptionUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatMessageUiModel

@Composable
fun ChatMessageBubble(
    model: ChatMessageUiModel,
    onDrillOptionSelect: (ChatDrillOptionUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    when (model.sender) {
        MessageSender.COACH -> CoachMessageBubble(model = model, modifier = modifier)
        MessageSender.AI -> AiMessageBubble(model = model, onDrillOptionSelect = onDrillOptionSelect, modifier = modifier)
    }
}
