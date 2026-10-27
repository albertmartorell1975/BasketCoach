package com.martorell.albert.basketcoach.ui.chat.data

import com.martorell.albert.basketcoach.domain.model.MessageSender
import com.martorell.albert.basketcoach.ui.chat.model.ChatDiagnosticEvidenceUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatDrillOptionUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatLeftPaneMode
import com.martorell.albert.basketcoach.ui.chat.model.ChatMessageUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatMetaHeaderUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatSummaryCardUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatTopicCardUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatUiState

/**
 * Static preview and mock data factory for the Tablet Single-Thread Chat UI.
 *
 * Supplies authentic Phase 1 intervention topics, empirical diagnostic evidence matching
 * the authoritative `chat_detalle` prototype, and a multi-turn Socratic dialogue.
 */
object ChatPreviewData {

    const val DEFAULT_TOPIC_ID = "rebound_control"

    val defaultMetaHeader = ChatMetaHeaderUiModel(
        spaceTag = "Espai de Reflexió Tàctica · 1a Fase",
        phaseSubtitle = "Interterritorial Grup 02 · 14 Jornades analitzades",
        balancePill = "Balanç: 3V - 11D",
        teamPill = "Junior A Masculí"
    )

    val defaultTopicList = listOf(
        ChatTopicCardUiModel(
            id = "rebound_control",
            priorityBadge = "ÀREA #1 · PRIORITÀRIA",
            isPriority = true,
            isOpenInquiry = false,
            title = "Control del Rebot Defensiu",
            description = "14.8 pts de 2a oportunitat concedits · 13.2 reb. def",
            statusPill = "Fil obert · 1 acord preliminar",
            metricPill = "-8.4 net",
            metricPillIsNegative = true
        ),
        ChatTopicCardUiModel(
            id = "ast_to_ratio",
            priorityBadge = "ÀREA #2 · GESTIÓ",
            isPriority = false,
            isOpenInquiry = false,
            title = "Ràtio Assistències / Pèrdues",
            description = "Ràtio 0.58 AST/TO (18.1 pèrdues/partit)",
            statusPill = "Pendent d'iniciar",
            metricPill = "10.5 ast",
            metricPillIsNegative = false
        ),
        ChatTopicCardUiModel(
            id = "q3_turnovers",
            priorityBadge = "ÀREA #3 · RITME I CONCENTRACIÓ",
            isPriority = false,
            isOpenInquiry = false,
            title = "Gestió de Pèrdues en el 3r Quart",
            description = "Concentració del 42% de pèrdues crítiques",
            statusPill = "Pendent d'iniciar",
            metricPill = "-12.2 Q3",
            metricPillIsNegative = true
        ),
        ChatTopicCardUiModel(
            id = "open_inquiry",
            priorityBadge = "CONSULTA LLIURE",
            isPriority = false,
            isOpenInquiry = true,
            title = "Línia Base de la 1a Fase",
            description = "Pregunta qualsevol dubte sobre els 14 partits, rivals, anotació o tendències generals.",
            statusPill = "Xat Obert",
            metricPill = "",
            metricPillIsNegative = false
        )
    )

    val defaultDiagnosticEvidence = ChatDiagnosticEvidenceUiModel(
        priorityBadge = "Àrea de Millora #1 · Prioritària",
        title = "Control del Rebot Defensiu",
        subtitle = "Àrea crítica d'intervenció · Fase 1",
        metric1Label = "Punts 2a Oportunitat",
        metric1Value = "14.8",
        metric1Unit = "pts/partit",
        metric1Delta = "▲ +5.1 vs Promig Lliga",
        metric2Label = "Rebots Ofensius Concedits",
        metric2Value = "13.2",
        metric2Unit = "reb/partit",
        metric2Ranking = "Tercer pitjor de la competició",
        zeroAssumptionTitle = "Regla de Zero-Suposició (Zero-Assumption)",
        zeroAssumptionDescription = "Corroboració empírica basada en 14 partits consolidats. Sense hipòtesis prèvies.",
        courtPerimeterPct = "61%",
        courtInteriorPct = "39%",
        courtLeftWingPct = "29%",
        courtTopOfKeyPct = "32%",
        courtRightWingPct = "23%",
        courtPaintPct = "16%",
        methodologyTitle = "Mètode Socràtic Pia",
        methodologyCycle = "Evidència → Reflexió → Exercicis → Compromís"
    )

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
            senderName = "BasketCoach",
            senderRole = "Evidència Traçable",
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
            senderName = "Coach Jordi",
            senderRole = "Entrenador Principal",
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
            senderName = "BasketCoach",
            senderRole = "Corroborat per Dades",
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

    val previewUiState = ChatUiState(
        metaHeader = defaultMetaHeader,
        leftPaneMode = ChatLeftPaneMode.TOPIC_LIST,
        topicList = defaultTopicList,
        activeTopicId = "rebound_control",
        diagnosticEvidence = defaultDiagnosticEvidence,
        messages = initialMessages,
        inputText = "",
        isSending = false
    )

    val emptyUiState = ChatUiState(
        metaHeader = defaultMetaHeader,
        leftPaneMode = ChatLeftPaneMode.TOPIC_LIST,
        topicList = defaultTopicList,
        activeTopicId = DEFAULT_TOPIC_ID,
        diagnosticEvidence = defaultDiagnosticEvidence,
        messages = emptyList(),
        inputText = "",
        isSending = false
    )

    fun createCoachMessage(text: String): ChatMessageUiModel = ChatMessageUiModel(
        id = "msg_${System.currentTimeMillis()}",
        sender = MessageSender.COACH,
        senderName = "Coach Jordi",
        senderRole = "Entrenador Principal",
        timestamp = "10:25",
        text = text.trim(),
        hasCalloutQuestion = false,
        calloutQuestionText = "",
        calloutSubText = "",
        hasDrillOptions = false,
        drillOptions = emptyList(),
        hasSummaryCard = false,
        summaryCard = ChatSummaryCardUiModel.EMPTY
    )

    fun createDrillConfirmationMessage(drill: ChatDrillOptionUiModel): ChatMessageUiModel = ChatMessageUiModel(
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

    fun createSummaryAiMessage(): ChatMessageUiModel = ChatMessageUiModel(
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
        summaryCard = summaryCard
    )
}
