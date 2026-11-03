package com.martorell.albert.basketcoach.ui.chat.mapper

import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.MessageSender
import com.martorell.albert.basketcoach.ui.chat.data.ChatPreviewData
import com.martorell.albert.basketcoach.ui.chat.model.ChatLeftPaneMode
import com.martorell.albert.basketcoach.ui.chat.model.ChatMessageUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatMetaHeaderUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatSummaryCardUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatUiContent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Transforms domain models ([Game], [ChatMessage]) into presentation models for the Chat workbench.
 */
class ChatUiMapper @Inject constructor() {

    fun mapToContent(
        games: List<Game>,
        messages: List<ChatMessage>,
        leftPaneMode: ChatLeftPaneMode = ChatLeftPaneMode.TOPIC_LIST,
        activeTopicId: String = ChatPreviewData.DEFAULT_TOPIC_ID,
        inputText: String = ""
    ): ChatUiContent {
        if (games.isEmpty()) {
            return ChatUiContent.Error(DomainError.Local.NotFound)
        }

        val wins = games.count { it.isWin }
        val losses = games.size - wins

        val metaHeader = ChatMetaHeaderUiModel(
            spaceTagRes = R.string.chat_meta_space_tag,
            phaseSubtitleRes = R.string.chat_meta_phase_subtitle,
            wins = wins,
            losses = losses,
            teamPillRes = R.string.chat_meta_team_pill
        )

        val mappedMessages = messages.map { mapMessage(it) }

        return ChatUiContent.Success(
            metaHeader = metaHeader,
            leftPaneMode = leftPaneMode,
            topicList = ChatPreviewData.defaultTopicList,
            activeTopicId = activeTopicId,
            diagnosticEvidence = ChatPreviewData.defaultDiagnosticEvidence,
            messages = mappedMessages,
            inputText = inputText
        )
    }

    fun mapMessage(domainMessage: ChatMessage): ChatMessageUiModel {
        val (nameRes, roleRes) = when (domainMessage.sender) {
            MessageSender.COACH -> Pair(R.string.chat_sender_coach_name, R.string.chat_sender_coach_role)
            MessageSender.AI -> Pair(R.string.chat_sender_ai_name, R.string.chat_sender_ai_role)
        }

        val timeString = formatTimestamp(domainMessage.timestamp)

        return ChatMessageUiModel(
            id = domainMessage.id,
            sender = domainMessage.sender,
            senderNameRes = nameRes,
            senderRoleRes = roleRes,
            timestamp = timeString,
            text = domainMessage.text,
            hasCalloutQuestion = false,
            calloutQuestionText = "",
            calloutSubText = "",
            hasDrillOptions = false,
            drillOptions = emptyList(),
            hasSummaryCard = false,
            summaryCard = ChatSummaryCardUiModel.EMPTY
        )
    }

    private fun formatTimestamp(timestamp: Long): String {
        return try {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            sdf.format(Date(timestamp))
        } catch (_: Exception) {
            "10:42"
        }
    }
}
