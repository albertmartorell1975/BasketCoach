package com.martorell.albert.basketcoach.ui.chat.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martorell.albert.basketcoach.ui.chat.data.ChatPreviewData
import com.martorell.albert.basketcoach.ui.chat.model.ChatMetaHeaderUiModel
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaSurfaceContainer
import com.martorell.albert.basketcoach.ui.theme.PiaTacticalOrange
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted

/**
 * Contextual meta header bar for the tactical reflection workspace.
 *
 * Displays a pulsating tactical orange indicator, the active workspace title, round count subtitle,
 * and pill badges representing the Phase 1 team balance and division.
 *
 * @param model Presentation model containing header labels and pills ([ChatMetaHeaderUiModel]).
 * @param modifier Composable modifier for layout adjustment.
 */
@Composable
fun ChatMetaHeader(
    model: ChatMetaHeaderUiModel,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .alpha(alpha)
                    .background(PiaTacticalOrange)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = model.spaceTag,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PiaNavy
                )
                Text(
                    text = "·",
                    fontSize = 14.sp,
                    color = PiaTextMuted
                )
                Text(
                    text = model.phaseSubtitle,
                    fontSize = 12.sp,
                    color = PiaTextMuted
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(PiaSurfaceContainer)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = model.balancePill,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = PiaNavy
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(PiaTacticalOrange.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = model.teamPill,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PiaTacticalOrange
                )
            }
        }
    }
}

@BcThemePreview
@Composable
private fun ChatMetaHeaderPreview() {
    ChatMetaHeader(model = ChatPreviewData.defaultMetaHeader)
}
