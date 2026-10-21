package com.martorell.albert.basketcoach.data.mapper

import com.martorell.albert.basketcoach.data.dto.GameDto
import com.martorell.albert.basketcoach.data.dto.ScoreDto
import com.martorell.albert.basketcoach.data.dto.TeamStatsDto
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Score
import com.martorell.albert.basketcoach.domain.model.TeamStats
import com.martorell.albert.basketcoach.domain.model.Venue

/**
 * Maps a raw [ScoreDto] to the immutable domain [Score] value object.
 *
 * @return A clean domain [Score] instance containing home and away points.
 */
fun ScoreDto.toDomain(): Score = Score(
    homePoints = homePoints,
    awayPoints = awayPoints
)

/**
 * Maps a raw [TeamStatsDto] to the domain [TeamStats] entity.
 *
 * In the raw JSON schema, team points are stored separately in the match final score rather than
 * inside the stats object. This mapper accepts the resolved [points] value to maintain complete
 * statistical integrity across all 16 metrics.
 *
 * @param points The points scored by the team (derived from [Score.homePoints] or [Score.awayPoints]).
 * @return A fully populated domain [TeamStats] instance.
 */
fun TeamStatsDto.toDomain(points: Int): TeamStats = TeamStats(
    ftPct = ftPct,
    fg2Pct = fg2Pct,
    fg3Pct = fg3Pct,
    efgPct = efgPct,
    offensiveRebounds = offensiveRebounds,
    defensiveRebounds = defensiveRebounds,
    totalRebounds = totalRebounds,
    turnovers = turnovers,
    assists = assists,
    astToRatio = astToRatio,
    steals = steals,
    blocks = blocks,
    secondChancePoints = secondChancePoints,
    fastBreakPoints = fastBreakPoints,
    personalFouls = personalFouls,
    points = points
)

/**
 * Maps a deserialized [GameDto] to the immutable domain [Game] aggregate root.
 *
 * Constructs nested domain objects:
 * - [Venue] from flattened city, arena, and address fields.
 * - [Score] from [GameDto.finalScore].
 * - [TeamStats] for home and away teams, injecting their respective point totals from the match score.
 *
 * @return A fully structured domain [Game] entity ready for use cases and analytical processing.
 */
fun GameDto.toDomain(): Game {
    val score = finalScore.toDomain()
    return Game(
        round = round,
        date = date,
        time = time,
        venue = Venue(
            city = city,
            arena = arena,
            address = address
        ),
        homeTeam = homeTeam,
        awayTeam = awayTeam,
        finalScore = score,
        homeStats = homeStats.toDomain(score.homePoints),
        awayStats = awayStats.toDomain(score.awayPoints)
    )
}
