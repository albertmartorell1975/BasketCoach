package com.martorell.albert.basketcoach.ui.dashboard.preview

import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Insight
import com.martorell.albert.basketcoach.domain.model.InsightCategory
import com.martorell.albert.basketcoach.domain.model.InsightMetric
import com.martorell.albert.basketcoach.domain.model.InsightType
import com.martorell.albert.basketcoach.domain.model.MetricSentiment
import com.martorell.albert.basketcoach.domain.model.OUR_TEAM_NAME
import com.martorell.albert.basketcoach.domain.model.Score
import com.martorell.albert.basketcoach.domain.model.TeamStats
import com.martorell.albert.basketcoach.domain.model.Venue
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardTab
import com.martorell.albert.basketcoach.ui.dashboard.model.DashboardUiState

/**
 * Preview fixture data providing verified Phase 1 matches and rich tactical insights
 * conforming to the tablet landscape design sketches.
 */
object DashboardPreviewData {

    private val sampleVenue = Venue(
        city = "Sabadell",
        arena = "Pavelló Escola Pia Sabadell",
        address = "Carrer de l'Escola Pia, 14"
    )

    private fun createStats(
        fg2Pct: Double = 48.0,
        fg3Pct: Double = 26.4,
        ftPct: Double = 65.0,
        efgPct: Double = 43.8,
        offReb: Int = 11,
        defReb: Int = 23,
        turnovers: Int = 17,
        assists: Int = 13,
        steals: Int = 8,
        blocks: Int = 2,
        fouls: Int = 19,
        secondChancePoints: Int = 8,
        fastBreakPoints: Int = 14
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
        astToRatio = if (turnovers > 0) (assists.toDouble() / turnovers * 10).toInt() / 10.0 else 0.0,
        steals = steals,
        blocks = blocks,
        secondChancePoints = secondChancePoints,
        fastBreakPoints = fastBreakPoints,
        personalFouls = fouls
    )

