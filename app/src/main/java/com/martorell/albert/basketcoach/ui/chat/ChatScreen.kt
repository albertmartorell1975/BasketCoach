package com.martorell.albert.basketcoach.ui.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.ui.chat.data.ChatPreviewData
import com.martorell.albert.basketcoach.ui.chat.model.ChatLeftPaneMode
import com.martorell.albert.basketcoach.ui.chat.model.ChatMetaHeaderUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatUiState

/**
 * Stateful entry composable for the Tablet Landscape Single-Thread Chat workbench.
 *
 * Manages local interaction state (left pane mode, active topic, draft input text, and dynamic
 * appended messages from drill option selections) while hoisting rendering to stateless [ChatContent].
 *
 * @param initialPrompt Pre-grounded prompt passed when navigating from the Analysis screen, or empty string.
 * @param modifier Composable modifier for outer layout customization.
 */
@Composable
fun ChatScreen(
    initialPrompt: String,
    modifier: Modifier = Modifier
) {
    val isExploringFromAnalysis = initialPrompt.isNotBlank()

    var leftPaneMode by remember {
        mutableStateOf(if (isExploringFromAnalysis) ChatLeftPaneMode.DIAGNOSTIC_EVIDENCE else ChatLeftPaneMode.TOPIC_LIST)
    }
    var activeTopicId by remember { mutableStateOf(ChatPreviewData.DEFAULT_TOPIC_ID) }
    var inputText by remember { mutableStateOf(initialPrompt) }
    var messages by remember { mutableStateOf(ChatPreviewData.initialMessages) }
    var isSending by remember { mutableStateOf(false) }

    val metaHeader = ChatMetaHeaderUiModel(
        spaceTag = stringResource(R.string.chat_meta_space_tag),
        phaseSubtitle = stringResource(R.string.chat_meta_phase_subtitle, "02", 14),
        balancePill = stringResource(R.string.chat_meta_balance_pill, 3, 11),
        teamPill = stringResource(R.string.chat_meta_team_pill)
    )

    val currentState = ChatUiState(
        metaHeader = metaHeader,
        leftPaneMode = leftPaneMode,
        topicList = ChatPreviewData.defaultTopicList,
        activeTopicId = activeTopicId,
        diagnosticEvidence = ChatPreviewData.defaultDiagnosticEvidence,
        messages = messages,
        inputText = inputText,
        isSending = isSending
    )

    ChatContent(
        state = currentState,
        onTopicClick = { topic ->
            activeTopicId = topic.id
            if (topic.id == ChatPreviewData.DEFAULT_TOPIC_ID) {
                leftPaneMode = ChatLeftPaneMode.DIAGNOSTIC_EVIDENCE
            }
        },
        onBackToTopics = {
            leftPaneMode = ChatLeftPaneMode.TOPIC_LIST
        },
        onInputChange = { text ->
            inputText = text
        },
        onSend = {
            if (inputText.isNotBlank()) {
                messages = messages + ChatPreviewData.createCoachMessage(inputText)
                inputText = ""
            }
        },
        onDrillOptionSelect = { drill ->
            messages = messages +
                ChatPreviewData.createDrillConfirmationMessage(drill) +
                ChatPreviewData.createSummaryAiMessage()
        },
        modifier = modifier
    )
}
