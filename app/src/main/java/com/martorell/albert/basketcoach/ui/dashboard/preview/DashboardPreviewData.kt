package com.martorell.albert.basketcoach.ui.dashboard.preview

import com.martorell.albert.basketcoach.domain.model.Drill
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.InsightCategory
import com.martorell.albert.basketcoach.domain.model.OUR_TEAM_NAME
import com.martorell.albert.basketcoach.domain.model.Score
import com.martorell.albert.basketcoach.domain.model.TeamStats
import com.martorell.albert.basketcoach.domain.model.Venue
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardTab
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardUiState

object DashboardPreviewData {

    private val sampleVenue = Venue(
        city = "Sabadell",
        arena = "Pavelló Escola Pia Sabadell",
        address = "Carrer de l'Escola Pia, 14"
    )

    private fun createStats(
        fg2Pct: Double = 48.0,
        fg3Pct: Double = 27.5,
        ftPct: Double = 65.0,
        efgPct: Double = 45.0,
        offReb: Int = 11,
        defReb: Int = 23,
        turnovers: Int = 14,
        assists: Int = 15,
        steals: Int = 8,
        fouls: Int = 18
    ) = TeamStats(
        ftPct = ftPct,
        fg2Pct = fg2Pct,
        fg3Pct = fg3Pct,
        efgPct = efgPct,
        offensiveRebounds = offReb,
        defensiveRebounds = defReb,
        totalRebounds = offReb + defReb,
        turnovers = turnovers,
        assists = assists,
        astToRatio = if (turnovers > 0) assists.toDouble() / turnovers else 0.0,
        steals = steals,
        blocks = 3,
        secondChancePoints = 9,
        fastBreakPoints = 12,
        personalFouls = fouls
    )

    val sampleGames: List<Game> = listOf(
        Game(1, "2023-09-30", "18:00", sampleVenue, OUR_TEAM_NAME, "CB Sant Josep", Score(58, 69), createStats(turnovers = 19, assists = 10), createStats()),
        Game(2, "2023-10-07", "19:30", sampleVenue, "JAC Sants", OUR_TEAM_NAME, Score(74, 62), createStats(), createStats(turnovers = 17, assists = 11)),
        Game(3, "2023-10-15", "12:00", sampleVenue, OUR_TEAM_NAME, "CB Granollers", Score(71, 68), createStats(turnovers = 11, assists = 18, fg3Pct = 38.0), createStats()),
        Game(4, "2023-10-21", "17:45", sampleVenue, "CB Hospitalet", OUR_TEAM_NAME, Score(80, 59), createStats(), createStats(turnovers = 21, assists = 8)),
        Game(5, "2023-10-28", "16:00", sampleVenue, OUR_TEAM_NAME, "UE Mataró", Score(63, 75), createStats(turnovers = 16, assists = 12), createStats()),
        Game(6, "2023-11-04", "18:30", sampleVenue, "CB Vic", OUR_TEAM_NAME, Score(77, 65), createStats(), createStats(turnovers = 18, assists = 9)),
        Game(7, "2023-11-12", "12:15", sampleVenue, OUR_TEAM_NAME, "Basket Almeda", Score(60, 72), createStats(turnovers = 15, assists = 11), createStats()),
        Game(8, "2023-11-18", "19:00", sampleVenue, "CB Santfeliuenc", OUR_TEAM_NAME, Score(69, 56), createStats(), createStats(turnovers = 14, assists = 10)),
        Game(9, "2023-11-25", "17:30", sampleVenue, "CB Santa Coloma", OUR_TEAM_NAME, Score(65, 68), createStats(), createStats(turnovers = 10, assists = 16, offReb = 15)),
        Game(10, "2023-12-02", "16:15", sampleVenue, "CB Cornellà", OUR_TEAM_NAME, Score(65, 67), createStats(), createStats(turnovers = 12, assists = 15, ftPct = 76.0)),
        Game(11, "2023-12-16", "18:00", sampleVenue, OUR_TEAM_NAME, "CB Tarragona", Score(61, 74), createStats(turnovers = 20, assists = 8), createStats()),
        Game(12, "2024-01-13", "19:15", sampleVenue, "SEGLE XXI", OUR_TEAM_NAME, Score(82, 60), createStats(), createStats(turnovers = 22, assists = 7)),
        Game(13, "2024-01-20", "12:00", sampleVenue, OUR_TEAM_NAME, "Bàsquet Manresa", Score(66, 78), createStats(turnovers = 15, assists = 12), createStats()),
        Game(14, "2024-01-27", "17:00", sampleVenue, "CB Girona", OUR_TEAM_NAME, Score(73, 58), createStats(), createStats(turnovers = 19, assists = 9))
    )