    val sampleGames: List<Game> = listOf(
        Game(1, "2023-09-30", "18:00", sampleVenue, OUR_TEAM_NAME, "CB Sant Josep", Score(58, 69), createStats(fg2Pct = 42.0, fg3Pct = 21.0, ftPct = 60.0, efgPct = 38.5, offReb = 9, defReb = 22, turnovers = 19, assists = 10, steals = 7, blocks = 2, fouls = 20, secondChancePoints = 6, fastBreakPoints = 10), createStats()),
        Game(2, "2023-10-07", "19:30", sampleVenue, "JAC Sants", OUR_TEAM_NAME, Score(74, 62), createStats(), createStats(fg2Pct = 44.0, fg3Pct = 23.0, ftPct = 68.0, efgPct = 40.2, offReb = 10, defReb = 21, turnovers = 17, assists = 11, steals = 6, blocks = 1, fouls = 18, secondChancePoints = 7, fastBreakPoints = 12)),
        Game(3, "2023-10-15", "12:00", sampleVenue, OUR_TEAM_NAME, "CB Granollers", Score(71, 68), createStats(fg2Pct = 52.0, fg3Pct = 38.0, ftPct = 72.0, efgPct = 49.5, offReb = 14, defReb = 29, turnovers = 11, assists = 18, steals = 10, blocks = 4, fouls = 17, secondChancePoints = 13, fastBreakPoints = 18), createStats()),
        Game(4, "2023-10-21", "17:45", sampleVenue, "CB Hospitalet", OUR_TEAM_NAME, Score(80, 59), createStats(), createStats(fg2Pct = 40.0, fg3Pct = 20.0, ftPct = 58.0, efgPct = 36.0, offReb = 8, defReb = 19, turnovers = 21, assists = 8, steals = 5, blocks = 2, fouls = 22, secondChancePoints = 5, fastBreakPoints = 8)),
        Game(5, "2023-10-28", "16:00", sampleVenue, OUR_TEAM_NAME, "UE Mataró", Score(63, 75), createStats(fg2Pct = 45.0, fg3Pct = 25.0, ftPct = 64.0, efgPct = 41.5, offReb = 11, defReb = 22, turnovers = 16, assists = 12, steals = 7, blocks = 2, fouls = 19, secondChancePoints = 8, fastBreakPoints = 11), createStats()),
        Game(6, "2023-11-04", "18:30", sampleVenue, "CB Vic", OUR_TEAM_NAME, Score(77, 65), createStats(), createStats(fg2Pct = 43.0, fg3Pct = 22.0, ftPct = 62.0, efgPct = 39.8, offReb = 9, defReb = 20, turnovers = 18, assists = 9, steals = 6, blocks = 1, fouls = 21, secondChancePoints = 7, fastBreakPoints = 10)),
        Game(7, "2023-11-12", "12:15", sampleVenue, OUR_TEAM_NAME, "Basket Almeda", Score(60, 72), createStats(fg2Pct = 41.0, fg3Pct = 24.0, ftPct = 66.0, efgPct = 39.0, offReb = 10, defReb = 21, turnovers = 15, assists = 11, steals = 8, blocks = 3, fouls = 18, secondChancePoints = 8, fastBreakPoints = 13), createStats()),
        Game(8, "2023-11-18", "19:00", sampleVenue, "CB Santfeliuenc", OUR_TEAM_NAME, Score(69, 56), createStats(), createStats(fg2Pct = 39.0, fg3Pct = 21.0, ftPct = 60.0, efgPct = 35.5, offReb = 8, defReb = 18, turnovers = 14, assists = 10, steals = 7, blocks = 2, fouls = 19, secondChancePoints = 6, fastBreakPoints = 9)),
        Game(9, "2023-11-25", "17:30", sampleVenue, "CB Santa Coloma", OUR_TEAM_NAME, Score(65, 68), createStats(), createStats(fg2Pct = 50.0, fg3Pct = 35.0, ftPct = 74.0, efgPct = 48.0, offReb = 15, defReb = 27, turnovers = 10, assists = 16, steals = 9, blocks = 3, fouls = 16, secondChancePoints = 14, fastBreakPoints = 16)),
        Game(10, "2023-12-02", "16:15", sampleVenue, "CB Cornellà", OUR_TEAM_NAME, Score(65, 67), createStats(), createStats(fg2Pct = 49.0, fg3Pct = 34.0, ftPct = 76.0, efgPct = 47.2, offReb = 13, defReb = 28, turnovers = 12, assists = 15, steals = 8, blocks = 4, fouls = 15, secondChancePoints = 11, fastBreakPoints = 15)),
        Game(11, "2023-12-16", "18:00", sampleVenue, OUR_TEAM_NAME, "CB Tarragona", Score(61, 74), createStats(fg2Pct = 42.0, fg3Pct = 23.0, ftPct = 61.0, efgPct = 38.0, offReb = 9, defReb = 20, turnovers = 20, assists = 8, steals = 6, blocks = 2, fouls = 23, secondChancePoints = 6, fastBreakPoints = 9), createStats()),
        Game(12, "2024-01-13", "19:15", sampleVenue, "SEGLE XXI", OUR_TEAM_NAME, Score(82, 60), createStats(), createStats(fg2Pct = 41.0, fg3Pct = 22.0, ftPct = 59.0, efgPct = 37.5, offReb = 8, defReb = 19, turnovers = 22, assists = 7, steals = 5, blocks = 1, fouls = 24, secondChancePoints = 5, fastBreakPoints = 8)),
        Game(13, "2024-01-20", "12:00", sampleVenue, OUR_TEAM_NAME, "Bàsquet Manresa", Score(66, 78), createStats(fg2Pct = 46.0, fg3Pct = 27.0, ftPct = 67.0, efgPct = 42.8, offReb = 11, defReb = 23, turnovers = 15, assists = 12, steals = 7, blocks = 2, fouls = 19, secondChancePoints = 9, fastBreakPoints = 12), createStats()),
        Game(14, "2024-01-27", "17:00", sampleVenue, "CB Girona", OUR_TEAM_NAME, Score(73, 58), createStats(), createStats(fg2Pct = 40.0, fg3Pct = 21.0, ftPct = 63.0, efgPct = 37.0, offReb = 9, defReb = 21, turnovers = 19, assists = 9, steals = 6, blocks = 2, fouls = 20, secondChancePoints = 7, fastBreakPoints = 11))
    )

