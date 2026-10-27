package com.martorell.albert.basketcoach.ui.chat.components.ai

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ContactSupport
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaOutline
import com.martorell.albert.basketcoach.ui.theme.PiaSurface
import com.martorell.albert.basketcoach.ui.theme.PiaTacticalOrange
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted

@Composable
fun AiCalloutCard(
    questionText: String,
    subText: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = PiaSurface),
        border = BorderStroke(1.dp, PiaOutline.copy(alpha = 0.5f)),
        modifier = modifier
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
                    text = questionText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PiaNavy
                )
            }
            if (subText.isNotBlank()) {
                Text(
                    text = subText,
                    fontSize = 12.sp,
                    color = PiaTextMuted
                )
            }
        }
    }
}
