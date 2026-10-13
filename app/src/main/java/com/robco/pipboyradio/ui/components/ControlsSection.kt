package com.robco.pipboyradio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robco.pipboyradio.ui.theme.AccentRed
import com.robco.pipboyradio.ui.theme.PhosphorBright
import com.robco.pipboyradio.ui.theme.PhosphorDim
import com.robco.pipboyradio.ui.theme.PhosphorPrimary
import com.robco.pipboyradio.ui.theme.PhosphorSecondary
import com.robco.pipboyradio.ui.theme.PipBoyTypography
import com.robco.pipboyradio.ui.theme.ScreenBackground
import com.robco.pipboyradio.ui.theme.SurfaceBorder
import com.robco.pipboyradio.ui.theme.SurfaceCard
import kotlin.math.roundToInt

@Composable
fun ControlsSection(
    isPlaying: Boolean,
    volume: Float,
    isOverdrive: Boolean,
    onTogglePower: () -> Unit,
    onPrevStation: () -> Unit,
    onNextStation: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, if (isOverdrive) AccentRed else SurfaceBorder, RoundedCornerShape(8.dp))
            .background(SurfaceCard, RoundedCornerShape(8.dp))
            .padding(12.dp)
            .testTag("controls_section")
    ) {
        Text(
            text = "[ CONTROLS ]",
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
                .padding(top = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Main Power Button
                Button(
                    onClick = onTogglePower,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPlaying) AccentRed else PhosphorPrimary,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(48.dp)
                        .testTag("power_btn")
                ) {
                    Text(
                        text = if (isPlaying) "TERMINATE" else "INITIALIZE",
                        style = PipBoyTypography.titleMedium.copy(
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                    )
                }

                // Prev Station Button
                OutlinedButton(
                    onClick = onPrevStation,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PhosphorPrimary
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("prev_station_btn")
                ) {
                    Text(
                        text = "◄ PREV",
                        style = PipBoyTypography.bodyMedium.copy(
                            color = PhosphorPrimary,
                            fontSize = 12.sp
                        )
                    )
                }

                // Next Station Button
                OutlinedButton(
                    onClick = onNextStation,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PhosphorPrimary
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("next_station_btn")
                ) {
                    Text(
                        text = "NEXT ►",
                        style = PipBoyTypography.bodyMedium.copy(
                            color = PhosphorPrimary,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            // Volume Control Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "VOL:",
                    style = PipBoyTypography.bodyMedium
                )

                Slider(
                    value = volume,
                    onValueChange = onVolumeChange,
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = PhosphorPrimary,
                        activeTrackColor = PhosphorPrimary,
                        inactiveTrackColor = PhosphorDim
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("volume_slider")
                )

                Text(
                    text = "${(volume * 100).roundToInt()}%",
                    style = PipBoyTypography.bodyMedium.copy(
                        color = PhosphorPrimary
                    ),
                    modifier = Modifier.width(42.dp)
                )
            }
        }
    }
}
