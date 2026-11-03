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
import com.martorell.albert.basketcoach.ui.chat.model.ChatTopicId
import com.martorell.albert.basketcoach.ui.chat.model.ChatUiContent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Transforms domain models ([Game], [ChatMessage]) into presentation models for the Chat workbench.
 *
 * Authoritative mathematical and presentation mapper computing:
 * - Phase 1 competitive balance (wins, losses) for the meta header.
 * - Localized sender name and role string resource IDs ([R.string.chat_sender_coach_name],
 *   [R.string.chat_sender_coach_role], [R.string.chat_sender_ai_name], [R.string.chat_sender_ai_role]).
 * - Default tactical intervention topic cards and diagnostic evidence models.
 * - Formatted message timestamps (`HH:mm`).
 */
class ChatUiMapper @Inject constructor() {

    /**
     * Maps match fixtures and chat messages into a [ChatUiContent.Success] state.
     *
     * Returns [ChatUiContent.Error] with [DomainError.Local.NotFound] if the games collection is empty.
     *
     * @param games List of Phase 1 match fixtures.
     * @param messages Chronological list of recorded conversation messages.
     * @param leftPaneMode Active display mode for the left workbench pane.
     * @param activeTopicId Active intervention topic card ID.
     * @param inputText Current draft text in the message input field.
     * @return [ChatUiContent] representing success or error.
     */
    fun mapToContent(
        games: List<Game>,
        messages: List<ChatMessage>,
        leftPaneMode: ChatLeftPaneMode,
        activeTopicId: ChatTopicId,
        inputText: String
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
        val topics = buildDefaultTopicList()

        return ChatUiContent.Success(
            metaHeader = metaHeader,
            leftPaneMode = leftPaneMode,
            topicList = topics,
            activeTopicId = activeTopicId,
            diagnosticEvidence = buildDefaultDiagnosticEvidence(),
            messages = mappedMessages,
            inputText = inputText
        )
    }

    /**
     * Maps an individual domain [ChatMessage] entity into a presentation [ChatMessageUiModel].
     *
     * @param domainMessage The domain chat message entity.
     * @return Presentation message model with localized sender metadata.
     */
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

    /**
     * Generates the default list of 4 tactical intervention topic cards.
     *
     * @return List of [ChatTopicCardUiModel] instances.
     */
    fun buildDefaultTopicList(): List<ChatTopicCardUiModel> = listOf(
        ChatTopicCardUiModel(
            id = ChatTopicId.REBOUND_CONTROL,
            priorityBadgeRes = R.string.chat_topic_1_badge,
            isPriority = true,
            isOpenInquiry = false,
            titleRes = R.string.chat_topic_1_title,
            descriptionRes = R.string.chat_topic_1_desc,
            statusPillRes = R.string.chat_topic_1_status,
            hasMetricPill = true,
            metricPillRes = R.string.chat_topic_1_metric,
            metricPillIsNegative = true,
            hasDiagnosticEvidence = true
        ),
        ChatTopicCardUiModel(
            id = ChatTopicId.AST_TO_RATIO,
            priorityBadgeRes = R.string.chat_topic_2_badge,
            isPriority = false,
            isOpenInquiry = false,
            titleRes = R.string.chat_topic_2_title,
            descriptionRes = R.string.chat_topic_2_desc,
            statusPillRes = R.string.chat_topic_2_status,
            hasMetricPill = true,
            metricPillRes = R.string.chat_topic_2_metric,
            metricPillIsNegative = false,
            hasDiagnosticEvidence = false
        ),
        ChatTopicCardUiModel(
            id = ChatTopicId.Q3_TURNOVERS,
            priorityBadgeRes = R.string.chat_topic_3_badge,
            isPriority = false,
            isOpenInquiry = false,
            titleRes = R.string.chat_topic_3_title,
            descriptionRes = R.string.chat_topic_3_desc,
            statusPillRes = R.string.chat_topic_3_status,
            hasMetricPill = true,
            metricPillRes = R.string.chat_topic_3_metric,
            metricPillIsNegative = true,
            hasDiagnosticEvidence = false
        ),
        ChatTopicCardUiModel(
            id = ChatTopicId.OPEN_INQUIRY,
            priorityBadgeRes = R.string.chat_topic_open_badge,
            isPriority = false,
            isOpenInquiry = true,
            titleRes = R.string.chat_topic_open_title,
            descriptionRes = R.string.chat_topic_open_desc,
            statusPillRes = R.string.chat_topic_open_status,
            hasMetricPill = false,
            metricPillRes = R.string.chat_topic_open_status,
            metricPillIsNegative = false,
            hasDiagnosticEvidence = false
        )
    )

    /**
     * Generates the default empirical diagnostic evidence model for defensive rebounding.
     *
     * @return [ChatDiagnosticEvidenceUiModel] instance with authentic Phase 1 metrics.
     */
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
