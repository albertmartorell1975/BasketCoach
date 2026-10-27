package com.martorell.albert.basketcoach.ui.analysis.data

import com.martorell.albert.basketcoach.domain.model.InsightType
import com.martorell.albert.basketcoach.domain.model.MetricSentiment
import com.martorell.albert.basketcoach.ui.analysis.model.AnalysisUiState
import com.martorell.albert.basketcoach.ui.analysis.model.InsightCardUiModel
import com.martorell.albert.basketcoach.ui.analysis.model.InsightFilterTab
import com.martorell.albert.basketcoach.ui.dashboard.data.Phase1PreviewData
import com.martorell.albert.basketcoach.ui.dashboard.model.Phase1SummaryUiModel

/**
 * Authoritative preview data and state factory for the Tablet Landscape Analysis screen.
 *
 * Supplies 5 authentic tactical insight cards grounded in the 14-game Phase 1 competition dataset
 * (2 Strengths and 3 Areas of Improvement).
 */
object AnalysisPreviewData {

    val summary: Phase1SummaryUiModel = Phase1PreviewData.summary

    val allInsights: List<InsightCardUiModel> = listOf(
        InsightCardUiModel(
            id = "insight_strength_1",
            type = InsightType.STRENGTH,
            categoryBadgeText = "Fortalesa Tàctica",
            contextTag = "Fase Ofensiva · Pintura",
            title = "Eficiència en Tir Interior",
            description = "Alta productivitat en finalitzacions sota cistella i talls ràpids per línia de fons. La generació d'avantatges interiors genera 34.6 pts de mitjana a la pintura amb excel·lents percentatges.",
            primaryMetricLabel = "Percentatge T2 Zona",
            primaryMetricValue = "58.4%",
            primaryMetricSubValue = "+8.2% sobre mitjana lliga",
            secondaryMetricLabel = "Punts / Pos.",
            secondaryMetricValue = "1.24 pts/pos",
            secondaryMetricTag = "dataset:paint_eff",
            metricSentiment = MetricSentiment.POSITIVE,
            footerNote = "Factor diferencial en 3 de les 3 victòries obtingudes.",
            canExploreChat = false,
            exploreChatPrompt = ""
        ),
        InsightCardUiModel(
            id = "insight_strength_2",
            type = InsightType.STRENGTH,
            categoryBadgeText = "Fortalesa Tàctica",
            contextTag = "Fase Defensiva · Intimidació",
            title = "Protecció de Cistella",
            description = "Gran solidesa en la defensa de l'1c1 interior i ajudes manuals coordinades dels pivots. El bloqueig directe rival es veu contingut forçant tirs exteriors de baix percentatge.",
            primaryMetricLabel = "T2 Rival Concedit",
            primaryMetricValue = "39.1%",
            primaryMetricSubValue = "4.8 taps / partit",
            secondaryMetricLabel = "Evidència Digital",
            secondaryMetricValue = "14 Partits",
            secondaryMetricTag = "dataset:rim_def",
            metricSentiment = MetricSentiment.POSITIVE,
            footerNote = "Només 28.4 punts encaixats a la pintura de mitjana.",
            canExploreChat = false,
            exploreChatPrompt = ""
        ),
        InsightCardUiModel(
            id = "insight_improvement_1",
            type = InsightType.IMPROVEMENT,
            categoryBadgeText = "Àrea de Millora",
            contextTag = "Transició · Balanç Defensiu",
            title = "Balanç Defensiu en Transició",
            description = "Poca comunicació en el retrocés defensiu i mal repartiment de carril després de llançament fallat. L'equip concedeix cistelles fàcils en superioritat rival en primers 5 segons.",
            primaryMetricLabel = "Punts Contraatac Rivals",
            primaryMetricValue = "16.2",
            primaryMetricSubValue = "1.31 pts/pos en transició rival",
            secondaryMetricLabel = "Prioritat",
            secondaryMetricValue = "Crítica #1",
            secondaryMetricTag = "dataset:fastbreak_def",
            metricSentiment = MetricSentiment.NEGATIVE,
            footerNote = "Pilar corrector Fase 2",
            canExploreChat = true,
            exploreChatPrompt = "Vull millorar el balanç defensiu en transició per a la Fase 2. Quins exercicis d'entrenament podem aplicar per reduir els punts de contraatac rival?"
        ),
        InsightCardUiModel(
            id = "insight_improvement_2",
            type = InsightType.IMPROVEMENT,
            categoryBadgeText = "Àrea de Millora",
            contextTag = "Rebot · Box Out",
            title = "Rebot Defensiu i Punts de 2a Oportunitat",
            description = "Irregularitat en el bloqueig del rebot defensiu per part de la línia exterior. Concedim segones opcions de tir que mantenen els rivals al partit fins als minuts decisius.",
            primaryMetricLabel = "Rebots Ofensius Concedits",
            primaryMetricValue = "11.6",
            primaryMetricSubValue = "14.8 pts de 2a oportunitat",
            secondaryMetricLabel = "Evidència Empírica",
            secondaryMetricValue = "11 Derrotes",
            secondaryMetricTag = "-7.8 reb en Fase 1",
            metricSentiment = MetricSentiment.NEGATIVE,
            footerNote = "Diferència -7.8 rebots en Fase 1",
            canExploreChat = true,
            exploreChatPrompt = "Com podem reforçar el rebot defensiu i el bloqueig (box out) dels jugadors exteriors a la Fase 2?"
        ),
        InsightCardUiModel(
            id = "insight_improvement_3",
            type = InsightType.IMPROVEMENT,
            categoryBadgeText = "Àrea de Millora",
            contextTag = "Concentració · Gestió Temporal",
            title = "Gestió de Pèrdues en el 3r Quart",
            description = "Desconnexió habitual a l'inici de la segona meitat. Els primers 4-6 minuts del tercer quart acumulen pèrdues no forçades i precipitació en la circulació de pilota.",
            primaryMetricLabel = "Pèrdues Concentrades",
            primaryMetricValue = "42%",
            primaryMetricSubValue = "Ràtio 0.51 AST/TO",
            secondaryMetricLabel = "Impacte Directe",
            secondaryMetricValue = "6.4 pts rival",
            secondaryMetricTag = "dataset:q3_turnovers",
            metricSentiment = MetricSentiment.NEGATIVE,
            footerNote = "+4.2 TO/partit al 3Q",
            canExploreChat = true,
            exploreChatPrompt = "Com podem treballar la concentració mental i la presa de decisions dels jugadors a l'inici del 3r quart per evitar ratxes de pèrdues?"
        )
    )

    fun filterInsights(selectedFilter: InsightFilterTab): List<InsightCardUiModel> {
        return when (selectedFilter) {
            InsightFilterTab.ALL -> allInsights
            InsightFilterTab.STRENGTHS -> allInsights.filter { it.type == InsightType.STRENGTH }
            InsightFilterTab.IMPROVEMENTS -> allInsights.filter { it.type == InsightType.IMPROVEMENT }
        }
    }

    fun createUiState(selectedFilter: InsightFilterTab = InsightFilterTab.ALL): AnalysisUiState {
        val filtered = filterInsights(selectedFilter)
        val strengthsCount = allInsights.count { it.type == InsightType.STRENGTH }
        val improvementsCount = allInsights.count { it.type == InsightType.IMPROVEMENT }
        return AnalysisUiState(
            summary = summary,
            selectedFilter = selectedFilter,
            totalCount = allInsights.size,
            strengthsCount = strengthsCount,
            improvementsCount = improvementsCount,
            displayedInsights = filtered
        )
    }

    val uiState: AnalysisUiState = createUiState(InsightFilterTab.ALL)
}
