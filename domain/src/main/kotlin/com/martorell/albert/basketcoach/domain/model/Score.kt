package com.martorell.albert.basketcoach.domain.model

/**
 * Encapsulates the final point score of a basketball match between home and away teams.
 *
 * @property homePoints Total points scored by the home team.
 * @property awayPoints Total points scored by the away team.
 */
data class Score(
    val homePoints: Int,
    val awayPoints: Int
)
