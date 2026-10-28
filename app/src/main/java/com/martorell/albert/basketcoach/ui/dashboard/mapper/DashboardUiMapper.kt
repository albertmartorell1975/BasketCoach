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
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt

class DashboardUiMapper @Inject constructor() {

    fun toSuccess(games: List<Game>): DashboardUiContent.Success {
        if (games.isEmpty()) {
            return DashboardUiContent.Success(
                summary = Phase1SummaryUiModel(
                    wins = 0,
                    losses = 0,
                    winPercentage = 0.0,
                    totalGames = 0,
                    categoryRes = R.string.topbar_category,
                    teamNameRes = R.string.hero_club_name
                ),
                kpis = DashboardKpisUiModel(
                    scoring = ScoringKpiUiModel(0.0, 0.0, 0.0, 0, 0f),
                    rebounds = ReboundsKpiUiModel(0.0, 0.0, 0.0, 0.0, 0.0),
                    shooting = ShootingKpiUiModel(0.0, 0.0, 0.0, 0.0),
                    gameControl = GameControlKpiUiModel(0.0, 0.0, 0.0)
                ),
                evolution = ScoringEvolutionUiModel(emptyList()),
                matches = emptyList()
            )
        }

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
            val scoreBadge = if (game.isWin) "W ${game.teamScore}-${game.opponentScore}" else ""
            RoundEvolutionUiModel(
                roundNumber = game.round,
                roundLabel = "J${game.round}",
                piaScore = game.teamScore,
                rivalScore = game.opponentScore,
                isWin = game.isWin,
                scoreBadge = scoreBadge
            )
        }

        val matchRows = sortedGames.map { game ->
            val isPiaHome = game.isHomeGame
            val scoreDisplay = "${game.finalScore.homePoints} - ${game.finalScore.awayPoints}"
            MatchRowUiModel(
                roundNumber = game.round,
                roundLabel = "J${game.round}",
                homeTeam = normalizeTeamName(game.homeTeam),
                awayTeam = normalizeTeamName(game.awayTeam),
                isPiaHome = isPiaHome,
                piaScore = game.teamScore,
                rivalScore = game.opponentScore,
                isWin = game.isWin,
                scoreDisplay = scoreDisplay,
                efg = "${formatOneDecimal(game.teamStats.efgPct)}%",
                fg2 = "${formatOneDecimal(game.teamStats.fg2Pct)}%",
                fg3 = "${formatOneDecimal(game.teamStats.fg3Pct)}%",
                ft = "${formatOneDecimal(game.teamStats.ftPct)}%",
                rebounds = "${game.teamStats.totalRebounds} (${game.teamStats.offensiveRebounds}/${game.teamStats.defensiveRebounds})",
                ast = game.teamStats.assists,
                to = game.teamStats.turnovers,
                astTo = String.format(Locale.US, "%.2f", game.teamStats.astToRatio),
                stl = game.teamStats.steals,
                blk = game.teamStats.blocks,
                pf = "${game.teamStats.personalFouls} PF",
                secondChance = "${game.teamStats.secondChancePoints} pts vs ${game.opponentStats.secondChancePoints}",
                fastBreak = "${game.teamStats.fastBreakPoints} pts vs ${game.opponentStats.fastBreakPoints}"
            )
        }

        return DashboardUiContent.Success(
            summary = summary,
            kpis = kpis,
            evolution = ScoringEvolutionUiModel(evolutionRounds),
            matches = matchRows
        )
    }

    private fun normalizeTeamName(rawName: String): String {
        val upper = rawName.uppercase().trim()
        return when {
            upper.contains("BASQUETPIASABADELL") || upper.contains("PIA SABADELL") -> "CEB Pia Sabadell"
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

    private fun formatOneDecimal(value: Double): String {
        return String.format(Locale.US, "%.1f", value)
    }
}
