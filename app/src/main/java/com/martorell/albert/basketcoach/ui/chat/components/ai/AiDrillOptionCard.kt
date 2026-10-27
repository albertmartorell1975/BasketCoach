package com.martorell.albert.basketcoach.ui.chat.components.ai

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.martorell.albert.basketcoach.ui.chat.model.ChatDrillOptionUiModel
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaOutline
import com.martorell.albert.basketcoach.ui.theme.PiaSuccessGreen
import com.martorell.albert.basketcoach.ui.theme.PiaSurface
import com.martorell.albert.basketcoach.ui.theme.PiaSurfaceContainer
import com.martorell.albert.basketcoach.ui.theme.PiaTacticalOrange
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted
import com.martorell.albert.basketcoach.ui.theme.PiaWhite

@Composable
fun AiDrillOptionCard(
    drill: ChatDrillOptionUiModel,
    onSelect: (ChatDrillOptionUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onSelect(drill) },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = PiaSurface),
        border = BorderStroke(1.dp, PiaOutline.copy(alpha = 0.5f)),
        modifier = modifier.semantics(mergeDescendants = true) { }
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
