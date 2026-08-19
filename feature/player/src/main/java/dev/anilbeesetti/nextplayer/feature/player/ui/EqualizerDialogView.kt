package dev.anilbeesetti.nextplayer.feature.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.anilbeesetti.nextplayer.feature.player.audio.AudioEqualizerManager
import dev.anilbeesetti.nextplayer.feature.player.audio.EqualizerPreset

@Composable
fun BoxScope.EqualizerDialogView(
    show: Boolean,
    equalizerManager: AudioEqualizerManager,
    onDismiss: () -> Unit,
) {
    var selectedPresetName by remember { mutableStateOf(equalizerManager.currentPresetName) }
    val bandCount = remember { equalizerManager.getNumberOfBands() }
    val bandRange = remember { equalizerManager.getBandLevelRange() }
    val minLevel = bandRange.getOrElse(0) { -1500 }.toFloat()
    val maxLevel = bandRange.getOrElse(1) { 1500 }.toFloat()
    val bandLevels = remember(selectedPresetName) {
        (0 until bandCount).map { band ->
            mutableStateOf(equalizerManager.getBandLevel(band).toFloat())
        }
    }

    OverlayView(
        show = show,
        title = "Graphic Equalizer",
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Presets row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(equalizerManager.presets) { preset: EqualizerPreset ->
                    val isSelected = selectedPresetName == preset.name
                    Text(
                        text = preset.name,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else Color.Gray,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else Color(0x33FFFFFF),
                            )
                            .clickable {
                                selectedPresetName = preset.name
                                equalizerManager.applyPreset(preset)
                                preset.bandGains.take(bandCount).forEachIndexed { i, gain ->
                                    bandLevels.getOrNull(i)?.value = gain.toFloat()
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
            }

            // Band sliders
            (0 until bandCount).forEach { band ->
                val freqRange = equalizerManager.getBandFreqRange(band)
                val freqLabel = when {
                    freqRange[0] < 100_000 -> "${freqRange[0] / 1000}Hz"
                    freqRange[0] < 1_000_000 -> "${freqRange[0] / 1000}Hz"
                    else -> "${freqRange[0] / 1_000_000}kHz"
                }
                val level = bandLevels.getOrNull(band) ?: return@forEach
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = freqLabel,
                        color = Color.Gray,
                        fontSize = 11.sp,
                        modifier = Modifier.weight(0.8f),
                    )
                    Slider(
                        value = level.value,
                        onValueChange = { v ->
                            level.value = v
                            equalizerManager.setBandLevel(band, v.toInt().toShort())
                        },
                        valueRange = minLevel..maxLevel,
                        modifier = Modifier.weight(3f),
                    )
                    Text(
                        text = "${(level.value / 100).toInt()}dB",
                        color = Color.White,
                        fontSize = 11.sp,
                        modifier = Modifier.weight(0.8f),
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
