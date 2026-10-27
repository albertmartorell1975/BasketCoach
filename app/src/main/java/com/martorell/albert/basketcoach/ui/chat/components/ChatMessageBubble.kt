package com.martorell.albert.basketcoach.ui.chat.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.martorell.albert.basketcoach.domain.model.MessageSender
import com.martorell.albert.basketcoach.ui.chat.model.ChatDrillOptionUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatMessageUiModel

/**
 * Dispatches message bubble rendering based on the sender: [CoachMessageBubble] for Coach Jordi
 * or [AiMessageBubble] for BasketCoach AI.
 *
 * @param model Presentation model for the message ([ChatMessageUiModel]).
 * @param onDrillOptionSelect Callback fired when a drill option is selected.
 * @param modifier Composable modifier for layout customization.
 */
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
