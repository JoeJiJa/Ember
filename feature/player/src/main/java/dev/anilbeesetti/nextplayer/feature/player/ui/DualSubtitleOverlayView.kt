package dev.anilbeesetti.nextplayer.feature.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BoxScope.DualSubtitleOverlayView(
    show: Boolean,
    primarySubtitleText: String,
    secondarySubtitleText: String,
) {
    if (!show) return

    Column(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 80.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (secondarySubtitleText.isNotBlank()) {
            SubtitleBubble(
                text = secondarySubtitleText,
                textColor = Color(0xFFFFD700.toInt()),
                backgroundColor = Color(0xAA000000.toInt()),
            )
        }
        if (primarySubtitleText.isNotBlank()) {
            SubtitleBubble(
                text = primarySubtitleText,
                textColor = Color.White,
                backgroundColor = Color(0xAA000000.toInt()),
            )
        }
    }
}

@Composable
private fun SubtitleBubble(
    text: String,
    textColor: Color,
    backgroundColor: Color,
) {
    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 22.sp,
        )
    }
}
