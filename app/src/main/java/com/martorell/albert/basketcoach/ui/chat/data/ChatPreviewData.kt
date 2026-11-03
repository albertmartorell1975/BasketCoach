package com.martorell.albert.basketcoach.ui.chat.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.domain.model.MessageSender
import com.martorell.albert.basketcoach.ui.chat.mapper.ChatUiMapper
import com.martorell.albert.basketcoach.ui.chat.model.ChatDiagnosticEvidenceUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatDrillOptionUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatLeftPaneMode
import com.martorell.albert.basketcoach.ui.chat.model.ChatMessageUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatMetaHeaderUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatSummaryCardUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatTopicCardUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatUiContent
import com.martorell.albert.basketcoach.ui.chat.model.ChatUiState

/**
 * Static preview data factory for Jetpack Compose `@Preview` annotations in the Chat UI.
 *
 * Strictly scoped to design-time previews and never imported by production ViewModels or Mappers.
 * All preview texts are backed by string resources for full bilingual parity.
 */
object ChatPreviewData {

    private val previewMapper = ChatUiMapper()

    val defaultMetaHeader = ChatMetaHeaderUiModel(
        spaceTagRes = R.string.chat_meta_space_tag,
        phaseSubtitleRes = R.string.chat_meta_phase_subtitle,
        wins = 3,
        losses = 11,
        teamPillRes = R.string.chat_meta_team_pill
    )

    val defaultTopicList: List<ChatTopicCardUiModel> = previewMapper.buildDefaultTopicList()

    val defaultDiagnosticEvidence: ChatDiagnosticEvidenceUiModel = previewMapper.buildDefaultDiagnosticEvidence()

    val drillOptionA: ChatDrillOptionUiModel
        @Composable
        get() = ChatDrillOptionUiModel(
            id = "drill_option_a",
            optionTag = stringResource(R.string.chat_preview_drill_a_tag),
            intensityTag = stringResource(R.string.chat_preview_drill_a_intensity),
            isHighIntensity = true,
            title = stringResource(R.string.chat_preview_drill_a_title),
            description = stringResource(R.string.chat_preview_drill_a_desc),
            actionButtonText = stringResource(R.string.chat_preview_drill_a_action)
        )

    val drillOptionB: ChatDrillOptionUiModel
        @Composable
        get() = ChatDrillOptionUiModel(
            id = "drill_option_b",
            optionTag = stringResource(R.string.chat_preview_drill_b_tag),
            intensityTag = stringResource(R.string.chat_preview_drill_b_intensity),
            isHighIntensity = false,
            title = stringResource(R.string.chat_preview_drill_b_title),
            description = stringResource(R.string.chat_preview_drill_b_desc),
            actionButtonText = stringResource(R.string.chat_preview_drill_b_action)
        )

    val summaryCard: ChatSummaryCardUiModel
        @Composable
        get() = ChatSummaryCardUiModel(
            title = stringResource(R.string.chat_preview_summary_title),
            badgeText = stringResource(R.string.chat_preview_summary_badge),
            description = stringResource(R.string.chat_preview_summary_desc),
            objectiveText = stringResource(R.string.chat_preview_summary_objective),
            microcycleText = stringResource(R.string.chat_preview_summary_microcycle)
        )

    val initialMessages: List<ChatMessageUiModel>
        @Composable
        get() = listOf(
            ChatMessageUiModel(
                id = "msg_1",
                sender = MessageSender.AI,
                senderNameRes = R.string.chat_sender_ai_name,
                senderRoleRes = R.string.chat_sender_ai_role,
                timestamp = "10:14",
                text = stringResource(R.string.chat_preview_msg_1_text),
                hasCalloutQuestion = true,
                calloutQuestionText = stringResource(R.string.chat_preview_msg_1_callout_q),
                calloutSubText = stringResource(R.string.chat_preview_msg_1_callout_sub),
                hasDrillOptions = false,
                drillOptions = emptyList(),
                hasSummaryCard = false,
                summaryCard = ChatSummaryCardUiModel.EMPTY
            ),
            ChatMessageUiModel(
                id = "msg_2",
                sender = MessageSender.COACH,
                senderNameRes = R.string.chat_sender_coach_name,
                senderRoleRes = R.string.chat_sender_coach_role,
                timestamp = "10:17",
                text = stringResource(R.string.chat_preview_msg_2_text),
                hasCalloutQuestion = false,
                calloutQuestionText = "",
                calloutSubText = "",
                hasDrillOptions = false,
                drillOptions = emptyList(),
                hasSummaryCard = false,
                summaryCard = ChatSummaryCardUiModel.EMPTY
            ),
            ChatMessageUiModel(
                id = "msg_3",
                sender = MessageSender.AI,
                senderNameRes = R.string.chat_sender_ai_name,
                senderRoleRes = R.string.chat_sender_ai_role,
                timestamp = "10:19",
                text = stringResource(R.string.chat_preview_msg_3_text),
                hasCalloutQuestion = false,
                calloutQuestionText = "",
                calloutSubText = "",
                hasDrillOptions = true,
                drillOptions = listOf(drillOptionA, drillOptionB),
                hasSummaryCard = false,
                summaryCard = ChatSummaryCardUiModel.EMPTY
            )
        )

    val previewUiContent: ChatUiContent.Success
        @Composable
        get() = ChatUiContent.Success(
            metaHeader = defaultMetaHeader,
            leftPaneMode = ChatLeftPaneMode.TOPIC_LIST,
            topicList = defaultTopicList,
            activeTopicId = defaultTopicList.first().id,
            diagnosticEvidence = defaultDiagnosticEvidence,
            messages = initialMessages,
            inputText = ""
        )

    val previewUiState: ChatUiState
        @Composable
        get() = ChatUiState(
            content = previewUiContent,
            isSending = false
        )
}
