package com.martorell.albert.basketcoach.ui.chat.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ContactSupport
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martorell.albert.basketcoach.ui.chat.data.ChatPreviewData
import com.martorell.albert.basketcoach.ui.chat.model.ChatDrillOptionUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatMessageUiModel
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaOutline
import com.martorell.albert.basketcoach.ui.theme.PiaSuccessGreen
import com.martorell.albert.basketcoach.ui.theme.PiaSurface
import com.martorell.albert.basketcoach.ui.theme.PiaSurfaceContainer
import com.martorell.albert.basketcoach.ui.theme.PiaSurfaceContainerLow
import com.martorell.albert.basketcoach.ui.theme.PiaTacticalOrange
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted
import com.martorell.albert.basketcoach.ui.theme.PiaWhite

@Composable
fun AiMessageBubble(
    model: ChatMessageUiModel,
    onDrillOptionSelect: (ChatDrillOptionUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PiaNavy),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "P",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PiaWhite
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(PiaTacticalOrange)
                    .align(Alignment.BottomEnd)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .padding(start = 8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = model.senderName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PiaNavy
                )
                Text(
                    text = model.timestamp,
                    fontSize = 12.sp,
                    color = PiaTextMuted
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(PiaSurfaceContainer)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = model.senderRole,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = PiaNavy
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 0.dp, topEnd = 12.dp, bottomEnd = 12.dp, bottomStart = 12.dp))
                    .background(PiaSurfaceContainerLow)
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = model.text,
                        fontSize = 14.sp,
                        color = PiaNavy,
                        lineHeight = 20.sp
                    )

                    if (model.hasCalloutQuestion) {
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = PiaSurface),
                            border = BorderStroke(1.dp, PiaOutline.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ContactSupport,
                                        contentDescription = null,
                                        tint = PiaTacticalOrange,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = model.calloutQuestionText,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PiaNavy
                                    )
                                }
                                if (model.calloutSubText.isNotBlank()) {
                                    Text(
                                        text = model.calloutSubText,
                                        fontSize = 12.sp,
                                        color = PiaTextMuted
                                    )
                                }
                            }
                        }
                    }

                    if (model.hasDrillOptions) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            model.drillOptions.forEach { drill ->
                                Card(
                                    onClick = { onDrillOptionSelect(drill) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = PiaSurface),
                                    border = BorderStroke(1.dp, PiaOutline.copy(alpha = 0.5f)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .semantics(mergeDescendants = true) { }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(PiaNavy)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = drill.optionTag,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = PiaWhite
                                                )
                                            }
                                            Text(
                                                text = drill.intensityTag,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (drill.isHighIntensity) PiaTacticalOrange else PiaSuccessGreen
                                            )
                                        }

                                        Text(
                                            text = drill.title,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PiaNavy
                                        )

                                        Text(
                                            text = drill.description,
                                            fontSize = 12.sp,
                                            color = PiaTextMuted,
                                            lineHeight = 16.sp
                                        )

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(PiaSurfaceContainer)
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    text = drill.actionButtonText,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = PiaNavy
                                                )
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                    contentDescription = null,
                                                    tint = PiaNavy,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (model.hasSummaryCard) {
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = PiaSurface),
                            border = BorderStroke(1.dp, PiaTacticalOrange.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = model.summaryCard.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PiaNavy,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(PiaTacticalOrange.copy(alpha = 0.12f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = model.summaryCard.badgeText,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PiaTacticalOrange
                                        )
                                    }
                                }

                                Text(
                                    text = model.summaryCard.description,
                                    fontSize = 12.sp,
                                    color = PiaTextMuted,
                                    lineHeight = 16.sp
                                )

                                HorizontalDivider(color = PiaSurfaceContainer)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Flag,
                                            contentDescription = null,
                                            tint = PiaTacticalOrange,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = model.summaryCard.objectiveText,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = PiaNavy
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.EventRepeat,
                                            contentDescription = null,
                                            tint = PiaNavy,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = model.summaryCard.microcycleText,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = PiaNavy
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@BcThemePreview
@Composable
private fun AiMessageBubblePreview() {
    AiMessageBubble(
        model = ChatPreviewData.initialMessages[2],
        onDrillOptionSelect = {}
    )
}
