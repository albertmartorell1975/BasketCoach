package com.martorell.albert.basketcoach.ui.dashboard.mapper

import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardKpisUiModel
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardUiContent
import com.martorell.albert.basketcoach.ui.dashboard.model.GameControlKpiUiModel
import com.martorell.albert.basketcoach.ui.dashboard.model.MatchRowUiModel
import com.martorell.albert.basketcoach.ui.dashboard.model.Phase1SummaryUiModel
import com.martorell.albert.basketcoach.ui.dashboard.model.ReboundsKpiUiModel
import com.martorell.albert.basketcoach.ui.dashboard.model.RoundEvolutionUiModel
import com.martorell.albert.basketcoach.ui.dashboard.model.ScoringEvolutionUiModel
import com.martorell.albert.basketcoach.ui.dashboard.model.ScoringKpiUiModel
import com.martorell.albert.basketcoach.ui.dashboard.model.ShootingKpiUiModel
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Sole authoritative transformation engine mapping authentic Phase 1 [Game] domain entities
 * into presentation models ([DashboardUiContent.Success]).
 *
 * Computes:
 * - Summary record: 3V-11D, 21.4% win percentage over 14 games.
 * - 4 analytical KPI pillars: Scoring (64.6 vs 73.7, net -9.1), Rebounds (22.8 vs 30.4, net -7.6,
 *   off 6.3, def 16.5), Shooting (eFG% 40.5%, 2P 43.4%, 3P 25.4%, FT 56.4%), and Game Control
 *   (AST/TO 0.65, assists 10.3, turnovers 17.6).
 * - 14-round scoring progression bars with win flags for J3, J9, and J10.
 * - 14-row statistical table across 16 statistical columns with normalized team names.
 */
class DashboardUiMapper @Inject constructor() {

