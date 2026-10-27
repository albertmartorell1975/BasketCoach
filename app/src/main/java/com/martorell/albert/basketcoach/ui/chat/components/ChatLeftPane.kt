package com.martorell.albert.basketcoach.ui.chat.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.martorell.albert.basketcoach.ui.chat.data.ChatPreviewData
import com.martorell.albert.basketcoach.ui.chat.model.ChatDiagnosticEvidenceUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatLeftPaneMode
import com.martorell.albert.basketcoach.ui.chat.model.ChatTopicCardUiModel
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview

@Composable
fun ChatLeftPane(
    mode: ChatLeftPaneMode,
    topics: List<ChatTopicCardUiModel>,
    activeTopicId: String,
    diagnosticEvidence: ChatDiagnosticEvidenceUiModel,
    onTopicClick: (ChatTopicCardUiModel) -> Unit,
    onBackToTopics: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = mode,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "left_pane_mode_transition",
        modifier = modifier.fillMaxWidth()
    ) { currentMode ->
        when (currentMode) {
            ChatLeftPaneMode.TOPIC_LIST -> {
                ChatTopicList(
                    topics = topics,
                    activeTopicId = activeTopicId,
                    onTopicClick = onTopicClick
                )
            }
            ChatLeftPaneMode.DIAGNOSTIC_EVIDENCE -> {
                ChatDiagnosticEvidenceView(
                    model = diagnosticEvidence,
                    onBackToTopics = onBackToTopics
                )
            }
        }
    }
}

@BcThemePreview
@Composable
private fun ChatLeftPanePreview() {
    ChatLeftPane(
        mode = ChatLeftPaneMode.TOPIC_LIST,
        topics = ChatPreviewData.defaultTopicList,
        activeTopicId = "rebound_control",
        diagnosticEvidence = ChatPreviewData.defaultDiagnosticEvidence,
        onTopicClick = {},
        onBackToTopics = {}
    )
}
