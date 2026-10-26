package com.martorell.albert.basketcoach.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaOutline
import com.martorell.albert.basketcoach.ui.theme.PiaSuccessBadge
import com.martorell.albert.basketcoach.ui.theme.PiaSuccessContainer
import com.martorell.albert.basketcoach.ui.theme.PiaSuccessGreen
import com.martorell.albert.basketcoach.ui.theme.PiaSurface
import com.martorell.albert.basketcoach.ui.theme.PiaSurfaceVariant
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted

/**
 * Top navigation and brand bar for the BasketCoach tablet application.
 *
 * Displays the team branding, data consolidation indicator, and Head Coach profile badge.
 */
@Composable
fun BasketCoachTopBar(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(PiaSurface)
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                drawLine(
                    color = PiaOutline.copy(alpha = 0.5f),
                    start = Offset(0f, size.height - strokeWidth),
                    end = Offset(size.width, size.height - strokeWidth),
                    strokeWidth = strokeWidth
                )
            }
            .padding(horizontal = 24.dp)
            .testTag("BasketCoachTopBar"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Branding: Logo + BasketCoach + Team Category Chip
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ic_basketcoach_logo),
                contentDescription = stringResource(R.string.brand_name),
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .testTag("TopBar_Logo")
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = stringResource(R.string.brand_name),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PiaNavy,
                    letterSpacing = (-0.5).sp
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .background(PiaSurfaceVariant, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(R.string.team_category_subtitle),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = PiaNavy,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        // Center Indicator: Verified Consolidated Dataset Badge
        Box(
            modifier = Modifier
                .background(PiaSuccessContainer, RoundedCornerShape(20.dp))
                .border(1.dp, PiaSuccessBadge.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .testTag("TopBar_ConsolidatedBadge")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(PiaSuccessBadge, CircleShape)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.data_consolidated_badge),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = PiaSuccessGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        // Right Profile: Head Coach Jordi
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.testTag("TopBar_CoachProfile")
        ) {
            Image(
                painter = painterResource(R.drawable.ic_avatar_coach),
                contentDescription = stringResource(R.string.coach_name),
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, PiaNavy.copy(alpha = 0.2f), CircleShape)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = stringResource(R.string.coach_role_label),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = PiaTextMuted,
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = stringResource(R.string.coach_name),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PiaNavy
                    )
                )
            }
        }
    }
}
