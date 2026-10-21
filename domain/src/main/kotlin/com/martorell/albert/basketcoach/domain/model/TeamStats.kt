package com.martorell.albert.basketcoach.domain.model

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
