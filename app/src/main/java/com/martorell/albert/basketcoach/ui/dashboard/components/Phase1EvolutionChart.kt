package com.martorell.albert.basketcoach.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaOutline
import com.martorell.albert.basketcoach.ui.theme.PiaSuccessContainer
import com.martorell.albert.basketcoach.ui.theme.PiaSuccessGreen
import com.martorell.albert.basketcoach.ui.theme.PiaSurface
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted
import com.martorell.albert.basketcoach.ui.theme.SlateBar

/**
 * 14-Round scoring evolution bar chart for Phase 1.
 *
 * Compares Escola Pia Sabadell points with Rival points per round,
 * visually emphasizing the 3 victories (J3, J9, J10) in green with floating score badges.
 *
 * @param games The 14 Phase 1 games to visualize.
 * @param modifier Modifier applied to the outer [Card].
 */
@Composable
fun Phase1EvolutionChart(
    games: List<Game>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("Phase1EvolutionChart"),
        colors = CardDefaults.cardColors(containerColor = PiaSurface),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(PiaOutline.copy(alpha = 0.5f))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header: Title, Subtitle, and Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.chart_title),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PiaNavy,
                            fontSize = 16.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.chart_subtitle),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PiaTextMuted,
                            fontSize = 12.sp
                        )
                    )
                }

                // Legend
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendItem(color = PiaSuccessGreen, label = stringResource(R.string.legend_pia_win))
                    LegendItem(color = PiaNavy, label = stringResource(R.string.legend_pia_loss))
                    LegendItem(color = SlateBar, label = stringResource(R.string.legend_rival))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bars Container
            val maxPoints = 90f // Base scale for bar normalization

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                games.forEach { game ->
                    val isWin = game.isWin
                    val piaPoints = game.teamScore
                    val rivalPoints = game.opponentScore
                    val piaBarHeight = (piaPoints / maxPoints).coerceIn(0.1f, 1f)
                    val rivalBarHeight = (rivalPoints / maxPoints).coerceIn(0.1f, 1f)
                    val piaColor = if (isWin) PiaSuccessGreen else PiaNavy

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        // Floating Win Badge if won
                        if (isWin) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(PiaSuccessContainer)
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                    .testTag("Chart_WinBadge_J${game.round}")
                            ) {
                                Text(
                                    text = "W $piaPoints-$rivalPoints",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PiaSuccessGreen
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        // Bar pair: Pia bar + Rival bar
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.height(120.dp)
                        ) {
                            // Pia Bar
                            Box(
                                modifier = Modifier
                                    .width(10.dp)
                                    .fillMaxHeight(piaBarHeight)
                                    .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                    .background(piaColor)
                            )
                            // Rival Bar
                            Box(
                                modifier = Modifier
                                    .width(10.dp)
                                    .fillMaxHeight(rivalBarHeight)
                                    .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                    .background(SlateBar)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Round Label
                        Text(
                            text = "J${game.round}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isWin) FontWeight.Bold else FontWeight.Normal,
                                color = if (isWin) PiaSuccessGreen else PiaTextMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = PiaTextMuted,
                fontSize = 11.sp
            )
        )
    }
}
