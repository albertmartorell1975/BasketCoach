package com.martorell.albert.basketcoach.ui.dashboard.model

import androidx.annotation.StringRes

/**
 * Root UI state model for the Phase 1 Basketball Dashboard adhering to the Hybrid UI State Pattern.
 *
 * @property content The primary mutually exclusive state (Loading, Success, Error).
 * @property isRefreshing Additive overlay flag indicating background or pull-to-refresh execution.
 */
data class DashboardUiState(
    val content: DashboardUiContent,
    val isRefreshing: Boolean
)

/**
 * Mutually exclusive content phases for the Phase 1 Dashboard screen.
 */
sealed interface DashboardUiContent {
    /**
     * Initial or passive loading state before domain data is resolved.
     */
    data object Loading : DashboardUiContent

    /**
     * Successful data state containing computed Phase 1 metrics, KPIs, evolution chart, and matches table.
     *
     * @property summary High-level Phase 1 record and balance overview.
     * @property kpis Aggregated key performance indicators across the four analytical pillars.
     * @property evolution Round-by-round scoring progression data.
     * @property matches Complete list of 14 match records displayed in the statistical table.
     */
    data class Success(
        val summary: Phase1SummaryUiModel,
        val kpis: DashboardKpisUiModel,
        val evolution: ScoringEvolutionUiModel,
        val matches: List<MatchRowUiModel>
    ) : DashboardUiContent

    /**
     * Error state displaying feedback and enabling retry recovery.
     *
     * @property message User-facing localized error description.
     */
    data class Error(
        val message: String
    ) : DashboardUiContent
}

/**
 * UI model representing the Phase 1 team summary and competitive balance.
 *
 * @property wins Total number of wins.
 * @property losses Total number of losses.
 * @property winPercentage Winning percentage (e.g. 21.4%).
 * @property totalGames Total games played in Phase 1 (14).
 * @property categoryRes String resource ID of the competition category.
 * @property teamNameRes String resource ID of the club name.
 */
data class Phase1SummaryUiModel(
    val wins: Int,
    val losses: Int,
    val winPercentage: Double,
    val totalGames: Int,
    @param:StringRes val categoryRes: Int,
    @param:StringRes val teamNameRes: Int
)

/**
 * Aggregated UI model grouping the four core analytical KPI pillars.
 *
 * @property scoring Average scoring and point differential metrics.
 * @property rebounds Rebounding averages, differentials, and offensive/defensive splits.
 * @property shooting Effective field goal percentage and shooting splits (2P, 3P, FT).
 * @property gameControl Assist-to-turnover ratio, volume assists, and turnovers.
 */
data class DashboardKpisUiModel(
    val scoring: ScoringKpiUiModel,
    val rebounds: ReboundsKpiUiModel,
    val shooting: ShootingKpiUiModel,
    val gameControl: GameControlKpiUiModel
)

/**
 * UI model for scoring KPIs.
 *
 * @property averageScore Team average points scored per match.
 * @property opponentAverageScore Opponent average points scored per match.
 * @property netDifferential Net score differential (team minus opponent).
 * @property gamesCount Total games evaluated.
 * @property progressFraction Relative visual progress bar fraction (0.0f..1.0f).
 */
data class ScoringKpiUiModel(
    val averageScore: Double,
    val opponentAverageScore: Double,
    val netDifferential: Double,
    val gamesCount: Int,
    val progressFraction: Float
)

/**
 * UI model for rebounding KPIs.
 *
 * @property averageRebounds Team average total rebounds per game.
 * @property opponentAverageRebounds Opponent average total rebounds per game.
 * @property netDifferential Net rebound differential.
 * @property offensiveRebounds Team average offensive rebounds.
 * @property defensiveRebounds Team average defensive rebounds.
 */
data class ReboundsKpiUiModel(
    val averageRebounds: Double,
    val opponentAverageRebounds: Double,
    val netDifferential: Double,
    val offensiveRebounds: Double,
    val defensiveRebounds: Double
)

/**
 * UI model for shooting efficiency KPIs.
 *
 * @property efgPercentage Effective field goal percentage (eFG%).
 * @property fg2Percentage Two-point field goal percentage.
 * @property fg3Percentage Three-point field goal percentage.
 * @property ftPercentage Free throw percentage.
 */
data class ShootingKpiUiModel(
    val efgPercentage: Double,
    val fg2Percentage: Double,
    val fg3Percentage: Double,
    val ftPercentage: Double
)

/**
 * UI model for game control and ball possession KPIs.
 *
 * @property astToRatio Assist-to-turnover ratio.
 * @property assistsPerGame Average assists per game.
 * @property turnoversPerGame Average turnovers per game.
 */
data class GameControlKpiUiModel(
    val astToRatio: Double,
    val assistsPerGame: Double,
    val turnoversPerGame: Double
)

/**
 * UI model grouping round-by-round scoring evolution data.
 *
 * @property rounds Ordered list of round evolution data points from J1 to J14.
 */
data class ScoringEvolutionUiModel(
    val rounds: List<RoundEvolutionUiModel>
)

/**
 * UI model representing a single round's scoring outcome in the evolution chart.
 *
 * @property roundNumber Competition round index (1..14).
 * @property roundLabel Short round identifier (e.g. "J1").
 * @property piaScore Points scored by CEB Pia Sabadell.
 * @property rivalScore Points scored by the opponent team.
 * @property isWin `true` if Pia Sabadell won the match; `false` otherwise.
 * @property scoreBadge Text badge displayed above winning bars (e.g. "W 71-68").
 */
data class RoundEvolutionUiModel(
    val roundNumber: Int,
    val roundLabel: String,
    val piaScore: Int,
    val rivalScore: Int,
    val isWin: Boolean,
    val scoreBadge: String
)

/**
 * UI model representing a single fixture row in the 16-column Phase 1 statistical table.
 *
 * @property roundNumber Round index (1..14).
 * @property roundLabel Round label (e.g. "J1").
 * @property homeTeam Home team name.
 * @property awayTeam Away team name.
 * @property isPiaHome `true` if Pia Sabadell was the home team; `false` if away.
 * @property piaScore Points scored by Pia Sabadell.
 * @property rivalScore Points scored by opponent.
 * @property isWin `true` if Pia won the match; `false` otherwise.
 * @property scoreDisplay Formatted score string (e.g. "62 - 78").
 * @property efg Formatted effective field goal percentage string (e.g. "41.2%").
 * @property fg2 Formatted two-point shooting percentage string.
 * @property fg3 Formatted three-point shooting percentage string.
 * @property ft Formatted free throw percentage string.
 * @property rebounds Total rebounds string with offensive/defensive split (e.g. "24 (6/18)").
 * @property ast Total assists.
 * @property to Total turnovers.
 * @property astTo Assist-to-turnover ratio string.
 * @property stl Total steals.
 * @property blk Total blocks.
 * @property pf Total personal fouls committed string.
 * @property secondChance Second-chance points string.
 * @property fastBreak Fast-break points string.
 */
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
