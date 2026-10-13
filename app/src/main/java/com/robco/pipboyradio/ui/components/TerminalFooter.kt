package com.robco.pipboyradio.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robco.pipboyradio.ui.theme.PhosphorSecondary
import com.robco.pipboyradio.ui.theme.PipBoyTypography

@Composable
fun TerminalFooter(
    tipText: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = tipText,
        style = PipBoyTypography.bodyMedium.copy(
            fontSize = 11.sp,
            color = PhosphorSecondary.copy(alpha = 0.85f),
            textAlign = TextAlign.Center
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("terminal_footer_tip")
    )
}
