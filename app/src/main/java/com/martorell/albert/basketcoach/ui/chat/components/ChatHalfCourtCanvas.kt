package com.martorell.albert.basketcoach.ui.chat.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.martorell.albert.basketcoach.R
import com.martorell.albert.basketcoach.ui.theme.BcThemePreview
import com.martorell.albert.basketcoach.ui.theme.PiaNavy
import com.martorell.albert.basketcoach.ui.theme.PiaOutline
import com.martorell.albert.basketcoach.ui.theme.PiaTacticalOrange
import com.martorell.albert.basketcoach.ui.theme.PiaTextMuted
import com.martorell.albert.basketcoach.ui.theme.PiaWhite

/**
 * Custom Compose [Canvas] component rendering FIBA basketball half-court geometry and
 * empirical perimeter vs. interior shot/rebound heat clusters.
 *
 * Strictly enforces a minimum typography size of 12.sp on all rendered zone labels per Constraint #16.
 *
 * @param perimeterPct Total percentage of conceded opportunities originating from perimeter.
 * @param interiorPct Total percentage of conceded opportunities originating from interior/paint.
 * @param leftWingPct Left wing zone frequency percentage.
 * @param topOfKeyPct Top of the key zone frequency percentage.
 * @param rightWingPct Right wing zone frequency percentage.
 * @param paintPct Paint zone frequency percentage.
 * @param modifier Composable modifier for layout customization.
 */
@Composable
fun ChatHalfCourtCanvas(
    perimeterPct: String,
    interiorPct: String,
    leftWingPct: String,
    topOfKeyPct: String,
    rightWingPct: String,
    paintPct: String,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PiaWhite)
        ) {
            val w = size.width
            val h = size.height

            // Outer Court Border
            drawRect(
                color = PiaOutline,
                topLeft = Offset(4f, 4f),
                size = Size(w - 8f, h - 8f),
                style = Stroke(width = 2f)
            )

            // 3-point Line
            val arcRect = Rect(Offset(w * 0.12f, -h * 0.1f), Size(w * 0.76f, h * 1.55f))
            val threePointPath = Path().apply {
                moveTo(w * 0.12f, h)
                lineTo(w * 0.12f, h * 0.65f)
                arcTo(arcRect, 180f, -180f, false)
                lineTo(w * 0.88f, h)
            }
            drawPath(
                path = threePointPath,
                color = PiaOutline,
                style = Stroke(width = 2f)
            )

            // Restricted Area (The Key / Paint)
            val paintWidth = w * 0.32f
            val paintHeight = h * 0.52f
            val paintX = (w - paintWidth) / 2f
            val paintY = h - paintHeight
            drawRect(
                color = PiaOutline,
                topLeft = Offset(paintX, paintY),
                size = Size(paintWidth, paintHeight),
                style = Stroke(width = 2f)
            )

            // Free Throw Circle Top Half
            val ftCircleRadius = paintWidth / 2f
            val ftCenter = Offset(w / 2f, paintY)
            drawArc(
                color = PiaOutline,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(ftCenter.x - ftCircleRadius, ftCenter.y - ftCircleRadius),
                size = Size(ftCircleRadius * 2f, ftCircleRadius * 2f),
                style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f)))
            )

            // Backboard & Rim
            val backboardY = h * 0.90f
            drawLine(
                color = PiaNavy,
                start = Offset(w * 0.44f, backboardY),
                end = Offset(w * 0.56f, backboardY),
                strokeWidth = 4f
            )
            drawCircle(
                color = PiaTacticalOrange,
                radius = 7.dp.toPx(),
                center = Offset(w / 2f, backboardY - 6.dp.toPx()),
                style = Stroke(width = 2f)
            )

            // Heat Clusters
            // 1. Left Wing
            val leftWingCenter = Offset(w * 0.22f, h * 0.52f)
            drawCircle(
                color = PiaTacticalOrange.copy(alpha = 0.18f),
                radius = 24.dp.toPx(),
                center = leftWingCenter
            )
            drawCircle(
                color = PiaTacticalOrange,
                radius = 24.dp.toPx(),
                center = leftWingCenter,
                style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))
            )
            val leftWingText = textMeasurer.measure(
                text = leftWingPct,
                style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PiaTacticalOrange)
            )
            drawText(
                textLayoutResult = leftWingText,
                topLeft = Offset(leftWingCenter.x - leftWingText.size.width / 2, leftWingCenter.y - leftWingText.size.height / 2)
            )

            // 2. Top of Key
            val topKeyCenter = Offset(w * 0.5f, h * 0.32f)
            drawCircle(
                color = PiaTacticalOrange.copy(alpha = 0.22f),
                radius = 26.dp.toPx(),
                center = topKeyCenter
            )
            drawCircle(
                color = PiaTacticalOrange,
                radius = 26.dp.toPx(),
                center = topKeyCenter,
                style = Stroke(width = 1.5f)
            )
            val topKeyText = textMeasurer.measure(
                text = topOfKeyPct,
                style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PiaTacticalOrange)
            )
            drawText(
                textLayoutResult = topKeyText,
                topLeft = Offset(topKeyCenter.x - topKeyText.size.width / 2, topKeyCenter.y - topKeyText.size.height / 2)
            )

            // 3. Right Wing
            val rightWingCenter = Offset(w * 0.78f, h * 0.52f)
            drawCircle(
                color = PiaTacticalOrange.copy(alpha = 0.16f),
                radius = 22.dp.toPx(),
                center = rightWingCenter
            )
            drawCircle(
                color = PiaTacticalOrange,
                radius = 22.dp.toPx(),
                center = rightWingCenter,
                style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))
            )
            val rightWingText = textMeasurer.measure(
                text = rightWingPct,
                style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PiaTacticalOrange)
            )
            drawText(
                textLayoutResult = rightWingText,
                topLeft = Offset(rightWingCenter.x - rightWingText.size.width / 2, rightWingCenter.y - rightWingText.size.height / 2)
            )

            // 4. Paint Cluster
            val paintCenter = Offset(w * 0.5f, h * 0.72f)
            drawCircle(
                color = PiaNavy.copy(alpha = 0.15f),
                radius = 20.dp.toPx(),
                center = paintCenter
            )
            val paintClusterText = textMeasurer.measure(
                text = paintPct,
                style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PiaNavy)
            )
            drawText(
                textLayoutResult = paintClusterText,
                topLeft = Offset(paintCenter.x - paintClusterText.size.width / 2, paintCenter.y - paintClusterText.size.height / 2)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(PiaTacticalOrange)
                )
                Text(
                    text = stringResource(R.string.chat_court_perimeter_legend, perimeterPct),
                    fontSize = 12.sp,
                    color = PiaTextMuted
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(PiaNavy)
                )
                Text(
                    text = stringResource(R.string.chat_court_interior_legend, interiorPct),
                    fontSize = 12.sp,
                    color = PiaTextMuted
                )
            }
        }
    }
}

@BcThemePreview
@Composable
private fun ChatHalfCourtCanvasPreview() {
    ChatHalfCourtCanvas(
        perimeterPct = "61%",
        interiorPct = "39%",
        leftWingPct = "29%",
        topOfKeyPct = "32%",
        rightWingPct = "23%",
        paintPct = "16%"
    )
}
