package dev.anilbeesetti.nextplayer.feature.player.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BoxScope.ABRepeatView(
    show: Boolean,
    repeatStart: Long?,
    repeatEnd: Long?,
    currentPosition: Long,
    isLooping: Boolean,
    onSetA: () -> Unit,
    onSetB: () -> Unit,
    onClearLoop: () -> Unit,
    onDismiss: () -> Unit,
) {
    OverlayView(
        show = show,
        title = "A-B Repeat",
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Start (A)",
                        color = Color.Gray,
                        fontSize = 12.sp,
                    )
                    Text(
                        text = if (repeatStart != null) formatMs(repeatStart) else "--:--",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(
                        text = "End (B)",
                        color = Color.Gray,
                        fontSize = 12.sp,
                    )
                    Text(
                        text = if (repeatEnd != null) formatMs(repeatEnd) else "--:--",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(onClick = onSetA, modifier = Modifier.weight(1f)) {
                    Text(text = "Set A")
                }
                Button(onClick = onSetB, modifier = Modifier.weight(1f)) {
                    Text(text = "Set B")
                }
                OutlinedButton(
                    onClick = onClearLoop,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                ) {
                    Text(text = "Clear")
                }
            }

            if (isLooping) {
                Text(
                    text = "LOOPING",
                    color = Color.Green,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

private fun formatMs(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
