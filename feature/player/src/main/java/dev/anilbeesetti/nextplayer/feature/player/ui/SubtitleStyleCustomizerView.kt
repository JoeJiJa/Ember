package dev.anilbeesetti.nextplayer.feature.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.anilbeesetti.nextplayer.core.ui.R

@Composable
fun BoxScope.SubtitleStyleCustomizerView(
    modifier: Modifier = Modifier,
    show: Boolean,
    currentSize: Int,
    currentColorHex: String,
    currentBgColorHex: String,
    onSizeChange: (Int) -> Unit,
    onColorChange: (String) -> Unit,
    onBgColorChange: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var size by remember(currentSize) { mutableStateOf(currentSize.toFloat()) }
    val colorPresets = listOf("#FFFFFF", "#FFFF00", "#00FFFF", "#00FF00", "#FFC0CB")
    val bgPresets = listOf("#80000000", "#00000000", "#FF000000", "#800000FF")

    OverlayView(
        modifier = modifier,
        show = show,
        title = stringResource(R.string.subtitle_settings),
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Live Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Sample Subtitle Text",
                    fontSize = size.sp,
                    fontWeight = FontWeight.Bold,
                    color = parseHexColor(currentColorHex),
                    modifier = Modifier
                        .background(parseHexColor(currentBgColorHex), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }

            // Size Slider
            Text(text = "Font Size: ${size.toInt()} sp", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = size,
                onValueChange = {
                    size = it
                    onSizeChange(it.toInt())
                },
                valueRange = 12f..36f,
            )

            // Text Color Presets
            Text(text = "Text Color", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                colorPresets.forEach { hex ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(parseHexColor(hex))
                            .clickable { onColorChange(hex) },
                    )
                }
            }

            // Background Color Presets
            Text(text = "Background Color", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                bgPresets.forEach { hex ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(parseHexColor(hex))
                            .clickable { onBgColorChange(hex) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun parseHexColor(hex: String): Color {
    return try {
        val colorInt = android.graphics.Color.parseColor(hex)
        Color(colorInt)
    } catch (e: Exception) {
        Color.White
    }
}
