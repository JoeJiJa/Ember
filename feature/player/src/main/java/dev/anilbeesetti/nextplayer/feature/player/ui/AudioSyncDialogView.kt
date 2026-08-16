package dev.anilbeesetti.nextplayer.feature.player.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AudioSyncDialogView(
    show: Boolean,
    currentOffsetMs: Long,
    onOffsetChange: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    if (!show) return

    var offset by remember(currentOffsetMs) { mutableLongStateOf(currentOffsetMs) }

    OverlayView(
        show = show,
        title = "Audio / Video Sync Offset",
        onDismiss = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = if (offset == 0L) "Audio & Video In Sync" else "${if (offset > 0) "+" else ""}${offset} ms",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (offset == 0L) Color.White else MaterialTheme.colorScheme.primary,
            )

            Text(
                text = "Adjust audio timing to fix Bluetooth or soundbar latency.",
                fontSize = 13.sp,
                color = Color.Gray,
            )

            Slider(
                value = offset.toFloat(),
                onValueChange = {
                    offset = it.toLong()
                    onOffsetChange(offset)
                },
                valueRange = -2000f..2000f,
                steps = 79, // 50ms increments
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                OutlinedButton(onClick = {
                    offset = (offset - 50L).coerceAtLeast(-2000L)
                    onOffsetChange(offset)
                }) {
                    Text(text = "- 50 ms")
                }

                Button(onClick = {
                    offset = 0L
                    onOffsetChange(0L)
                }) {
                    Text(text = "Reset")
                }

                OutlinedButton(onClick = {
                    offset = (offset + 50L).coerceAtMost(2000L)
                    onOffsetChange(offset)
                }) {
                    Text(text = "+ 50 ms")
                }
            }
        }
    }
}
