package com.robco.pipboyradio.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robco.pipboyradio.data.model.Station
import com.robco.pipboyradio.ui.theme.AccentAmber
import com.robco.pipboyradio.ui.theme.AccentRed
import com.robco.pipboyradio.ui.theme.PhosphorPrimary
import com.robco.pipboyradio.ui.theme.PhosphorSecondary
import com.robco.pipboyradio.ui.theme.PipBoyTypography
import com.robco.pipboyradio.ui.theme.ScreenBackground
import com.robco.pipboyradio.ui.theme.SurfaceBorder
import com.robco.pipboyradio.ui.theme.SurfaceCard

@Composable
fun StatusPanel(
    statusSignal: String,
    currentStation: Station?,
    isPlaying: Boolean,
    isOverdrive: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "BlinkTransition")
    val blinkAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blinkAlpha"
    )

    val isAlert = statusSignal.contains("OVERDRIVE") || statusSignal.contains("ERR") || statusSignal.contains("ACQUIRING")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, if (isOverdrive) AccentRed else SurfaceBorder, RoundedCornerShape(8.dp))
            .background(SurfaceCard, RoundedCornerShape(8.dp))
            .padding(12.dp)
            .testTag("status_panel")
    ) {
        Text(
            text = "[ STATUS ]",
            style = PipBoyTypography.labelSmall.copy(
                fontSize = 11.sp,
                color = if (isOverdrive) AccentRed else PhosphorPrimary
            ),
            modifier = Modifier
                .padding(start = 8.dp)
                .background(ScreenBackground)
                .padding(horizontal = 4.dp)
                .align(Alignment.TopStart)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STATUS:",
                    style = PipBoyTypography.bodyMedium
                )
                Text(
                    text = statusSignal,
                    style = PipBoyTypography.titleMedium.copy(
                        color = if (isAlert) AccentRed else PhosphorPrimary
                    ),
                    modifier = Modifier
                        .alpha(if (isAlert) blinkAlpha else 1f)
                        .testTag("status_signal_value")
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TUNED:",
                    style = PipBoyTypography.bodyMedium
                )
                Text(
                    text = if (isPlaying) currentStation?.name ?: "---" else "---",
                    style = PipBoyTypography.titleMedium.copy(
                        color = PhosphorPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .testTag("current_station_name")
                )
            }

            if (isPlaying && currentStation != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RELAY:",
                        style = PipBoyTypography.bodyMedium
                    )
                    Text(
                        text = "[${currentStation.category.uppercase()}]",
                        style = PipBoyTypography.labelSmall.copy(
                            color = AccentAmber
                        )
                    )
                }
            }
        }
    }
}
