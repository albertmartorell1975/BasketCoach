package com.martorell.albert.basketcoach.usecases.fakes

import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.OUR_TEAM_NAME
import com.martorell.albert.basketcoach.domain.model.Score
import com.martorell.albert.basketcoach.domain.model.TeamStats
import com.martorell.albert.basketcoach.domain.model.Venue

object TestGameFactory {

    fun createGame(
        round: Int = 1,
        isHome: Boolean = true,
        teamPoints: Int = 70,
        oppPoints: Int = 65,
        opponentName: String = "CB Opponent",
        assists: Int = 12,
        turnovers: Int = 10,
        totalRebounds: Int = 35,
        oppTotalRebounds: Int = 30,
        personalFouls: Int = 18,
        fg2Pct: Double = 50.0,
        fg3Pct: Double = 33.3,
        ftPct: Double = 75.0
    ): Game {
        val homeTeam = if (isHome) OUR_TEAM_NAME else opponentName
        val awayTeam = if (isHome) opponentName else OUR_TEAM_NAME
        val homePoints = if (isHome) teamPoints else oppPoints
        val awayPoints = if (isHome) oppPoints else teamPoints

        val stats = TeamStats(
            ftPct = ftPct,
            fg2Pct = fg2Pct,
            fg3Pct = fg3Pct,
            efgPct = 52.0,
            offensiveRebounds = 10,
            defensiveRebounds = totalRebounds - 10,
            totalRebounds = totalRebounds,
            turnovers = turnovers,
            assists = assists,
            astToRatio = if (turnovers > 0) assists.toDouble() / turnovers else 0.0,
            steals = 8,
            blocks = 3,
            secondChancePoints = 12,
            fastBreakPoints = 14,
            personalFouls = personalFouls,
            points = teamPoints
        )

        val oppStats = TeamStats(
            ftPct = 65.0,
            fg2Pct = 45.0,
            fg3Pct = 28.0,
            efgPct = 48.0,
            offensiveRebounds = 8,
            defensiveRebounds = oppTotalRebounds - 8,
            totalRebounds = oppTotalRebounds,
            turnovers = 15,
            assists = 10,
            astToRatio = 0.67,
            steals = 6,
            blocks = 2,
            secondChancePoints = 8,
            fastBreakPoints = 10,
            personalFouls = 20,
            points = oppPoints
        )

        return Game(
            round = round,
            date = "2023-10-01",
            time = "18:00",
            venue = Venue(city = "Sabadell", arena = "Pabellon", address = "Carrer Major 1"),
            homeTeam = homeTeam,
            awayTeam = awayTeam,
            finalScore = Score(homePoints = homePoints, awayPoints = awayPoints),
            homeStats = if (isHome) stats else oppStats,
            awayStats = if (isHome) oppStats else stats
        )
    }
}
