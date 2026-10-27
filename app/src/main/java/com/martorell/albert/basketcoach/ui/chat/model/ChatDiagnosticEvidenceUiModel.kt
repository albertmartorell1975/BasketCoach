package com.martorell.albert.basketcoach.ui.chat.model

/**
 * Presentation model representing deep empirical diagnostic evidence and court spatial distribution
 * for an active intervention area in [com.martorell.albert.basketcoach.ui.chat.components.ChatDiagnosticEvidenceView].
 *
 * Grounded strictly in Phase 1 match statistics and Zero-Assumption methodology.
 *
 * @property priorityBadge Header priority badge text.
 * @property title Name of the tactical intervention area.
 * @property subtitle Empirical phase descriptor.
 * @property metric1Label Primary box-score metric label (e.g., "Punts 2a Oportunitat").
 * @property metric1Value Formatted numerical value for metric 1.
 * @property metric1Unit Measurement unit for metric 1 (e.g., "pts/partit").
 * @property metric1Delta Performance delta compared to league baseline.
 * @property metric2Label Secondary box-score metric label (e.g., "Rebots Ofensius Concedits").
 * @property metric2Value Formatted numerical value for metric 2.
 * @property metric2Unit Measurement unit for metric 2 (e.g., "reb/partit").
 * @property metric2Ranking League or competition ranking indicator.
 * @property zeroAssumptionTitle Zero-Assumption validation card title.
 * @property zeroAssumptionDescription Zero-Assumption empirical verification summary.
 * @property courtPerimeterPct Percentage of conceded opportunities originating from perimeter.
 * @property courtInteriorPct Percentage of conceded opportunities originating from interior/paint.
 * @property courtLeftWingPct Left wing zone percentage.
 * @property courtTopOfKeyPct Top of the key zone percentage.
 * @property courtRightWingPct Right wing zone percentage.
 * @property courtPaintPct Paint zone percentage.
 * @property methodologyTitle Socratic coaching methodology title.
 * @property methodologyCycle Step-by-step coaching cycle description.
 */
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
