package com.martorell.albert.basketcoach.navigation

import kotlinx.serialization.Serializable

/**
 * Root navigation destinations for the BasketCoach tablet application.
 */
sealed interface Destination {

    /**
     * Dashboard overview destination with KPI cards, scoring evolution bar chart,
     * and consolidated 14-match statistical table.
     */
    @Serializable
    data object Dashboard : Destination

    /**
     * Tactical analysis destination displaying the 3-column grid of strengths and improvement areas.
     */
    @Serializable
    data object Analysis : Destination

    /**
     * Socratic AI chat assistant screen destination for interactive tactical Q&A.
     *
     * @property initialPrompt Optional pre-populated prompt from "Explorar al Xat".
     */
    @Serializable
    data class Chat(val initialPrompt: String? = null) : Destination
}
