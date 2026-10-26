package com.martorell.albert.basketcoach.ui.dashboard.model

import androidx.annotation.StringRes
import com.martorell.albert.basketcoach.R

data class DashboardUiState(
    val summary: Phase1SummaryUiModel,
    val kpis: DashboardKpisUiModel,
    val evolution: ScoringEvolutionUiModel,
    val matches: List<MatchRowUiModel>
)

data class Phase1SummaryUiModel(
    val wins: Int,
    val losses: Int,
    val winPercentage: Double,
    val totalGames: Int,
    @param:StringRes val categoryRes: Int = R.string.topbar_category,
    @param:StringRes val teamNameRes: Int = R.string.hero_club_name
)

data class DashboardKpisUiModel(
    val scoring: ScoringKpiUiModel,
    val rebounds: ReboundsKpiUiModel,
    val shooting: ShootingKpiUiModel,
    val gameControl: GameControlKpiUiModel
)

data class ScoringKpiUiModel(
    val averageScore: Double,
    val opponentAverageScore: Double,
    val netDifferential: Double,
    val gamesCount: Int,
    val progressFraction: Float
)

data class ReboundsKpiUiModel(
    val averageRebounds: Double,
    val opponentAverageRebounds: Double,
    val netDifferential: Double,
    val offensiveRebounds: Double,
    val defensiveRebounds: Double
)

data class ShootingKpiUiModel(
    val efgPercentage: Double,
    val fg2Percentage: Double,
    val fg3Percentage: Double,
    val ftPercentage: Double
)

data class GameControlKpiUiModel(
    val astToRatio: Double,
    val assistsPerGame: Double,
    val turnoversPerGame: Double
)

data class ScoringEvolutionUiModel(
    val rounds: List<RoundEvolutionUiModel>
)

data class RoundEvolutionUiModel(
    val roundNumber: Int,
    val roundLabel: String,
    val piaScore: Int,
    val rivalScore: Int,
    val isWin: Boolean,
    val scoreBadge: String? = null
)

data class MatchRowUiModel(
    val roundNumber: Int,
    val roundLabel: String,
    val homeTeam: String,
    val awayTeam: String,
    val isPiaHome: Boolean,
    val piaScore: Int,
    val rivalScore: Int,
    val isWin: Boolean,
    val scoreDisplay: String,
    val efg: String,
    val fg2: String,
    val fg3: String,
    val ft: String,
    val rebounds: String,
    val ast: Int,
    val to: Int,
    val astTo: String,
    val stl: Int,
    val blk: Int,
    val pf: String,
    val secondChance: String,
    val fastBreak: String
)
