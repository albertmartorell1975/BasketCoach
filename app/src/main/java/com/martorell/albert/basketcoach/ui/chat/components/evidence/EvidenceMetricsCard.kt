package com.martorell.albert.basketcoach.ui.chat.components.evidence

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.ui.chat.data.ChatPreviewData
import com.martorell.albert.basketcoach.ui.chat.model.ChatDiagnosticEvidenceUiModel
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview
import com.martorell.albert.basketcoach.ui.theme.PiaDangerRed
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaSurface
import com.martorell.albert.basketcoach.ui.theme.PiaSurfaceContainerLow
import com.martorell.albert.basketcoach.ui.theme.PiaTacticalOrange
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted

@Composable
fun EvidenceMetricsCard(
    model: ChatDiagnosticEvidenceUiModel,
    onBackToTopics: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = PiaSurface),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(PiaTacticalOrange.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = model.priorityBadge,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PiaTacticalOrange
                    )
                }

                Text(
                    text = stringResource(R.string.chat_topics_back_toggle),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PiaNavy,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable(onClick = onBackToTopics)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = model.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PiaNavy
                )
                Text(
                    text = model.subtitle,
                    fontSize = 12.sp,
                    color = PiaTextMuted
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PiaSurfaceContainerLow)
                        .padding(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = model.metric1Label,
                            fontSize = 12.sp,
                            color = PiaTextMuted
                        )
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = model.metric1Value,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = PiaDangerRed
                            )
                            Text(
                                text = model.metric1Unit,
                                fontSize = 12.sp,
                                color = PiaTextMuted
                            )
                        }
                        Text(
                            text = model.metric1Delta,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PiaTacticalOrange
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PiaSurfaceContainerLow)
                        .padding(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = model.metric2Label,
                            fontSize = 12.sp,
                            color = PiaTextMuted
                        )
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = model.metric2Value,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = PiaNavy
                            )
                            Text(
                                text = model.metric2Unit,
                                fontSize = 12.sp,
                                color = PiaTextMuted
                            )
                        }
                        Text(
                            text = model.metric2Ranking,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PiaDangerRed
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(PiaNavy.copy(alpha = 0.08f))
                    .padding(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.QueryStats,
                        contentDescription = null,
                        tint = PiaNavy,
                        modifier = Modifier.size(18.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = model.zeroAssumptionTitle,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PiaNavy
                        )
                        Text(
                            text = model.zeroAssumptionDescription,
                            fontSize = 12.sp,
                            color = PiaTextMuted,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@BcThemePreview
@Composable
private fun EvidenceMetricsCardPreview() {
    EvidenceMetricsCard(
        model = ChatPreviewData.defaultDiagnosticEvidence,
        onBackToTopics = {}
    )
}
