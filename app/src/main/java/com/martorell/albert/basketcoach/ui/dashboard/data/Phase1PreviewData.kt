package com.martorell.albert.basketcoach.ui.dashboard.data

import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardKpisUiModel
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardUiContent
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardUiState
import com.martorell.albert.basketcoach.ui.dashboard.model.GameControlKpiUiModel
import com.martorell.albert.basketcoach.ui.dashboard.model.MatchRowUiModel
import com.martorell.albert.basketcoach.ui.dashboard.model.Phase1SummaryUiModel
import com.martorell.albert.basketcoach.ui.dashboard.model.ReboundsKpiUiModel
import com.martorell.albert.basketcoach.ui.dashboard.model.RoundEvolutionUiModel
import com.martorell.albert.basketcoach.ui.dashboard.model.ScoringEvolutionUiModel
import com.martorell.albert.basketcoach.ui.dashboard.model.ScoringKpiUiModel
import com.martorell.albert.basketcoach.ui.dashboard.model.ShootingKpiUiModel

/**
 * Static authentic preview and fallback dataset for Phase 1 of the Junior Men's Interterritorial competition.
 *
 * Models the complete 14-game record (3 Victories, 11 Defeats) of CEB Escola Pia Sabadell,
 * matching official statistics parsed from `dataset.json`.
 */
object Phase1PreviewData {

    /**
     * Phase 1 summary baseline with 3 wins, 11 losses, 21.4% win rate over 14 games.
     */
    val summary = Phase1SummaryUiModel(
        wins = 3,
        losses = 11,
        winPercentage = 21.4,
        totalGames = 14,
        categoryRes = R.string.topbar_category,
        teamNameRes = R.string.hero_club_name
    )

    /**
     * Aggregated KPI metrics for Phase 1 (scoring 64.6, rebounds 22.8, eFG% 40.5%, AST/TO 0.59).
     */
    val kpis = DashboardKpisUiModel(
        scoring = ScoringKpiUiModel(
            averageScore = 64.6,
            opponentAverageScore = 73.7,
            netDifferential = -9.1,
            gamesCount = 14,
            progressFraction = 0.47f
        ),
        rebounds = ReboundsKpiUiModel(
            averageRebounds = 22.8,
            opponentAverageRebounds = 30.6,
            netDifferential = -7.8,
            offensiveRebounds = 5.9,
            defensiveRebounds = 16.9
        ),
        shooting = ShootingKpiUiModel(
            efgPercentage = 40.5,
            fg2Percentage = 43.4,
            fg3Percentage = 25.4,
            ftPercentage = 56.4
        ),
        gameControl = GameControlKpiUiModel(
            astToRatio = 0.59,
            assistsPerGame = 10.3,
            turnoversPerGame = 17.6
        )
    )

    /**
     * 14-round scoring evolution data points with green win badges on J3, J9, and J10.
     */
    val evolution = ScoringEvolutionUiModel(
        rounds = listOf(
            RoundEvolutionUiModel(1, 62, 78, false),
            RoundEvolutionUiModel(2, 70, 79, false),
            RoundEvolutionUiModel(3, 71, 68, true),
            RoundEvolutionUiModel(4, 62, 78, false),
            RoundEvolutionUiModel(5, 65, 71, false),
            RoundEvolutionUiModel(6, 64, 81, false),
            RoundEvolutionUiModel(7, 63, 70, false),
            RoundEvolutionUiModel(8, 66, 80, false),
            RoundEvolutionUiModel(9, 68, 65, true),
            RoundEvolutionUiModel(10, 67, 65, true),
            RoundEvolutionUiModel(11, 58, 72, false),
            RoundEvolutionUiModel(12, 58, 74, false),
            RoundEvolutionUiModel(13, 61, 75, false),
            RoundEvolutionUiModel(14, 69, 76, false)
        )
    )