    val sampleInsights: List<Insight> = listOf(
        Insight(
            id = "insight-f1",
            title = "Protecció de la Pintura i Rebot Defensiu",
            description = "Control sòlid del rebot defensiu limitant segones opcions del rival en els 3 partits guanyats.",
            category = InsightCategory.DEFENSE,
            relatedGameRounds = listOf(3, 9, 10),
            type = InsightType.STRENGTH,
            contextTag = "Jornades 1-14 · Consolidat",
            metric = InsightMetric(
                label = "Rebot Defensiu",
                value = "28.3 reb/p",
                comparison = "vs 23.5 rival",
                delta = "+4.8 diferencial",
                sentiment = MetricSentiment.POSITIVE
            ),
            footerNote = "Consolidat en 14 partits"
        ),
        Insight(
            id = "insight-f2",
            title = "Transició Ofensiva i Punts de Contraatac",
            description = "Efectivitat elevada en transició quan es recupera pilota en primera línia defensiva.",
            category = InsightCategory.OFFENSE,
            relatedGameRounds = listOf(3, 9, 10),
            type = InsightType.STRENGTH,
            contextTag = "Jornades 1-14 · Consolidat",
            metric = InsightMetric(
                label = "Punts Contraatac",
                value = "14.2 ppg",
                comparison = "eficiència 62%",
                delta = "+3.4 vs mitjana lliga",
                sentiment = MetricSentiment.POSITIVE
            ),
            footerNote = "Consolidat en 14 partits"
        ),
        Insight(
            id = "insight-m1",
            title = "Control de Pèrdues de Pilota",
            description = "Mitjana crítica de pèrdues no forçades que generen punts fàcils del rival en transició directa.",
            category = InsightCategory.GENERAL,
            relatedGameRounds = listOf(1, 4, 11, 12),
            type = InsightType.IMPROVEMENT,
            contextTag = "Pèrdues Crítiques",
            priority = "Prioritat 1",
            metric = InsightMetric(
                label = "Pèrdues per partit",
                value = "17.4 TO",
                comparison = "vs 12.1 rival",
                delta = "+5.3 diferencial",
                sentiment = MetricSentiment.NEGATIVE
            ),
            footerNote = "Diferencial directe en 8 derrotes ajustades",
            exploreChatPrompt = "Com podem reduir les pèrdues de pilota (17.4 TO/p) de cara a la Fase 2?"
        ),
        Insight(
            id = "insight-m2",
            title = "Percentatge de Tir Exterior (3P%)",
            description = "Baixa efectivitat en tirs de tres punts alliberats des de les cantonades i sortides de bloqueig.",
            category = InsightCategory.OFFENSE,
            relatedGameRounds = listOf(2, 5, 7, 8, 13),
            type = InsightType.IMPROVEMENT,
            contextTag = "Dèficit Crític",
            priority = "Prioritat 2",
            metric = InsightMetric(
                label = "Percentatge 3P%",
                value = "26.4%",
                comparison = "lliga 32.8%",
                delta = "-6.4% diferencial",
                sentiment = MetricSentiment.NEGATIVE
            ),
            footerNote = "Factor decisiu en 5 partits perduts per menys de 10 punts",
            exploreChatPrompt = "Com podem millorar el percentatge de 3 punts (26.4%) en situacions de tir alliberat?"
        ),
        Insight(
            id = "insight-m3",
            title = "Rendiment del Rebot Ofensiu Rival Concedit",
            description = "Permetem massa segones opcions en els finals de quart quan baixa la intensitat del bloqueig de rebot.",
            category = InsightCategory.DEFENSE,
            relatedGameRounds = listOf(4, 6, 11, 14),
            type = InsightType.IMPROVEMENT,
            contextTag = "Defensa Col·lectiva",
            priority = "Prioritat 3",
            metric = InsightMetric(
                label = "Rebot Ofensiu Rival",
                value = "13.6 reb/p",
                comparison = "concedits",
                delta = "+4.2 segones opcions",
                sentiment = MetricSentiment.NEGATIVE
            ),
            footerNote = "Incidència en darrers 5 minuts de partit",
            exploreChatPrompt = "Quins exercicis podem fer per assegurar el tancament de rebot defensiu?"
        )
    )

    val sampleUiState = DashboardUiState(
        games = sampleGames,
        insights = sampleInsights,
        selectedTab = DashboardTab.ALL,
        isLoading = false,
        errorMessage = null
    )
}
