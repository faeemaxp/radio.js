package com.robco.pipboyradio.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robco.pipboyradio.ui.theme.AccentRed
import com.robco.pipboyradio.ui.theme.PhosphorBright
import com.robco.pipboyradio.ui.theme.PhosphorPrimary
import com.robco.pipboyradio.ui.theme.PhosphorSecondary
import com.robco.pipboyradio.ui.theme.PipBoyTypography
import com.robco.pipboyradio.ui.theme.ScreenBackground
import com.robco.pipboyradio.ui.theme.SurfaceBorder
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun VisualizerCanvas(
    isPlaying: Boolean,
    isOverdrive: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "VisualizerTransition")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phaseAnimation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .border(1.dp, if (isOverdrive) AccentRed else SurfaceBorder, RoundedCornerShape(8.dp))
            .background(Color.Black, RoundedCornerShape(8.dp))
            .testTag("visualizer_box")
    ) {
        // Frame title tag
        Text(
            text = "[ VISUALIZER ]",
            style = PipBoyTypography.labelSmall.copy(
                fontSize = 11.sp,
                color = if (isOverdrive) AccentRed else PhosphorPrimary
            ),
            modifier = Modifier
                .padding(start = 12.dp, top = 4.dp)
                .background(ScreenBackground)
                .padding(horizontal = 4.dp)
                .align(Alignment.TopStart)
        )

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, bottom = 4.dp, start = 8.dp, end = 8.dp)
        ) {
            val width = size.width
            val height = size.height

            // Background subtle grid lines
            val gridStep = 24.dp.toPx()
            var currentY = gridStep
            while (currentY < height) {
                drawLine(
                    color = if (isOverdrive) Color(0x33FF4C4C) else Color(0x221FA85B),
                    start = Offset(0f, currentY),
                    end = Offset(width, currentY),
                    strokeWidth = 1f
                )
                currentY += gridStep
            }

            if (!isPlaying) {
                // Standby: Oscilloscope sine wave line
                val points = 60
                val stepX = width / points
                var lastX = 0f
                var lastY = height / 2f

                for (i in 0..points) {
                    val x = i * stepX
                    val y = height / 2f + sin(x * 0.05f + phase) * 6f
                    if (i > 0) {
                        drawLine(
                            color = if (isOverdrive) AccentRed else PhosphorSecondary,
                            start = Offset(lastX, lastY),
                            end = Offset(x, y),
                            strokeWidth = 2f
                        )
                    }
                    lastX = x
                    lastY = y
                }
            } else {
                // Real-time animated phosphor frequency spectrum bars
                val barCount = 32
                val spacing = 3.dp.toPx()
                val totalSpacing = spacing * (barCount - 1)
                val barWidth = (width - totalSpacing) / barCount

                for (i in 0 until barCount) {
                    val normI = i.toFloat() / barCount
                    val bass = sin(phase * 1.8f + normI * 2.5f) * 0.4f + 0.6f
                    val mid = cos(phase * 2.6f - normI * 4.5f) * 0.35f + 0.5f
                    val high = sin(phase * 4f + normI * 6f) * 0.25f + 0.3f
                    val raw = (bass * 0.45f + mid * 0.35f + high * 0.2f) * (1f - normI * 0.35f)
                    val barHeight = (raw * height * 0.85f).coerceIn(6f, height - 4f)

                    val x = i * (barWidth + spacing)
                    val y = height - barHeight

                    val barColor = if (isOverdrive) {
                        Color(0xFFFF4C4C).copy(alpha = 0.6f + (barHeight / height) * 0.4f)
                    } else {
                        PhosphorPrimary.copy(alpha = 0.55f + (barHeight / height) * 0.45f)
                    }

                    // Main bar
                    drawRect(
                        color = barColor,
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight)
                    )

                    // Phosphor cap highlight
                    drawRect(
                        color = if (isOverdrive) Color(0xFFFFCCCC) else PhosphorBright,
                        topLeft = Offset(x, (y - 3f).coerceAtLeast(0f)),
                        size = Size(barWidth, 2.5f)
                    )
                }
            }
        }
    }
}
