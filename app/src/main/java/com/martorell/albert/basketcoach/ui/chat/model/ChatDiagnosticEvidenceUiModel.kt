package com.martorell.albert.basketcoach.ui.chat.model

data class ChatDiagnosticEvidenceUiModel(
    val priorityBadge: String,
    val title: String,
    val subtitle: String,
    val metric1Label: String,
    val metric1Value: String,
    val metric1Unit: String,
    val metric1Delta: String,
    val metric2Label: String,
    val metric2Value: String,
    val metric2Unit: String,
    val metric2Ranking: String,
    val zeroAssumptionTitle: String,
    val zeroAssumptionDescription: String,
    val courtPerimeterPct: String,
    val courtInteriorPct: String,
    val courtLeftWingPct: String,
    val courtTopOfKeyPct: String,
    val courtRightWingPct: String,
    val courtPaintPct: String,
    val methodologyTitle: String,
    val methodologyCycle: String
)
