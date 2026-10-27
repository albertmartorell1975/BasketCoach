package com.martorell.albert.basketcoach.ui.chat.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.PsychologyAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martorell.albert.basketcoach.ui.chat.data.ChatPreviewData
import com.martorell.albert.basketcoach.ui.chat.model.ChatTopicCardUiModel
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview
import com.martorell.albert.basketcoach.ui.theme.PiaDangerRed
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaNavyLight
import com.martorell.albert.basketcoach.ui.theme.PiaSuccessGreen
import com.martorell.albert.basketcoach.ui.theme.PiaSurface
import com.martorell.albert.basketcoach.ui.theme.PiaSurfaceContainer
import com.martorell.albert.basketcoach.ui.theme.PiaSurfaceContainerLow
import com.martorell.albert.basketcoach.ui.theme.PiaTacticalOrange
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted
import com.martorell.albert.basketcoach.ui.theme.PiaWhite

/**
 * Interactive card component representing a single tactical intervention area or open inquiry thread.
 *
 * @param model Presentation data for this topic ([ChatTopicCardUiModel]).
 * @param isActive Whether this topic card is currently selected.
 * @param onClick Callback triggered on click.
 * @param modifier Composable modifier for layout customization.
 */
@Composable
fun ChatTopicCard(
    model: ChatTopicCardUiModel,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isActive) PiaTacticalOrange else PiaSurfaceContainer
    val containerColor = if (isActive) PiaSurface else PiaSurfaceContainerLow

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (isActive) 1.5.dp else 1.dp, borderColor),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
                if (model.isPriority) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(PiaTacticalOrange)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = null,
                            tint = PiaWhite,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = model.priorityBadge,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PiaWhite
                        )
                    }
                } else if (model.isOpenInquiry) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PsychologyAlt,
                            contentDescription = null,
                            tint = PiaNavy,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = model.priorityBadge,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PiaNavy
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(PiaSurfaceContainer)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = model.priorityBadge,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PiaNavyLight
                        )
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = if (isActive) PiaTacticalOrange else PiaTextMuted,
                    modifier = Modifier.size(12.dp)
                )
            }

            Text(
                text = model.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = PiaNavy
            )

            Text(
                text = model.description,
                fontSize = 12.sp,
                color = PiaTextMuted,
                lineHeight = 16.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (model.isOpenInquiry) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(PiaSuccessGreen.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = model.statusPill,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = PiaSuccessGreen
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isActive) PiaTacticalOrange else PiaTextMuted)
                        )
                        Text(
                            text = model.statusPill,
                            fontSize = 12.sp,
                            color = if (isActive) PiaNavy else PiaTextMuted
                        )
                    }
                }

                if (model.metricPill.isNotBlank()) {
                    Text(
                        text = model.metricPill,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (model.metricPillIsNegative) PiaDangerRed else PiaNavy
                    )
                }
            }
        }
    }
}

@BcThemePreview
@Composable
private fun ChatTopicCardPreview() {
    ChatTopicCard(
        model = ChatPreviewData.defaultTopicList.first(),
        isActive = true,
        onClick = {}
    )
}
