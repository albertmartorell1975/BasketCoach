package com.martorell.albert.basketcoach.navigation

import kotlinx.serialization.Serializable

/**
 * Root navigation destinations for the BasketCoach tablet application.
 */
sealed interface Destination {

    /**
     * Dashboard screen destination displaying Phase 1 match results, box scores,
     * tactical AI insights, and recommended practice drills.
     */
    @Serializable
    data object Dashboard : Destination

    /**
     * AI chat assistant screen destination for interactive tactical Q&A with Coach AI.
     */
    @Serializable
    data object Chat : Destination
}
