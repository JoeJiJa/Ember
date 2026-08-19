package dev.anilbeesetti.nextplayer.feature.player.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BoxScope.AudioSyncDialogView(
    show: Boolean,
    currentOffsetMs: Long,
    onOffsetChanged: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    var sliderValue by remember(currentOffsetMs) { mutableStateOf(currentOffsetMs.toFloat()) }

    OverlayView(
        show = show,
        title = "Audio Sync Offset",
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Offset: ${sliderValue.toLong()} ms",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )

            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                onValueChangeFinished = { onOffsetChanged(sliderValue.toLong()) },
                valueRange = -500f..500f,
                steps = 99,
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                OutlinedButton(
                    onClick = {
                        sliderValue = 0f
                        onOffsetChanged(0L)
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = "Reset")
                }
                Button(
                    onClick = { onOffsetChanged(sliderValue.toLong()) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = "Apply")
                }
            }
        }
    }
}
