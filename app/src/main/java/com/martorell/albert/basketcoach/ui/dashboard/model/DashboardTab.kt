package com.martorell.albert.basketcoach.ui.dashboard.model

/**
 * Filter tabs available on the right-hand pane of the dashboard.
 */
enum class DashboardTab {
    /** Displays both AI tactical insights and practice drills sequentially. */
    ALL,

    /** Filters right pane to display only AI tactical insights. */
    INSIGHTS,

    /** Filters right pane to display only recommended practice drills. */
    DRILLS
}
