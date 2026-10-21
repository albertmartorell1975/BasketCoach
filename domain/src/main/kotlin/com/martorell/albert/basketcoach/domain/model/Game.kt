package com.martorell.albert.basketcoach.domain.model

const val OUR_TEAM_NAME: String = "BASQUETPIASABADELL BLAU"

data class Game(
    val round: Int,
    val date: String,
    val time: String,
    val venue: Venue,
    val homeTeam: String,
    val awayTeam: String,
    val finalScore: Score,
    val homeStats: TeamStats,
    val awayStats: TeamStats
) {
    val isHomeGame: Boolean
        get() = homeTeam.equals(OUR_TEAM_NAME, ignoreCase = true)

    val teamScore: Int
        get() = if (isHomeGame) finalScore.homePoints else finalScore.awayPoints

    val opponentScore: Int
        get() = if (isHomeGame) finalScore.awayPoints else finalScore.homePoints

    val isWin: Boolean
        get() = teamScore > opponentScore

    val opponentName: String
        get() = if (isHomeGame) awayTeam else homeTeam

    val teamStats: TeamStats
        get() = if (isHomeGame) homeStats else awayStats

    val opponentStats: TeamStats
        get() = if (isHomeGame) awayStats else homeStats

    val pointDifferential: Int
        get() = teamScore - opponentScore

    companion object {
        const val OUR_TEAM_NAME: String = com.martorell.albert.basketcoach.domain.model.OUR_TEAM_NAME
    }
}
