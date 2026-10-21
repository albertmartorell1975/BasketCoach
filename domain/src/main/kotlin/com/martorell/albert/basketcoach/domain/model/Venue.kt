package com.martorell.albert.basketcoach.domain.model

/**
 * Represents the physical location and facility where a basketball match takes place.
 *
 * @property city Name of the municipality or city hosting the game.
 * @property arena Name of the sports pavilion or arena.
 * @property address Physical street or postal address of the facility.
 */
data class Venue(
    val city: String,
    val arena: String,
    val address: String
)
