package com.martorell.albert.basketcoach.ui.chat.components

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martorell.albert.basketcoach.ui.chat.components.ai.AiCalloutCard
import com.martorell.albert.basketcoach.ui.chat.components.ai.AiDrillOptionCard
import com.martorell.albert.basketcoach.ui.chat.components.ai.AiSummaryCard
import com.martorell.albert.basketcoach.ui.chat.data.ChatPreviewData
import com.martorell.albert.basketcoach.ui.chat.model.ChatDrillOptionUiModel
import com.martorell.albert.basketcoach.ui.chat.model.ChatMessageUiModel
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
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
                        AiCalloutCard(
                            questionText = model.calloutQuestionText,
                            subText = model.calloutSubText
                        )
                    }

                    if (model.hasDrillOptions) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            model.drillOptions.forEach { drill ->
                                AiDrillOptionCard(
                                    drill = drill,
                                    onSelect = onDrillOptionSelect,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    if (model.hasSummaryCard) {
                        AiSummaryCard(
                            model = model.summaryCard
                        )
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
