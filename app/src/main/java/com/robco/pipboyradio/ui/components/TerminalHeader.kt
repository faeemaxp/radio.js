package com.robco.pipboyradio.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robco.pipboyradio.ui.theme.AccentAmber
import com.robco.pipboyradio.ui.theme.AccentRed
import com.robco.pipboyradio.ui.theme.PhosphorPrimary
import com.robco.pipboyradio.ui.theme.PhosphorSecondary
import com.robco.pipboyradio.ui.theme.PipBoyTypography
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TerminalHeader(
    isOverdrive: Boolean,
    onToggleOverdrive: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf("00:00:00") }

    LaunchedEffect(Unit) {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.US)
        while (true) {
            currentTime = sdf.format(Date())
            delay(1000)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("terminal_header")
    ) {
        Text(
            text = "----------------------------------------",
            style = PipBoyTypography.bodyMedium.copy(
                fontSize = 11.sp,
                color = PhosphorSecondary.copy(alpha = 0.5f)
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PIP-BOY 3000",
                style = PipBoyTypography.headlineMedium.copy(
                    color = if (isOverdrive) AccentRed else PhosphorPrimary
                )
            )

            Text(
                text = if (isOverdrive) "[ OVERDRIVE: ENGAGED ]" else "[ MODE: STANDARD ]",
                style = PipBoyTypography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = if (isOverdrive) AccentRed else PhosphorSecondary
                ),
                modifier = Modifier
                    .border(
                        1.dp,
                        if (isOverdrive) AccentRed else PhosphorSecondary,
                        RoundedCornerShape(4.dp)
                    )
                    .clickable { onToggleOverdrive() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("overdrive_toggle_btn")
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "> SIGNAL: ACTIVE",
                style = PipBoyTypography.labelSmall.copy(
                    color = AccentAmber
                )
            )

            Text(
                text = currentTime,
                style = PipBoyTypography.labelSmall.copy(
                    color = AccentAmber
                ),
                modifier = Modifier.testTag("clock_text")
            )
        }
    }
}
