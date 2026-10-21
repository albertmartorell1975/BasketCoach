package com.martorell.albert.basketcoach.domain.model

/**
 * Standard identifier for the coached team ("BASQUETPIASABADELL BLAU").
 *
 * Used across the domain layer to dynamically distinguish our team's performance,
 * home/away designation, and match outcomes.
 */
const val OUR_TEAM_NAME: String = "BASQUETPIASABADELL BLAU"

/**
 * Represents a completed basketball game in Phase 1, containing schedule metadata, venue,
 * score, and comprehensive box scores for both competing sides.
 *
 * Provides dynamic helper properties relative to [OUR_TEAM_NAME] to resolve our score,
 * opponent score, victory status, and active statistics regardless of home/away venue.
 *
 * @property round Competition round number (1 to 14).
 * @property date Match date formatted as ISO string (e.g. "2023-10-08").
 * @property time Match tip-off time formatted as string (e.g. "17:45").
 * @property venue Physical court and arena location.
 * @property homeTeam Name of the home team.
 * @property awayTeam Name of the visiting team.
 * @property finalScore Final score breakdown between home and away teams.
 * @property homeStats Box score statistics recorded for the home team.
 * @property awayStats Box score statistics recorded for the visiting team.
 */
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
    /**
     * True if [OUR_TEAM_NAME] played as the home team; false if playing away.
     */
    val isHomeGame: Boolean
        get() = homeTeam.equals(OUR_TEAM_NAME, ignoreCase = true)

    /**
     * Points scored by [OUR_TEAM_NAME].
     */
    val teamScore: Int
        get() = if (isHomeGame) finalScore.homePoints else finalScore.awayPoints

    /**
     * Points scored by the opposing team.
     */
    val opponentScore: Int
        get() = if (isHomeGame) finalScore.awayPoints else finalScore.homePoints

    /**
     * True if [OUR_TEAM_NAME] scored strictly more points than the opponent.
     */
    val isWin: Boolean
        get() = teamScore > opponentScore

    /**
     * Name of the opposing team faced in this game.
     */
    val opponentName: String
        get() = if (isHomeGame) awayTeam else homeTeam

    /**
     * Box score statistics recorded for [OUR_TEAM_NAME].
     */
    val teamStats: TeamStats
        get() = if (isHomeGame) homeStats else awayStats

    /**
     * Box score statistics recorded for the opposing team.
     */
    val opponentStats: TeamStats
        get() = if (isHomeGame) awayStats else homeStats

    /**
     * Point margin for [OUR_TEAM_NAME] ([teamScore] minus [opponentScore]).
     * Positive for a victory, negative for a loss.
     */
    val pointDifferential: Int
        get() = teamScore - opponentScore

    companion object {
        /**
         * Companion reference to the coached team's name identifier.
         */
        const val OUR_TEAM_NAME: String = com.martorell.albert.basketcoach.domain.model.OUR_TEAM_NAME
    }
}
