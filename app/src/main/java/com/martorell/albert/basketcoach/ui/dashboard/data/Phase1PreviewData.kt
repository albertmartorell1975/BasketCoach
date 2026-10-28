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
            RoundEvolutionUiModel(1, "J1", 62, 78, false, ""),
            RoundEvolutionUiModel(2, "J2", 70, 79, false, ""),
            RoundEvolutionUiModel(3, "J3", 71, 68, true, "W 71-68"),
            RoundEvolutionUiModel(4, "J4", 62, 78, false, ""),
            RoundEvolutionUiModel(5, "J5", 65, 71, false, ""),
            RoundEvolutionUiModel(6, "J6", 64, 81, false, ""),
            RoundEvolutionUiModel(7, "J7", 63, 70, false, ""),
            RoundEvolutionUiModel(8, "J8", 66, 80, false, ""),
            RoundEvolutionUiModel(9, "J9", 68, 65, true, "W 68-65"),
            RoundEvolutionUiModel(10, "J10", 67, 65, true, "W 67-65"),
            RoundEvolutionUiModel(11, "J11", 58, 72, false, ""),
            RoundEvolutionUiModel(12, "J12", 58, 74, false, ""),
            RoundEvolutionUiModel(13, "J13", 61, 75, false, ""),
            RoundEvolutionUiModel(14, "J14", 69, 76, false, "")
        )
    )

    /**
     * Complete list of 14 Phase 1 matches with 16 statistical columns per match.
     */
    val matches = listOf(
        MatchRowUiModel(
            roundNumber = 1,
            roundLabel = "J1",
            homeTeam = "CEB Pia Sabadell",
            awayTeam = "CB Cornellà",
            isPiaHome = true,
            piaScore = 62,
            rivalScore = 78,
            isWin = false,
            scoreDisplay = "62 - 78",
            efg = "38.6%",
            fg2 = "42.1%",
            fg3 = "21.4%",
            ft = "52.4%",
            rebounds = "21 (5/16)",
            ast = 8,
            to = 19,
            astTo = "0.42",
            stl = 4,
            blk = 1,
            pf = "22 PF",
            secondChance = "4 pts vs 14",
            fastBreak = "6 pts vs 18"
        ),
        MatchRowUiModel(
            roundNumber = 2,
            roundLabel = "J2",
            homeTeam = "CB Cardedeu",
            awayTeam = "CEB Pia Sabadell",
            isPiaHome = false,
            piaScore = 70,
            rivalScore = 79,
            isWin = false,
            scoreDisplay = "79 - 70",
            efg = "42.3%",
            fg2 = "45.0%",
            fg3 = "28.6%",
            ft = "56.3%",
            rebounds = "23 (6/17)",
            ast = 11,
            to = 17,
            astTo = "0.65",
            stl = 5,
            blk = 1,
            pf = "21 PF",
            secondChance = "6 pts vs 12",
            fastBreak = "8 pts vs 14"
        ),
        MatchRowUiModel(
            roundNumber = 3,
            roundLabel = "J3",
            homeTeam = "CEB Pia Sabadell",
            awayTeam = "CEB Sant Jordi Blau",
            isPiaHome = true,
            piaScore = 71,
            rivalScore = 68,
            isWin = true,
            scoreDisplay = "71 - 68",
            efg = "48.2%",
            fg2 = "51.2%",
            fg3 = "33.3%",
            ft = "70.6%",
            rebounds = "29 (9/20)",
            ast = 15,
            to = 12,
            astTo = "1.25",
            stl = 8,
            blk = 3,
            pf = "17 PF",
            secondChance = "10 pts vs 8",
            fastBreak = "12 pts vs 9"
        ),
        MatchRowUiModel(
            roundNumber = 4,
            roundLabel = "J4",
            homeTeam = "CB Prat",
            awayTeam = "CEB Pia Sabadell",
            isPiaHome = false,
            piaScore = 62,
            rivalScore = 78,
            isWin = false,
            scoreDisplay = "78 - 62",
            efg = "37.5%",
            fg2 = "40.0%",
            fg3 = "22.2%",
            ft = "53.3%",
            rebounds = "21 (5/16)",
            ast = 8,
            to = 19,
            astTo = "0.42",
            stl = 4,
            blk = 0,
            pf = "23 PF",
            secondChance = "4 pts vs 15",
            fastBreak = "5 pts vs 18"
        ),
        MatchRowUiModel(
            roundNumber = 5,
            roundLabel = "J5",
            homeTeam = "CEB Pia Sabadell",
            awayTeam = "Bàsquet Sant Pere A",
            isPiaHome = true,
            piaScore = 65,
            rivalScore = 71,
            isWin = false,
            scoreDisplay = "65 - 71",
            efg = "41.1%",
            fg2 = "43.6%",
            fg3 = "25.0%",
            ft = "61.1%",
            rebounds = "24 (7/17)",
            ast = 12,
            to = 15,
            astTo = "0.80",
            stl = 6,
            blk = 2,
            pf = "19 PF",
            secondChance = "8 pts vs 11",
            fastBreak = "9 pts vs 13"
        ),
        MatchRowUiModel(
            roundNumber = 6,
            roundLabel = "J6",
            homeTeam = "CB Sant Just",
            awayTeam = "CEB Pia Sabadell",
            isPiaHome = false,
            piaScore = 64,
            rivalScore = 81,
            isWin = false,
            scoreDisplay = "81 - 64",
            efg = "39.0%",
            fg2 = "41.0%",
            fg3 = "25.0%",
            ft = "47.1%",
            rebounds = "22 (7/15)",
            ast = 10,
            to = 18,
            astTo = "0.56",
            stl = 4,
            blk = 1,
            pf = "23 PF",
            secondChance = "6 pts vs 15",
            fastBreak = "7 pts vs 20"
        ),
        MatchRowUiModel(
            roundNumber = 7,
            roundLabel = "J7",
            homeTeam = "CEB Pia Sabadell",
            awayTeam = "Club Natació Sabadell A",
            isPiaHome = true,
            piaScore = 63,
            rivalScore = 70,
            isWin = false,
            scoreDisplay = "63 - 70",
            efg = "39.2%",
            fg2 = "41.5%",
            fg3 = "23.8%",
            ft = "50.0%",
            rebounds = "22 (5/17)",
            ast = 9,
            to = 18,
            astTo = "0.50",
            stl = 5,
            blk = 1,
            pf = "22 PF",
            secondChance = "5 pts vs 12",
            fastBreak = "7 pts vs 14"
        ),
        MatchRowUiModel(
            roundNumber = 8,
            roundLabel = "J8",
            homeTeam = "CB Cornellà",
            awayTeam = "CEB Pia Sabadell",
            isPiaHome = false,
            piaScore = 66,
            rivalScore = 80,
            isWin = false,
            scoreDisplay = "80 - 66",
            efg = "39.5%",
            fg2 = "42.5%",
            fg3 = "23.5%",
            ft = "53.8%",
            rebounds = "21 (6/15)",
            ast = 10,
            to = 16,
            astTo = "0.63",
            stl = 4,
            blk = 1,
            pf = "22 PF",
            secondChance = "5 pts vs 16",
            fastBreak = "6 pts vs 17"
        ),
        MatchRowUiModel(
            roundNumber = 9,
            roundLabel = "J9",
            homeTeam = "CEB Pia Sabadell",
            awayTeam = "CB Cardedeu",
            isPiaHome = true,
            piaScore = 68,
            rivalScore = 65,
            isWin = true,
            scoreDisplay = "68 - 65",
            efg = "47.1%",
            fg2 = "50.0%",
            fg3 = "31.3%",
            ft = "68.8%",
            rebounds = "27 (8/19)",
            ast = 14,
            to = 13,
            astTo = "1.08",
            stl = 7,
            blk = 2,
            pf = "18 PF",
            secondChance = "8 pts vs 9",
            fastBreak = "10 pts vs 8"
        ),
        MatchRowUiModel(
            roundNumber = 10,
            roundLabel = "J10",
            homeTeam = "CEB Sant Jordi Blau",
            awayTeam = "CEB Pia Sabadell",
            isPiaHome = false,
            piaScore = 67,
            rivalScore = 65,
            isWin = true,
            scoreDisplay = "65 - 67",
            efg = "45.5%",
            fg2 = "48.7%",
            fg3 = "31.3%",
            ft = "69.2%",
            rebounds = "27 (8/19)",
            ast = 13,
            to = 13,
            astTo = "1.00",
            stl = 7,
            blk = 2,
            pf = "17 PF",
            secondChance = "8 pts vs 9",
            fastBreak = "11 pts vs 10"
        ),
        MatchRowUiModel(
            roundNumber = 11,
            roundLabel = "J11",
            homeTeam = "CEB Pia Sabadell",
            awayTeam = "CB Prat",
            isPiaHome = true,
            piaScore = 58,
            rivalScore = 72,
            isWin = false,
            scoreDisplay = "58 - 72",
            efg = "33.9%",
            fg2 = "37.8%",
            fg3 = "18.8%",
            ft = "45.5%",
            rebounds = "19 (4/15)",
            ast = 7,
            to = 20,
            astTo = "0.35",
            stl = 3,
            blk = 0,
            pf = "20 PF",
            secondChance = "3 pts vs 12",
            fastBreak = "4 pts vs 19"
        ),
        MatchRowUiModel(
            roundNumber = 12,
            roundLabel = "J12",
            homeTeam = "Bàsquet Sant Pere A",
            awayTeam = "CEB Pia Sabadell",
            isPiaHome = false,
            piaScore = 58,
            rivalScore = 74,
            isWin = false,
            scoreDisplay = "74 - 58",
            efg = "36.2%",
            fg2 = "38.5%",
            fg3 = "23.5%",
            ft = "55.6%",
            rebounds = "20 (6/14)",
            ast = 9,
            to = 21,
            astTo = "0.43",
            stl = 5,
            blk = 0,
            pf = "24 PF",
            secondChance = "5 pts vs 12",
            fastBreak = "4 pts vs 16"
        ),
        MatchRowUiModel(
            roundNumber = 13,
            roundLabel = "J13",
            homeTeam = "CEB Pia Sabadell",
            awayTeam = "CB Sant Just",
            isPiaHome = true,
            piaScore = 61,
            rivalScore = 75,
            isWin = false,
            scoreDisplay = "61 - 75",
            efg = "36.8%",
            fg2 = "40.0%",
            fg3 = "21.1%",
            ft = "42.9%",
            rebounds = "20 (5/15)",
            ast = 8,
            to = 19,
            astTo = "0.42",
            stl = 4,
            blk = 0,
            pf = "23 PF",
            secondChance = "4 pts vs 13",
            fastBreak = "5 pts vs 17"
        ),
        MatchRowUiModel(
            roundNumber = 14,
            roundLabel = "J14",
            homeTeam = "Club Natació Sabadell A",
            awayTeam = "CEB Pia Sabadell",
            isPiaHome = false,
            piaScore = 69,
            rivalScore = 76,
            isWin = false,
            scoreDisplay = "76 - 69",
            efg = "41.5%",
            fg2 = "44.7%",
            fg3 = "26.7%",
            ft = "58.8%",
            rebounds = "23 (7/16)",
            ast = 11,
            to = 17,
            astTo = "0.65",
            stl = 5,
            blk = 1,
            pf = "21 PF",
            secondChance = "7 pts vs 13",
            fastBreak = "8 pts vs 15"
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
