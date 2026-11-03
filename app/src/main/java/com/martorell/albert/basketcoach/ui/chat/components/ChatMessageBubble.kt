package com.martorell.albert.basketcoach.ui.chat.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.martorell.albert.basketcoach.domain.model.MessageSender
import com.martorell.albert.basketcoach.ui.chat.data.ChatPreviewData
import com.martorell.albert.basketcoach.ui.chat.model.ChatDrillOptionUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatMessageUiModel
import com.martorell.albert.basketcoach.ui.theme.BcDevicePreview
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview

/**
 * Dispatches message bubble rendering based on the sender: [CoachMessageBubble] for Coach Jordi,
 * [AiMessageBubble] for BasketCoach AI, or [SystemMessageBubble] for system directives.
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
        MessageSender.SYSTEM -> SystemMessageBubble(model = model, modifier = modifier)
    }
}

@BcThemePreview
@BcDevicePreview
@Composable
private fun ChatMessageBubbleCoachPreview() {
    ChatMessageBubble(
        model = ChatPreviewData.initialMessages[1],
        onDrillOptionSelect = {}
    )
}

@BcThemePreview
@BcDevicePreview
@Composable
private fun ChatMessageBubbleAiPreview() {
    ChatMessageBubble(
        model = ChatPreviewData.initialMessages[2],
        onDrillOptionSelect = {}
    )
}

@BcThemePreview
@BcDevicePreview
@Composable
private fun ChatMessageBubbleSystemPreview() {
    ChatMessageBubble(
        model = ChatPreviewData.systemMessage,
        onDrillOptionSelect = {}
    )
}
