package com.martorell.albert.basketcoach.ui.chat.model

import androidx.annotation.StringRes

/**
 * Presentation model representing deep empirical diagnostic evidence and court spatial distribution
 * for an active intervention area in [com.martorell.albert.basketcoach.ui.chat.components.ChatDiagnosticEvidenceView].
 *
 * Grounded strictly in Phase 1 match statistics and Zero-Assumption methodology.
 *
 * @property priorityBadgeRes Resource ID for the header priority badge text.
 * @property titleRes Resource ID for the name of the tactical intervention area.
 * @property subtitleRes Resource ID for the empirical phase descriptor.
 * @property metric1LabelRes Resource ID for the primary box-score metric label.
 * @property metric1Value Formatted numerical value for metric 1.
 * @property metric1UnitRes Resource ID for the measurement unit for metric 1.
 * @property metric1DeltaRes Resource ID for the performance delta compared to league baseline.
 * @property metric2LabelRes Resource ID for the secondary box-score metric label.
 * @property metric2Value Formatted numerical value for metric 2.
 * @property metric2UnitRes Resource ID for the measurement unit for metric 2.
 * @property metric2RankingRes Resource ID for the league or competition ranking indicator.
 * @property zeroAssumptionTitleRes Resource ID for the Zero-Assumption validation card title.
 * @property zeroAssumptionDescriptionRes Resource ID for the Zero-Assumption empirical verification summary.
 * @property courtPerimeterPct Percentage of conceded opportunities originating from perimeter.
 * @property courtInteriorPct Percentage of conceded opportunities originating from interior/paint.
 * @property courtLeftWingPct Left wing zone percentage.
 * @property courtTopOfKeyPct Top of the key zone percentage.
 * @property courtRightWingPct Right wing zone percentage.
 * @property courtPaintPct Paint zone percentage.
 * @property methodologyTitleRes Resource ID for the Socratic coaching methodology title.
 * @property methodologyCycleRes Resource ID for the step-by-step coaching cycle description.
 */
data class ChatDiagnosticEvidenceUiModel(
    @param:StringRes val priorityBadgeRes: Int,
    @param:StringRes val titleRes: Int,
    @param:StringRes val subtitleRes: Int,
    @param:StringRes val metric1LabelRes: Int,
    val metric1Value: String,
    @param:StringRes val metric1UnitRes: Int,
    @param:StringRes val metric1DeltaRes: Int,
    @param:StringRes val metric2LabelRes: Int,
    val metric2Value: String,
    @param:StringRes val metric2UnitRes: Int,
    @param:StringRes val metric2RankingRes: Int,
    @param:StringRes val zeroAssumptionTitleRes: Int,
    @param:StringRes val zeroAssumptionDescriptionRes: Int,
    val courtPerimeterPct: String,
    val courtInteriorPct: String,
    val courtLeftWingPct: String,
    val courtTopOfKeyPct: String,
    val courtRightWingPct: String,
    val courtPaintPct: String,
    @param:StringRes val methodologyTitleRes: Int,
    @param:StringRes val methodologyCycleRes: Int
)