    val sampleInsights: List<Insight> = listOf(
        Insight(
            id = "ins-1",
            title = "Vulnerability in Defensive Transition & Turnovers",
            description = "Games with over 16 turnovers resulted in an average of 18.4 fast break points conceded. Limiting unforced ball losses directly protects our transition defense.",
            category = InsightCategory.DEFENSE,
            relatedGameRounds = listOf(1, 4, 11, 12)
        ),
        Insight(
            id = "ins-2",
            title = "Clutch 3-Point Percentage Directly Correlates with Victories",
            description = "In the 3 Phase 1 wins, team shot 36.8% from three-point range compared to 24.2% across the 11 losses. Open corner threes generated from inside-out kickouts are key.",
            category = InsightCategory.OFFENSE,
            relatedGameRounds = listOf(3, 9, 10)
        ),
        Insight(
            id = "ins-3",
            title = "Assist-to-Turnover Ratio Defines Team Ceiling",
            description = "When AST/TO ratio exceeds 1.25, Pia Sabadell achieved a 3-1 record. Maintaining quick ball reversals and avoiding forced baseline drives keeps possession efficiency high.",
            category = InsightCategory.SYNERGY,
            relatedGameRounds = listOf(3, 9, 10)
        ),
        Insight(
            id = "ins-4",
            title = "Defensive Rebounding Boxing-Out Consistency",
            description = "Allowing second-chance points in second halves accounted for a -9.2 average margin in Q3 and Q4 during close games.",
            category = InsightCategory.GENERAL,
            relatedGameRounds = listOf(5, 7, 13)
        )
    )

    val sampleDrills: List<Drill> = listOf(
        Drill(
            id = "drill-1",
            name = "3-on-2 to 2-on-1 Transition Sprint",
            objective = "Improve defensive recovery angles and accelerate recovery after turnovers.",
            instructions = listOf(
                "3 offensive players sprint downcourt against 2 tandem defenders.",
                "Offense gets maximum 2 passes and 7 seconds to execute a shot.",
                "Shooter and furthest offensive player sprint back immediately as defenders in 2-on-1 reverse transition.",
                "Play continues continuously for 5 minutes at high game pace."
            ),
            focusArea = "Defensive Transition & Rebounding"
        ),
        Drill(
            id = "drill-2",
            name = "Inside-Out Kickout Shooting Motion",
            objective = "Develop high-percentage catch-and-shoot threes from penetration kickouts.",
            instructions = listOf(
                "Point guard penetrates lane off pick-and-roll, attracting help.",
                "Corner and wing execute skip-pass coordination.",
                "Shooter catches in shooting pocket with balanced two-count footwork.",
                "Goal is 70% makes from wings and corners over 50 attempts."
            ),
            focusArea = "Shooting Mechanics & Spacing"
        ),
        Drill(
            id = "drill-3",
            name = "Pressure Passing & Turnover Elimination",
            objective = "Train ball handlers against aggressive full-court traps without turning the ball over.",
            instructions = listOf(
                "4-on-4 full court with trapped zones along sidelines and half-court corners.",
                "Offense cannot use dribble for the first 3 seconds of possession.",
                "Ball handler must use jump stops and aggressive pivots to find flash cuts.",
                "Turnover immediately awards point to defensive team."
            ),
            focusArea = "Ball Protection & Playmaking"
        )
    )

    val sampleUiState = DashboardUiState(
        games = sampleGames,
        selectedGame = sampleGames[2],
        insights = sampleInsights,
        drills = sampleDrills,
        selectedTab = DashboardTab.ALL,
        isLoading = false,
        errorMessage = null
    )
}
