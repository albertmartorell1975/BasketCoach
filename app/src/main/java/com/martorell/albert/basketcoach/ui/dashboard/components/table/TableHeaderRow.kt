package com.martorell.albert.basketcoach.ui.dashboard.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaSurfaceContainerLow
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted

/**
 * Header row for the Phase 1 matches statistical table rendering 16 column labels.
 */
@Composable
internal fun TableHeaderRow(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(PiaSurfaceContainerLow)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderCell(text = stringResource(R.string.table_col_round), width = TableColumnWidths.round, textAlign = TextAlign.Start)
        HeaderCell(text = stringResource(R.string.table_col_match), width = TableColumnWidths.match, textAlign = TextAlign.Start)
        HeaderCell(text = stringResource(R.string.table_col_score), width = TableColumnWidths.score, textAlign = TextAlign.Center)
        HeaderCell(text = stringResource(R.string.table_col_efg), width = TableColumnWidths.efg, textAlign = TextAlign.Center, isPrimary = true)
        HeaderCell(text = stringResource(R.string.table_col_2p), width = TableColumnWidths.fg2, textAlign = TextAlign.Center)
        HeaderCell(text = stringResource(R.string.table_col_3p), width = TableColumnWidths.fg3, textAlign = TextAlign.Center)
        HeaderCell(text = stringResource(R.string.table_col_ft), width = TableColumnWidths.ft, textAlign = TextAlign.Center)
        HeaderCell(text = stringResource(R.string.table_col_reb), width = TableColumnWidths.reb, textAlign = TextAlign.Center)
        HeaderCell(text = stringResource(R.string.table_col_ast), width = TableColumnWidths.ast, textAlign = TextAlign.Center)
        HeaderCell(text = stringResource(R.string.table_col_to), width = TableColumnWidths.to, textAlign = TextAlign.Center)
        HeaderCell(text = stringResource(R.string.table_col_ast_to), width = TableColumnWidths.astTo, textAlign = TextAlign.Center)
        HeaderCell(text = stringResource(R.string.table_col_stl), width = TableColumnWidths.stl, textAlign = TextAlign.Center)
        HeaderCell(text = stringResource(R.string.table_col_blk), width = TableColumnWidths.blk, textAlign = TextAlign.Center)
        HeaderCell(text = stringResource(R.string.table_col_pf), width = TableColumnWidths.pf, textAlign = TextAlign.Center)
        HeaderCell(text = stringResource(R.string.table_col_second_chance), width = TableColumnWidths.secondChance, textAlign = TextAlign.Center)
        HeaderCell(text = stringResource(R.string.table_col_fast_break), width = TableColumnWidths.fastBreak, textAlign = TextAlign.Center)
    }
}

/**
 * Standardized header text cell with typography and alignment formatting.
 *
 * @param text Header label string.
 * @param width Layout width matching the corresponding data cell.
 * @param textAlign Horizontal text alignment.
 * @param modifier Layout modifier applied to the text cell.
 * @param isPrimary Whether this metric represents a primary analytical pillar (e.g. eFG%).
 */
@Composable
internal fun HeaderCell(
    text: String,
    width: Dp,
    textAlign: TextAlign,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false
) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp,
        color = if (isPrimary) PiaNavy else PiaTextMuted,
        textAlign = textAlign,
        modifier = modifier.width(width)
    )
}
