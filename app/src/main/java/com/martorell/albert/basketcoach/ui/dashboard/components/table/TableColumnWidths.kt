package com.martorell.albert.basketcoach.ui.dashboard.components.table

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Standardized column widths for the Phase 1 matches statistical table.
 *
 * Enforces a single source of truth (SSOT) across [TableHeaderRow] and [TableDataRow]
 * to prevent horizontal layout misalignment during table scrolling.
 */
internal object TableColumnWidths {
    val round: Dp = 55.dp
    val match: Dp = 160.dp
    val score: Dp = 90.dp
    val efg: Dp = 65.dp
    val fg2: Dp = 55.dp
    val fg3: Dp = 55.dp
    val ft: Dp = 55.dp
    val reb: Dp = 90.dp
    val ast: Dp = 45.dp
    val to: Dp = 45.dp
    val astTo: Dp = 60.dp
    val stl: Dp = 45.dp
    val blk: Dp = 45.dp
    val pf: Dp = 60.dp
    val secondChance: Dp = 105.dp
    val fastBreak: Dp = 105.dp
}
