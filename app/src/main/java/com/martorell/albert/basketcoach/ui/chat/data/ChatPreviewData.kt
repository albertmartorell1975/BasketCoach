package com.martorell.albert.basketcoach.ui.chat.data

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

    val drillOptionA = ChatDrillOptionUiModel(
        id = "drill_option_a",
        optionTag = "Opció A",
        intensityTag = "Intensitat Alta",
        isHighIntensity = true,
        title = "3c3 Crash vs Box-Out",
        description = "Tir forçat des de l'oposat. Obligació de bloqueig de 3 segons abans de l'alliberament del rebot. Puntuació doble pel rebot net defensiu.",
        actionButtonText = "Seleccionar Opció A"
    )

    val drillOptionB = ChatDrillOptionUiModel(
        id = "drill_option_b",
        optionTag = "Opció B",
        intensityTag = "Continuïtat",
        isHighIntensity = false,
        title = "Regla Primer Contacte 4c4",
        description = "Joc continu 4c4. Si qualsevol defensor exterior gira el cap sense fixar contacte pectoral previ, es xiula falta tècnica interna d'entrenament.",
        actionButtonText = "Seleccionar Opció B"
    )

    val summaryCard = ChatSummaryCardUiModel(
        title = "Capacitat de Tancar el Rebot Defensiu (Línia Exterior)",
        badgeText = "3c3 Crash",
        description = "Implantació del bloqueig de 3 segons abans de l'alliberament del rebot en tirs des de l'oposat.",
        objectiveText = "< 9.5 pts 2a oportunitat",
        microcycleText = "4 setmanes (Dimecres)"
    )

    val initialMessages = listOf(
        ChatMessageUiModel(
            id = "msg_1",
            sender = MessageSender.AI,
            senderNameRes = R.string.chat_sender_ai_name,
            senderRoleRes = R.string.chat_sender_ai_role,
            timestamp = "10:14",
            text = "Hola Jordi. El dataset dels últims 14 partits confirma que concedim 14.8 punts de segona oportunitat per duel. Això representa el 21.4% de la nostra anotació rival total.",
            hasCalloutQuestion = true,
            calloutQuestionText = "Com valores si aquesta sagnia ve d'un bloqueig de rebot deficient dels exteriors o d'interiors que surten massa a ajudar?",
            calloutSubText = "Comprova la tendència: no és un problema d'alçada, sinó d'orientació en el llançament oposat.",
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
            text = "Crec que sobretot els exteriors no tanquen la línia de tir lliure quan salta el tir exterior. Es giren mirant la pilota sense contactar primer amb el seu atacant, i ens guanyen l'esquena amb facilitat.",
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
            text = "La teva lectura és exacta Jordi: el 61% dels rebots ofensius cedits provenen d'entrades des de les ales o capçalera després de tir exterior.\n\nPer revertir-ho en la Fase 2, quina dinàmica formativa s'adapta millor a l'horari de dimecres?",
            hasCalloutQuestion = false,
            calloutQuestionText = "",
            calloutSubText = "",
            hasDrillOptions = true,
            drillOptions = listOf(drillOptionA, drillOptionB),
            hasSummaryCard = false,
            summaryCard = ChatSummaryCardUiModel.EMPTY
        )
    )

    val previewUiContent = ChatUiContent.Success(
        metaHeader = defaultMetaHeader,
        leftPaneMode = ChatLeftPaneMode.TOPIC_LIST,
        topicList = defaultTopicList,
        activeTopicId = ChatUiMapper.DEFAULT_TOPIC_ID,
        diagnosticEvidence = defaultDiagnosticEvidence,
        messages = initialMessages,
        inputText = ""
    )

    val previewUiState = ChatUiState(
        content = previewUiContent,
        isSending = false
    )
}
