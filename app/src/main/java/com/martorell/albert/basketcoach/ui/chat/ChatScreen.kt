package com.martorell.albert.basketcoach.ui.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.domain.model.MessageSender
import com.martorell.albert.basketcoach.ui.chat.data.ChatPreviewData
import com.martorell.albert.basketcoach.ui.chat.model.ChatLeftPaneMode
import com.martorell.albert.basketcoach.ui.chat.model.ChatMessageUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatMetaHeaderUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatSummaryCardUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatUiState

@Composable
fun ChatScreen(
    initialPrompt: String = "",
    modifier: Modifier = Modifier
) {
    val isExploringFromAnalysis = initialPrompt.isNotBlank()

    var leftPaneMode by remember {
        mutableStateOf(if (isExploringFromAnalysis) ChatLeftPaneMode.DIAGNOSTIC_EVIDENCE else ChatLeftPaneMode.TOPIC_LIST)
    }
    var activeTopicId by remember { mutableStateOf("rebound_control") }
    var inputText by remember { mutableStateOf(initialPrompt) }
    var messages by remember { mutableStateOf(ChatPreviewData.initialMessages) }
    var isSending by remember { mutableStateOf(false) }

    val metaHeader = ChatMetaHeaderUiModel(
        spaceTag = stringResource(R.string.chat_meta_space_tag),
        phaseSubtitle = stringResource(R.string.chat_meta_phase_subtitle),
        balancePill = stringResource(R.string.chat_meta_balance_pill),
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
            if (topic.id == "rebound_control") {
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
                val newCoachMessage = ChatMessageUiModel(
                    id = "msg_${System.currentTimeMillis()}",
                    sender = MessageSender.COACH,
                    senderName = "Coach Jordi",
                    senderRole = "Entrenador Principal",
                    timestamp = "10:25",
                    text = inputText.trim(),
                    hasCalloutQuestion = false,
                    calloutQuestionText = "",
                    calloutSubText = "",
                    hasDrillOptions = false,
                    drillOptions = emptyList(),
                    hasSummaryCard = false,
                    summaryCard = ChatSummaryCardUiModel.EMPTY
                )
                messages = messages + newCoachMessage
                inputText = ""
            }
        },
        onDrillOptionSelect = { drill ->
            val confirmationMsg = ChatMessageUiModel(
                id = "msg_${System.currentTimeMillis()}_coach",
                sender = MessageSender.COACH,
                senderName = "Coach Jordi",
                senderRole = "Entrenador Principal",
                timestamp = "10:22",
                text = "Comencem amb l'${drill.optionTag} (${drill.title}) els dimecres. Prioritzem crear l'hàbit de contacte abans d'anar a buscar la pilota.",
                hasCalloutQuestion = false,
                calloutQuestionText = "",
                calloutSubText = "",
                hasDrillOptions = false,
                drillOptions = emptyList(),
                hasSummaryCard = false,
                summaryCard = ChatSummaryCardUiModel.EMPTY
            )
            val summaryAiMsg = ChatMessageUiModel(
                id = "msg_${System.currentTimeMillis()}_ai",
                sender = MessageSender.AI,
                senderName = "BasketCoach",
                senderRole = "Focus Integrat",
                timestamp = "10:23",
                text = "Molt d'acord, Jordi. Queda sintetitzat i incorporat a la planificació formativa de la Fase 2:",
                hasCalloutQuestion = false,
                calloutQuestionText = "",
                calloutSubText = "",
                hasDrillOptions = false,
                drillOptions = emptyList(),
                hasSummaryCard = true,
                summaryCard = ChatPreviewData.summaryCard
            )
            messages = messages + confirmationMsg + summaryAiMsg
        },
        modifier = modifier
    )
}
