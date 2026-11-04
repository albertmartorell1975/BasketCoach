package com.martorell.albert.basketcoach.ui.chat.mapper

import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.domain.model.ChatMessage
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.InsightType
import com.martorell.albert.basketcoach.domain.model.MessageSender
import com.martorell.albert.basketcoach.domain.model.MetricSentiment
import com.martorell.albert.basketcoach.domain.repository.OPEN_INQUIRY_TOPIC_ID
import com.martorell.albert.basketcoach.ui.chat.model.ChatMessageUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatMetaHeaderUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatSummaryCardUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatTopicCardUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatUiContent
import com.martorell.albert.basketcoach.ui.util.UiText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Transforms domain models ([Game], [ChatMessage], [Insight]) into presentation models for the Chat workbench.
 *
 * Responsibilities include:
 * - Aggregating Phase 1 game statistics into [ChatMetaHeaderUiModel].
 * - Dynamically mapping [InsightType.IMPROVEMENT] insights into [ChatTopicCardUiModel] entries with deterministic priority.
 * - Appending the unconstrained [OPEN_INQUIRY_TOPIC_ID] card ("Consulta Lliure") to the topic list.
 * - Mapping domain [ChatMessage] items into presentation [ChatMessageUiModel] representations with localized role labels.
 */
class ChatUiMapper @Inject constructor() {

    /**
     * Transforms domain data into the active [ChatUiContent] presentation state.
     *
     * @param games Complete list of Phase 1 [Game] fixtures used to calculate record and context metadata.
     * @param insights Full list of AI-generated [Insight] entities to extract improvement areas from.
     * @param messages Chronological message stream for the active topic thread.
     * @param activeTopicId Unique identifier of the currently selected topic or improvement area.
     * @param inputText Current text draft in the message input field.
     * @return [ChatUiContent.Success] with mapped presentation models, or [ChatUiContent.Error] if games list is empty.
     */
    fun mapToContent(
        games: List<Game>,
        insights: List<Insight>,
        messages: List<ChatMessage>,
        activeTopicId: String,
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

        val improvementCards = insights
            .filter { it.type == InsightType.IMPROVEMENT }
            .mapIndexed { index, insight ->
                ChatTopicCardUiModel(
                    id = insight.id,
                    badgeText = if (insight.priority.isNotBlank()) {
                        UiText.DynamicString(insight.priority)
                    } else {
                        UiText.StringResource(R.string.chat_topic_badge_improvement)
                    },
                    isPriority = index == 0,
                    isOpenInquiry = false,
                    title = UiText.DynamicString(insight.title),
                    description = UiText.DynamicString(insight.description),
                    statusPill = if (insight.id == activeTopicId) {
                        UiText.StringResource(R.string.chat_topic_status_active)
                    } else {
                        UiText.StringResource(R.string.chat_topic_status_pending)
                    },
                    hasMetricPill = insight.metric.primaryValue.isNotBlank(),
                    metricPill = UiText.DynamicString(insight.metric.primaryValue),
                    metricPillIsNegative = insight.metric.sentiment == MetricSentiment.NEGATIVE,
                    exploreChatPrompt = insight.exploreChatPrompt
                )
            }

        val openInquiryCard = ChatTopicCardUiModel(
            id = OPEN_INQUIRY_TOPIC_ID,
            badgeText = UiText.StringResource(R.string.chat_topic_open_badge),
            isPriority = false,
            isOpenInquiry = true,
            title = UiText.StringResource(R.string.chat_topic_open_title),
            description = UiText.StringResource(R.string.chat_topic_open_desc),
            statusPill = UiText.StringResource(R.string.chat_topic_open_status),
            hasMetricPill = false,
            metricPill = UiText.DynamicString(""),
            metricPillIsNegative = false,
            exploreChatPrompt = ""
        )

        val allTopics = improvementCards + openInquiryCard
        val mappedMessages = messages.map { mapMessage(it) }

        return ChatUiContent.Success(
            metaHeader = metaHeader,
            topicList = allTopics,
            activeTopicId = activeTopicId,
            messages = mappedMessages,
            inputText = inputText
        )
    }

    /**
     * Maps an individual domain [ChatMessage] into its presentation [ChatMessageUiModel] representation.
     *
     * @param domainMessage The domain [ChatMessage] to transform.
     * @return Transformed [ChatMessageUiModel] with localized sender name and role resources.
     */
    fun mapMessage(domainMessage: ChatMessage): ChatMessageUiModel {
        val (nameRes, roleRes) = when (domainMessage.sender) {
            MessageSender.COACH -> Pair(R.string.chat_sender_coach_name, R.string.chat_sender_coach_role)
            MessageSender.AI -> Pair(R.string.chat_sender_ai_name, R.string.chat_sender_ai_role)
            MessageSender.SYSTEM -> Pair(R.string.chat_sender_system_name, R.string.chat_sender_system_role)
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
        if (timestamp <= 0L) return ""
        return try {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            sdf.format(Date(timestamp))
        } catch (_: Exception) {
            ""
        }
    }
}
