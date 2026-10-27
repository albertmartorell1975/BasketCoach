package com.martorell.albert.basketcoach.ui.chat.components.evidence

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ScatterPlot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.ui.chat.components.ChatHalfCourtCanvas
import com.martorell.albert.basketcoach.ui.chat.data.ChatPreviewData
import com.martorell.albert.basketcoach.ui.chat.model.ChatDiagnosticEvidenceUiModel
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaSurface
import com.martorell.albert.basketcoach.ui.theme.PiaTacticalOrange

/**
 * Container card for the basketball half-court spatial distribution visualizer in the left diagnostic evidence pane.
 *
 * @param model Diagnostic evidence model containing zone frequency percentages ([ChatDiagnosticEvidenceUiModel]).
 * @param modifier Composable modifier for layout customization.
 */
@Composable
fun EvidenceCourtCard(
    model: ChatDiagnosticEvidenceUiModel,
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ScatterPlot,
                        contentDescription = null,
                        tint = PiaNavy,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = stringResource(R.string.chat_court_origin_title),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PiaNavy
                    )
                }
                Text(
                    text = stringResource(R.string.chat_court_perimeter_badge),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PiaTacticalOrange
                )
            }

            ChatHalfCourtCanvas(
                perimeterPct = model.courtPerimeterPct,
                interiorPct = model.courtInteriorPct,
                leftWingPct = model.courtLeftWingPct,
                topOfKeyPct = model.courtTopOfKeyPct,
                rightWingPct = model.courtRightWingPct,
                paintPct = model.courtPaintPct
            )
        }
    }
}

@BcThemePreview
@Composable
private fun EvidenceCourtCardPreview() {
    EvidenceCourtCard(
        model = ChatPreviewData.defaultDiagnosticEvidence
    )
}
