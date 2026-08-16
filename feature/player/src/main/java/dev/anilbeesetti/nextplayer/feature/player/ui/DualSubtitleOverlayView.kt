package dev.anilbeesetti.nextplayer.feature.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
    secondaryText: String?,
    modifier: Modifier = Modifier,
) {
    if (secondaryText.isNullOrEmpty()) return

    Box(
        modifier = modifier
            .align(Alignment.TopCenter)
            .padding(top = 48.dp, start = 16.dp, end = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = secondaryText,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFFFD700), // Dual subtitle secondary gold accent
            modifier = Modifier
                .background(Color(0xAA000000), RoundedCornerShape(4.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}
