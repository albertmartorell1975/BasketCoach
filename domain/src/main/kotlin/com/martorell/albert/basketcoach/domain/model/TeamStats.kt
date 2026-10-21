package com.martorell.albert.basketcoach.domain.model

/**
 * Comprehensive team-level box score metrics for a single basketball game.
 *
 * Captures shooting percentages, rebounding breakdown, playmaking, turnovers, defensive actions,
 * scoring opportunities, and foul counts according to the Phase 1 dataset specification.
 *
 * @property ftPct Free throw percentage (0.0 - 100.0).
 * @property fg2Pct 2-point field goal percentage (0.0 - 100.0).
 * @property fg3Pct 3-point field goal percentage (0.0 - 100.0).
 * @property efgPct Effective field goal percentage (0.0 - 100.0), accounting for 3-point value.
 * @property offensiveRebounds Total offensive rebounds captured.
 * @property defensiveRebounds Total defensive rebounds secured.
 * @property totalRebounds Aggregate rebounds (offensive + defensive).
 * @property turnovers Total possessions lost via turnovers.
 * @property assists Total completed assists.
 * @property astToRatio Assist-to-turnover ratio (assists / turnovers).
 * @property steals Total defensive steals.
 * @property blocks Total defensive blocked shots.
 * @property secondChancePoints Points scored following offensive rebounds.
 * @property fastBreakPoints Points scored in transition / fast break situations.
 * @property personalFouls Total team personal fouls committed.
 * @property points Total points scored in the game (default is 0 when derived from box score total).
 */
data class TeamStats(
    val ftPct: Double,
    val fg2Pct: Double,
    val fg3Pct: Double,
    val efgPct: Double,
    val offensiveRebounds: Int,
    val defensiveRebounds: Int,
    val totalRebounds: Int,
    val turnovers: Int,
    val assists: Int,
    val astToRatio: Double,
    val steals: Int,
    val blocks: Int,
    val secondChancePoints: Int,
    val fastBreakPoints: Int,
    val personalFouls: Int,
    val points: Int = 0
)