    /**
     * Transforms a non-empty list of [Game] fixtures into [DashboardUiContent.Success].
     *
     * @param games The list of authentic Phase 1 games to aggregate.
     * @return Fully populated, non-nullable [DashboardUiContent.Success] presentation model.
     * @throws IllegalArgumentException if [games] is empty.
     */
    fun toSuccess(games: List<Game>): DashboardUiContent.Success {
        require(games.isNotEmpty()) { "Cannot compute Phase 1 dashboard metrics from an empty game list." }

        val sortedGames = games.sortedBy { it.round }
        val totalGames = sortedGames.size
        val wins = sortedGames.count { it.isWin }
        val losses = totalGames - wins
        val winPercentage = roundToOneDecimal((wins.toDouble() / totalGames) * 100)

        val avgScore = roundToOneDecimal(sortedGames.map { it.teamScore }.average())
        val avgOppScore = roundToOneDecimal(sortedGames.map { it.opponentScore }.average())
        val netScoreDiff = roundToOneDecimal(avgScore - avgOppScore)
        val progressFraction = if (avgScore + avgOppScore > 0) {
            ((avgScore / (avgScore + avgOppScore)).toFloat() * 100f).roundToInt() / 100f
        } else {
            0.5f
        }

        val avgRebounds = roundToOneDecimal(sortedGames.map { it.teamStats.totalRebounds }.average())
        val avgOppRebounds = roundToOneDecimal(sortedGames.map { it.opponentStats.totalRebounds }.average())
        val netReboundsDiff = roundToOneDecimal(avgRebounds - avgOppRebounds)
        val offRebounds = roundToOneDecimal(sortedGames.map { it.teamStats.offensiveRebounds }.average())
        val defRebounds = roundToOneDecimal(sortedGames.map { it.teamStats.defensiveRebounds }.average())

        val efgPct = roundToOneDecimal(sortedGames.map { it.teamStats.efgPct }.average())
        val fg2Pct = roundToOneDecimal(sortedGames.map { it.teamStats.fg2Pct }.average())
        val fg3Pct = roundToOneDecimal(sortedGames.map { it.teamStats.fg3Pct }.average())
        val ftPct = roundToOneDecimal(sortedGames.map { it.teamStats.ftPct }.average())

        val astToRatio = roundToTwoDecimals(sortedGames.map { it.teamStats.astToRatio }.average())
        val avgAssists = roundToOneDecimal(sortedGames.map { it.teamStats.assists }.average())
        val avgTurnovers = roundToOneDecimal(sortedGames.map { it.teamStats.turnovers }.average())

        val summary = Phase1SummaryUiModel(
            wins = wins,
            losses = losses,
            winPercentage = winPercentage,
            totalGames = totalGames,
            categoryRes = R.string.topbar_category,
            teamNameRes = R.string.hero_club_name
        )

        val kpis = DashboardKpisUiModel(
            scoring = ScoringKpiUiModel(
                averageScore = avgScore,
                opponentAverageScore = avgOppScore,
                netDifferential = netScoreDiff,
                gamesCount = totalGames,
                progressFraction = progressFraction
            ),
            rebounds = ReboundsKpiUiModel(
                averageRebounds = avgRebounds,
                opponentAverageRebounds = avgOppRebounds,
                netDifferential = netReboundsDiff,
                offensiveRebounds = offRebounds,
                defensiveRebounds = defRebounds
            ),
            shooting = ShootingKpiUiModel(
                efgPercentage = efgPct,
                fg2Percentage = fg2Pct,
                fg3Percentage = fg3Pct,
                ftPercentage = ftPct
            ),
            gameControl = GameControlKpiUiModel(
                astToRatio = astToRatio,
                assistsPerGame = avgAssists,
                turnoversPerGame = avgTurnovers
            )
        )

        val evolutionRounds = sortedGames.map { game ->
            RoundEvolutionUiModel(
                roundNumber = game.round,
                piaScore = game.teamScore,
                rivalScore = game.opponentScore,
                isWin = game.isWin
            )
        }

        val matchRows = sortedGames.map { game ->
            MatchRowUiModel(
                roundNumber = game.round,
                homeTeam = formatTeamDisplayName(game.homeTeam),
                awayTeam = formatTeamDisplayName(game.awayTeam),
                isPiaHome = game.isHomeGame,
                homePoints = game.finalScore.homePoints,
                awayPoints = game.finalScore.awayPoints,
                isWin = game.isWin,
                efgPct = roundToOneDecimal(game.teamStats.efgPct),
                fg2Pct = roundToOneDecimal(game.teamStats.fg2Pct),
                fg3Pct = roundToOneDecimal(game.teamStats.fg3Pct),
                ftPct = roundToOneDecimal(game.teamStats.ftPct),
                totalRebounds = game.teamStats.totalRebounds,
                offensiveRebounds = game.teamStats.offensiveRebounds,
                defensiveRebounds = game.teamStats.defensiveRebounds,
                ast = game.teamStats.assists,
                to = game.teamStats.turnovers,
                astTo = roundToTwoDecimals(game.teamStats.astToRatio),
                stl = game.teamStats.steals,
                blk = game.teamStats.blocks,
                personalFouls = game.teamStats.personalFouls,
                secondChancePoints = game.teamStats.secondChancePoints,
                opponentSecondChancePoints = game.opponentStats.secondChancePoints,
                fastBreakPoints = game.teamStats.fastBreakPoints,
                opponentFastBreakPoints = game.opponentStats.fastBreakPoints
            )
        }

        return DashboardUiContent.Success(
            summary = summary,
            kpis = kpis,
            evolution = ScoringEvolutionUiModel(evolutionRounds),
            matches = matchRows
        )
    }

    private fun formatTeamDisplayName(rawName: String): String {
        val upper = rawName.uppercase().trim()
        return when {
            upper.contains("BASQUETPIASABADELL") || upper.contains("PIA SABADELL") -> "CEB Escola Pia Sabadell"
            upper.contains("CORNELL") -> "CB Cornellà"
            upper.contains("CARDEDEU") -> "CB Cardedeu"
            upper.contains("SANT JORDI") -> "CEB Sant Jordi Blau"
            upper.contains("PRAT") -> "CB Prat"
            upper.contains("SANT PERE") -> "Bàsquet Sant Pere A"
            upper.contains("SANT JUST") -> "CB Sant Just"
            upper.contains("NATACI") -> "Club Natació Sabadell A"
            else -> rawName
        }
    }

    private fun roundToOneDecimal(value: Double): Double {
        return (value * 10.0).roundToInt() / 10.0
    }

    private fun roundToTwoDecimals(value: Double): Double {
        return (value * 100.0).roundToInt() / 100.0
    }
}
