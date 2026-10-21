package com.martorell.albert.basketcoach.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data transfer object representing the final match score in the raw JSON dataset.
 *
 * @property homePoints Total points scored by the home team.
 * @property awayPoints Total points scored by the away team.
 */
@Serializable
data class ScoreDto(
    @SerialName("home_points") val homePoints: Int,
    @SerialName("away_points") val awayPoints: Int
)

/**
 * Data transfer object representing box score statistics for a team in the raw JSON dataset.
 * Captures all 15 offensive and defensive team metrics.
 *
 * @property ftPct Free throw percentage (0.0 to 100.0).
 * @property fg2Pct 2-point field goal percentage (0.0 to 100.0).
 * @property fg3Pct 3-point field goal percentage (0.0 to 100.0).
 * @property efgPct Effective field goal percentage (0.0 to 100.0).
 * @property offensiveRebounds Total offensive rebounds secured.
 * @property defensiveRebounds Total defensive rebounds secured.
 * @property totalRebounds Combined team rebounds.
 * @property turnovers Total turnovers committed during the game.
 * @property assists Total assists recorded.
 * @property astToRatio Assist-to-turnover ratio.
 * @property steals Total steals credited.
 * @property blocks Total blocked shots credited.
 * @property secondChancePoints Points scored from second-chance possessions.
 * @property fastBreakPoints Points scored on transition / fast breaks.
 * @property personalFouls Total personal fouls committed.
 */
@Serializable
data class TeamStatsDto(
    @SerialName("ft_pct") val ftPct: Double,
    @SerialName("fg2_pct") val fg2Pct: Double,
    @SerialName("fg3_pct") val fg3Pct: Double,
    @SerialName("efg_pct") val efgPct: Double,
    @SerialName("offensive_rebounds") val offensiveRebounds: Int,
    @SerialName("defensive_rebounds") val defensiveRebounds: Int,
    @SerialName("total_rebounds") val totalRebounds: Int,
    @SerialName("turnovers") val turnovers: Int,
    @SerialName("assists") val assists: Int,
    @SerialName("ast_to_ratio") val astToRatio: Double,
    @SerialName("steals") val steals: Int,
    @SerialName("blocks") val blocks: Int,
    @SerialName("second_chance_points") val secondChancePoints: Int,
    @SerialName("fast_break_points") val fastBreakPoints: Int,
    @SerialName("personal_fouls") val personalFouls: Int
)

/**
 * Data transfer object representing an entire basketball game record in the raw Phase 1 JSON dataset.
 *
 * @property round The competition round number (1 to 14).
 * @property date Match date formatted as DD/MM/YYYY.
 * @property time Match start time formatted as HH:mm.
 * @property city City where the match took place.
 * @property arena Name of the pavilion or sports arena.
 * @property address Physical street address of the venue.
 * @property homeTeam Official name of the home team.
 * @property awayTeam Official name of the away team.
 * @property finalScore Final score breakdown for home and away teams.
 * @property homeStats Complete box score metrics for the home team.
 * @property awayStats Complete box score metrics for the away team.
 */
@Serializable
data class GameDto(
    @SerialName("round") val round: Int,
    @SerialName("date") val date: String,
    @SerialName("time") val time: String,
    @SerialName("city") val city: String,
    @SerialName("arena") val arena: String,
    @SerialName("address") val address: String,
    @SerialName("home_team") val homeTeam: String,
    @SerialName("away_team") val awayTeam: String,
    @SerialName("final_score") val finalScore: ScoreDto,
    @SerialName("home_stats") val homeStats: TeamStatsDto,
    @SerialName("away_stats") val awayStats: TeamStatsDto
)