    /**
     * Complete list of 14 Phase 1 matches with 16 statistical columns per match.
     */
    val matches = listOf(
        MatchRowUiModel(
            roundNumber = 1,
            homeTeam = "CEB Escola Pia Sabadell",
            awayTeam = "CB Cornellà",
            isPiaHome = true,
            homePoints = 62,
            awayPoints = 78,
            isWin = false,
            efgPct = 38.6,
            fg2Pct = 42.1,
            fg3Pct = 21.4,
            ftPct = 52.4,
            totalRebounds = 21,
            offensiveRebounds = 5,
            defensiveRebounds = 16,
            ast = 8,
            to = 19,
            astTo = 0.42,
            stl = 4,
            blk = 1,
            personalFouls = 22,
            secondChancePoints = 4,
            opponentSecondChancePoints = 14,
            fastBreakPoints = 6,
            opponentFastBreakPoints = 18
        ),
        MatchRowUiModel(
            roundNumber = 2,
            homeTeam = "CB Cardedeu",
            awayTeam = "CEB Escola Pia Sabadell",
            isPiaHome = false,
            homePoints = 79,
            awayPoints = 70,
            isWin = false,
            efgPct = 42.3,
            fg2Pct = 45.0,
            fg3Pct = 28.6,
            ftPct = 56.3,
            totalRebounds = 23,
            offensiveRebounds = 6,
            defensiveRebounds = 17,
            ast = 11,
            to = 17,
            astTo = 0.65,
            stl = 5,
            blk = 1,
            personalFouls = 21,
            secondChancePoints = 6,
            opponentSecondChancePoints = 12,
            fastBreakPoints = 8,
            opponentFastBreakPoints = 14
        ),
        MatchRowUiModel(
            roundNumber = 3,
            homeTeam = "CEB Escola Pia Sabadell",
            awayTeam = "CEB Sant Jordi Blau",
            isPiaHome = true,
            homePoints = 71,
            awayPoints = 68,
            isWin = true,
            efgPct = 48.2,
            fg2Pct = 51.2,
            fg3Pct = 33.3,
            ftPct = 70.6,
            totalRebounds = 29,
            offensiveRebounds = 9,
            defensiveRebounds = 20,
            ast = 15,
            to = 12,
            astTo = 1.25,
            stl = 8,
            blk = 3,
            personalFouls = 17,
            secondChancePoints = 10,
            opponentSecondChancePoints = 8,
            fastBreakPoints = 12,
            opponentFastBreakPoints = 9
        ),
        MatchRowUiModel(
            roundNumber = 4,
            homeTeam = "CB Prat",
            awayTeam = "CEB Escola Pia Sabadell",
            isPiaHome = false,
            homePoints = 78,
            awayPoints = 62,
            isWin = false,
            efgPct = 37.5,
            fg2Pct = 40.0,
            fg3Pct = 22.2,
            ftPct = 53.3,
            totalRebounds = 21,
            offensiveRebounds = 5,
            defensiveRebounds = 16,
            ast = 8,
            to = 19,
            astTo = 0.42,
            stl = 4,
            blk = 0,
            personalFouls = 23,
            secondChancePoints = 4,
            opponentSecondChancePoints = 15,
            fastBreakPoints = 5,
            opponentFastBreakPoints = 18
        ),
        MatchRowUiModel(
            roundNumber = 5,
            homeTeam = "CEB Escola Pia Sabadell",
            awayTeam = "Bàsquet Sant Pere A",
            isPiaHome = true,
            homePoints = 65,
            awayPoints = 71,
            isWin = false,
            efgPct = 41.1,
            fg2Pct = 43.6,
            fg3Pct = 25.0,
            ftPct = 61.1,
            totalRebounds = 24,
            offensiveRebounds = 7,
            defensiveRebounds = 17,
            ast = 12,
            to = 15,
            astTo = 0.80,
            stl = 6,
            blk = 2,
            personalFouls = 19,
            secondChancePoints = 8,
            opponentSecondChancePoints = 11,
            fastBreakPoints = 9,
            opponentFastBreakPoints = 13
        ),
        MatchRowUiModel(
            roundNumber = 6,
            homeTeam = "CB Sant Just",
            awayTeam = "CEB Escola Pia Sabadell",
            isPiaHome = false,
            homePoints = 81,
            awayPoints = 64,
            isWin = false,
            efgPct = 39.0,
            fg2Pct = 41.0,
            fg3Pct = 25.0,
            ftPct = 47.1,
            totalRebounds = 22,
            offensiveRebounds = 7,
            defensiveRebounds = 15,
            ast = 10,
            to = 18,
            astTo = 0.56,
            stl = 4,
            blk = 1,
            personalFouls = 23,
            secondChancePoints = 6,
            opponentSecondChancePoints = 15,
            fastBreakPoints = 7,
            opponentFastBreakPoints = 20
        ),
        MatchRowUiModel(
            roundNumber = 7,
            homeTeam = "CEB Escola Pia Sabadell",
            awayTeam = "Club Natació Sabadell A",
            isPiaHome = true,
            homePoints = 63,
            awayPoints = 70,
            isWin = false,
            efgPct = 39.2,
            fg2Pct = 41.5,
            fg3Pct = 23.8,
            ftPct = 50.0,
            totalRebounds = 22,
            offensiveRebounds = 5,
            defensiveRebounds = 17,
            ast = 9,
            to = 18,
            astTo = 0.50,
            stl = 5,
            blk = 1,
            personalFouls = 22,
            secondChancePoints = 5,
            opponentSecondChancePoints = 12,
            fastBreakPoints = 7,
            opponentFastBreakPoints = 14
        ),
        MatchRowUiModel(
            roundNumber = 8,
            homeTeam = "CB Cornellà",
            awayTeam = "CEB Escola Pia Sabadell",
            isPiaHome = false,
            homePoints = 80,
            awayPoints = 66,
            isWin = false,
            efgPct = 39.5,
            fg2Pct = 42.5,
            fg3Pct = 23.5,
            ftPct = 53.8,
            totalRebounds = 21,
            offensiveRebounds = 6,
            defensiveRebounds = 15,
            ast = 10,
            to = 16,
            astTo = 0.63,
            stl = 4,
            blk = 1,
            personalFouls = 22,
            secondChancePoints = 5,
            opponentSecondChancePoints = 16,
            fastBreakPoints = 6,
            opponentFastBreakPoints = 17
        ),
        MatchRowUiModel(
            roundNumber = 9,
            homeTeam = "CEB Escola Pia Sabadell",
            awayTeam = "CB Cardedeu",
            isPiaHome = true,
            homePoints = 68,
            awayPoints = 65,
            isWin = true,
            efgPct = 47.1,
            fg2Pct = 50.0,
            fg3Pct = 31.3,
            ftPct = 68.8,
            totalRebounds = 27,
            offensiveRebounds = 8,
            defensiveRebounds = 19,
            ast = 14,
            to = 13,
            astTo = 1.08,
            stl = 7,
            blk = 2,
            personalFouls = 18,
            secondChancePoints = 8,
            opponentSecondChancePoints = 9,
            fastBreakPoints = 10,
            opponentFastBreakPoints = 8
        ),
        MatchRowUiModel(
            roundNumber = 10,
            homeTeam = "CEB Sant Jordi Blau",
            awayTeam = "CEB Escola Pia Sabadell",
            isPiaHome = false,
            homePoints = 65,
            awayPoints = 67,
            isWin = true,
            efgPct = 45.5,
            fg2Pct = 48.7,
            fg3Pct = 31.3,
            ftPct = 69.2,
            totalRebounds = 27,
            offensiveRebounds = 8,
            defensiveRebounds = 19,
            ast = 13,
            to = 13,
            astTo = 1.00,
            stl = 7,
            blk = 2,
            personalFouls = 17,
            secondChancePoints = 8,
            opponentSecondChancePoints = 9,
            fastBreakPoints = 11,
            opponentFastBreakPoints = 10
        ),
        MatchRowUiModel(
            roundNumber = 11,
            homeTeam = "CEB Escola Pia Sabadell",
            awayTeam = "CB Prat",
            isPiaHome = true,
            homePoints = 58,
            awayPoints = 72,
            isWin = false,
            efgPct = 33.9,
            fg2Pct = 37.8,
            fg3Pct = 18.8,
            ftPct = 45.5,
            totalRebounds = 19,
            offensiveRebounds = 4,
            defensiveRebounds = 15,
            ast = 7,
            to = 20,
            astTo = 0.35,
            stl = 3,
            blk = 0,
            personalFouls = 20,
            secondChancePoints = 3,
            opponentSecondChancePoints = 12,
            fastBreakPoints = 4,
            opponentFastBreakPoints = 19
        ),
        MatchRowUiModel(
            roundNumber = 12,
            homeTeam = "Bàsquet Sant Pere A",
            awayTeam = "CEB Escola Pia Sabadell",
            isPiaHome = false,
            homePoints = 74,
            awayPoints = 58,
            isWin = false,
            efgPct = 36.2,
            fg2Pct = 38.5,
            fg3Pct = 23.5,
            ftPct = 55.6,
            totalRebounds = 20,
            offensiveRebounds = 6,
            defensiveRebounds = 14,
            ast = 9,
            to = 21,
            astTo = 0.43,
            stl = 5,
            blk = 0,
            personalFouls = 24,
            secondChancePoints = 5,
            opponentSecondChancePoints = 12,
            fastBreakPoints = 4,
            opponentFastBreakPoints = 16
        ),
        MatchRowUiModel(
            roundNumber = 13,
            homeTeam = "CEB Escola Pia Sabadell",
            awayTeam = "CB Sant Just",
            isPiaHome = true,
            homePoints = 61,
            awayPoints = 75,
            isWin = false,
            efgPct = 36.8,
            fg2Pct = 40.0,
            fg3Pct = 21.1,
            ftPct = 42.9,
            totalRebounds = 20,
            offensiveRebounds = 5,
            defensiveRebounds = 15,
            ast = 8,
            to = 19,
            astTo = 0.42,
            stl = 4,
            blk = 0,
            personalFouls = 23,
            secondChancePoints = 4,
            opponentSecondChancePoints = 13,
            fastBreakPoints = 5,
            opponentFastBreakPoints = 17
        ),
        MatchRowUiModel(
            roundNumber = 14,
            homeTeam = "Club Natació Sabadell A",
            awayTeam = "CEB Escola Pia Sabadell",
            isPiaHome = false,
            homePoints = 76,
            awayPoints = 69,
            isWin = false,
            efgPct = 41.5,
            fg2Pct = 44.7,
            fg3Pct = 26.7,
            ftPct = 58.8,
            totalRebounds = 23,
            offensiveRebounds = 7,
            defensiveRebounds = 16,
            ast = 11,
            to = 17,
            astTo = 0.65,
            stl = 5,
            blk = 1,
            personalFouls = 21,
            secondChancePoints = 7,
            opponentSecondChancePoints = 13,
            fastBreakPoints = 8,
            opponentFastBreakPoints = 15
        )
    )

    /**
     * Root [DashboardUiState] aggregating all Phase 1 baseline summary, KPIs, chart, and table data.
     */
    val success = DashboardUiContent.Success(
        summary = summary,
        kpis = kpis,
        evolution = evolution,
        matches = matches
    )

    val uiState = DashboardUiState(
        content = success,
        isRefreshing = false
    )
}
