package com.martorell.albert.basketcoach.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.ui.theme.PiaDangerRed
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaOutline
import com.martorell.albert.basketcoach.ui.theme.PiaSurface
import com.martorell.albert.basketcoach.ui.theme.PiaSurfaceVariant
import com.martorell.albert.basketcoach.ui.theme.PiaTacticalOrange
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted

/**
 * Top row of 4 KPI overview cards for the BasketCoach dashboard.
 *
 * Renders:
 * 1. Anotació Mitjana (64.6 ppg vs 73.7 rival, -9.1 net) with visual bar.
 * 2. Rebots & Balanç (22.8 reb/p vs 30.6, -7.8) with offensive/defensive split.
 * 3. Eficiència Tir (eFG% 43.8%) with 2P, 3P, TL split.
 * 4. Control de Joc (0.8 AST/TO) with AST vs TO breakdown.
 */
@Composable
fun KpiCardRow(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("KpiCardRow"),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Scoring Card
        KpiCard(
            modifier = Modifier
                .weight(1f)
                .testTag("KpiCard_SCORING")
        ) {
            Column {
                Text(
                    text = stringResource(R.string.kpi_scoring_title),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PiaTextMuted,
                        letterSpacing = 0.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "64.6",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = PiaNavy,
                            fontSize = 28.sp
                        )
                    )
                    Text(
                        text = " ppg",
                        modifier = Modifier.padding(bottom = 3.dp, start = 4.dp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PiaTextMuted
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.kpi_scoring_comparison),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PiaTextMuted,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = stringResource(R.string.kpi_scoring_delta),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PiaDangerRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                // Visual bar: 47% fill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(PiaSurfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.47f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(PiaNavy)
                    )
                }
            }
        }

        // 2. Rebounding Card
        KpiCard(
            modifier = Modifier
                .weight(1f)
                .testTag("KpiCard_REBOUNDING")
        ) {
            Column {
                Text(
                    text = stringResource(R.string.kpi_rebounding_title),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PiaTextMuted,
                        letterSpacing = 0.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "22.8",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = PiaNavy,
                            fontSize = 28.sp
                        )
                    )
                    Text(
                        text = " " + stringResource(R.string.kpi_rebounding_comparison),
                        modifier = Modifier.padding(bottom = 3.dp, start = 4.dp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PiaTextMuted
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.kpi_rebounding_delta),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = PiaTacticalOrange,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(PiaSurfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.kpi_reb_off_format, 11.2),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PiaNavy,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp
                        )
                    )
                    Text(
                        text = stringResource(R.string.kpi_reb_def_format, 22.8),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PiaNavy,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }

        // 3. Shooting Efficiency (eFG%) Card
        KpiCard(
            modifier = Modifier
                .weight(1f)
                .testTag("KpiCard_SHOOTING")
        ) {
            Column {
                Text(
                    text = stringResource(R.string.kpi_shooting_title),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PiaTextMuted,
                        letterSpacing = 0.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "43.8%",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = PiaNavy,
                            fontSize = 28.sp
                        )
                    )
                    Text(
                        text = " " + stringResource(R.string.kpi_shooting_comparison),
                        modifier = Modifier.padding(bottom = 3.dp, start = 4.dp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PiaTextMuted
                        )
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(PiaSurfaceVariant)
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = stringResource(R.string.kpi_shooting_2p_format, 48.0),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PiaNavy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                    Text(text = "·", color = PiaTextMuted, fontSize = 10.sp)
                    Text(
                        text = stringResource(R.string.kpi_shooting_3p_format, 26.4),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PiaTacticalOrange,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                    Text(text = "·", color = PiaTextMuted, fontSize = 10.sp)
                    Text(
                        text = stringResource(R.string.kpi_shooting_ft_format, 65.0),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PiaNavy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }

        // 4. Game Control (AST/TO) Card
        KpiCard(
            modifier = Modifier
                .weight(1f)
                .testTag("KpiCard_CONTROL")
        ) {
            Column {
                Text(
                    text = stringResource(R.string.kpi_control_title),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PiaTextMuted,
                        letterSpacing = 0.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "0.8",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = PiaNavy,
                            fontSize = 28.sp
                        )
                    )
                    Text(
                        text = " " + stringResource(R.string.kpi_control_comparison),
                        modifier = Modifier.padding(bottom = 3.dp, start = 4.dp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PiaTextMuted
                        )
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(PiaSurfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.kpi_control_ast_format, 13.1),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PiaNavy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                    Text(
                        text = stringResource(R.string.kpi_control_to_format, 17.4),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PiaTacticalOrange,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun KpiCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = PiaSurface),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(PiaOutline.copy(alpha = 0.5f))
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            content()
        }
    }
}
