package com.martorell.albert.basketcoach.ui.chat.mapper

import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.MessageSender
import com.martorell.albert.basketcoach.ui.chat.model.ChatDiagnosticEvidenceUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatLeftPaneMode
import com.martorell.albert.basketcoach.ui.chat.model.ChatMessageUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatMetaHeaderUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatSummaryCardUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatTopicCardUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatUiContent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Transforms domain models ([Game], [ChatMessage]) into presentation models for the Chat workbench.
 */
class ChatUiMapper @Inject constructor() {

    companion object {
        const val DEFAULT_TOPIC_ID = "rebound_control"
    }

    fun mapToContent(
        games: List<Game>,
        messages: List<ChatMessage>,
        leftPaneMode: ChatLeftPaneMode = ChatLeftPaneMode.TOPIC_LIST,
        activeTopicId: String = DEFAULT_TOPIC_ID,
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
            topicList = buildDefaultTopicList(),
            activeTopicId = activeTopicId,
            diagnosticEvidence = buildDefaultDiagnosticEvidence(),
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

    fun buildDefaultTopicList(): List<ChatTopicCardUiModel> = listOf(
        ChatTopicCardUiModel(
            id = DEFAULT_TOPIC_ID,
            priorityBadgeRes = R.string.chat_topic_1_badge,
            isPriority = true,
            isOpenInquiry = false,
            titleRes = R.string.chat_topic_1_title,
            descriptionRes = R.string.chat_topic_1_desc,
            statusPillRes = R.string.chat_topic_1_status,
            hasMetricPill = true,
            metricPillRes = R.string.chat_topic_1_metric,
            metricPillIsNegative = true
        ),
        ChatTopicCardUiModel(
            id = "ast_to_ratio",
            priorityBadgeRes = R.string.chat_topic_2_badge,
            isPriority = false,
            isOpenInquiry = false,
            titleRes = R.string.chat_topic_2_title,
            descriptionRes = R.string.chat_topic_2_desc,
            statusPillRes = R.string.chat_topic_2_status,
            hasMetricPill = true,
            metricPillRes = R.string.chat_topic_2_metric,
            metricPillIsNegative = false
        ),
        ChatTopicCardUiModel(
            id = "q3_turnovers",
            priorityBadgeRes = R.string.chat_topic_3_badge,
            isPriority = false,
            isOpenInquiry = false,
            titleRes = R.string.chat_topic_3_title,
            descriptionRes = R.string.chat_topic_3_desc,
            statusPillRes = R.string.chat_topic_3_status,
            hasMetricPill = true,
            metricPillRes = R.string.chat_topic_3_metric,
            metricPillIsNegative = true
        ),
        ChatTopicCardUiModel(
            id = "open_inquiry",
            priorityBadgeRes = R.string.chat_topic_open_badge,
            isPriority = false,
            isOpenInquiry = true,
            titleRes = R.string.chat_topic_open_title,
            descriptionRes = R.string.chat_topic_open_desc,
            statusPillRes = R.string.chat_topic_open_status,
            hasMetricPill = false,
            metricPillRes = R.string.chat_topic_open_status,
            metricPillIsNegative = false
        )
    )

    fun buildDefaultDiagnosticEvidence(): ChatDiagnosticEvidenceUiModel = ChatDiagnosticEvidenceUiModel(
        priorityBadgeRes = R.string.chat_evidence_priority_badge,
        titleRes = R.string.chat_evidence_title,
        subtitleRes = R.string.chat_evidence_subtitle,
        metric1LabelRes = R.string.chat_evidence_metric1_label,
        metric1Value = "14.8",
        metric1UnitRes = R.string.chat_evidence_metric1_unit,
        metric1DeltaRes = R.string.chat_evidence_metric1_delta,
        metric2LabelRes = R.string.chat_evidence_metric2_label,
        metric2Value = "13.2",
        metric2UnitRes = R.string.chat_evidence_metric2_unit,
        metric2RankingRes = R.string.chat_evidence_metric2_ranking,
        zeroAssumptionTitleRes = R.string.chat_evidence_zero_assumption_title,
        zeroAssumptionDescriptionRes = R.string.chat_evidence_zero_assumption_desc,
        courtPerimeterPct = "61%",
        courtInteriorPct = "39%",
        courtLeftWingPct = "29%",
        courtTopOfKeyPct = "32%",
        courtRightWingPct = "23%",
        courtPaintPct = "16%",
        methodologyTitleRes = R.string.chat_methodology_title,
        methodologyCycleRes = R.string.chat_methodology_cycle
    )

    private fun formatTimestamp(timestamp: Long): String {
        return try {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            sdf.format(Date(timestamp))
        } catch (_: Exception) {
            "10:42"
        }
    }
}
