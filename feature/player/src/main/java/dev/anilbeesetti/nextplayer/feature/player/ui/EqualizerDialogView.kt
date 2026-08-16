package dev.anilbeesetti.nextplayer.feature.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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

import androidx.compose.foundation.layout.BoxScope

@Composable
fun BoxScope.EqualizerDialogView(
    show: Boolean,
    equalizerManager: AudioEqualizerManager,
    onDismiss: () -> Unit,
) {
    if (!show) return

    var isEqEnabled by remember { mutableStateOf(equalizerManager.isEnabled) }
    var selectedPresetName by remember { mutableStateOf(equalizerManager.currentPresetName) }
    var bassBoostVal by remember { mutableFloatStateOf(0f) }
    var virtualizerVal by remember { mutableFloatStateOf(0f) }

    val bandFrequencies = listOf("60 Hz", "230 Hz", "910 Hz", "3.6 kHz", "14 kHz")
    var bandLevels by remember { mutableStateOf(listOf(0f, 0f, 0f, 0f, 0f)) }

    OverlayView(
        show = show,
        title = "Graphic Equalizer",
        onDismiss = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Master Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Enable Equalizer",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                )
                Switch(
                    checked = isEqEnabled,
                    onCheckedChange = {
                        isEqEnabled = it
                        equalizerManager.setEnabled(it)
                    },
                )
            }

            // Presets Horizontal Selector
            Text(
                text = "Presets",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray,
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(equalizerManager.presets) { preset ->
                    val isSelected = preset.name == selectedPresetName
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF333333))
                            .clickable {
                                selectedPresetName = preset.name
                                equalizerManager.applyPreset(preset)
                                bandLevels = preset.bandGains.map { it.toFloat() / 100f }
                                bassBoostVal = preset.bassBoostStrength.toFloat() / 10f
                                virtualizerVal = preset.virtualizerStrength.toFloat() / 10f
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    ) {
                        Text(
                            text = preset.name,
                            color = if (isSelected) Color.Black else Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }

            // 5 Band Sliders
            Text(
                text = "Frequency Bands",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray,
            )
            bandFrequencies.forEachIndexed { index, freq ->
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = freq, color = Color.White, fontSize = 13.sp)
                        Text(
                            text = "${bandLevels.getOrElse(index) { 0f }.toInt()} dB",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 13.sp,
                        )
                    }
                    Slider(
                        value = bandLevels.getOrElse(index) { 0f },
                        onValueChange = { newValue ->
                            val updated = bandLevels.toMutableList()
                            updated[index] = newValue
                            bandLevels = updated
                            selectedPresetName = "Custom"
                            equalizerManager.setBandLevel(index.toShort(), (newValue * 100).toInt().toShort())
                        },
                        valueRange = -12f..12f,
                        enabled = isEqEnabled,
                    )
                }
            }

            // Bass Boost & Virtualizer
            Text(
                text = "Audio Effects",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray,
            )
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = "Bass Boost", color = Color.White, fontSize = 13.sp)
                    Text(text = "${bassBoostVal.toInt()}%", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                }
                Slider(
                    value = bassBoostVal,
                    onValueChange = {
                        bassBoostVal = it
                        equalizerManager.setBassBoost((it * 10).toInt().toShort())
                    },
                    valueRange = 0f..100f,
                    enabled = isEqEnabled,
                )
            }

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = "Spatializer / Virtualizer", color = Color.White, fontSize = 13.sp)
                    Text(text = "${virtualizerVal.toInt()}%", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                }
                Slider(
                    value = virtualizerVal,
                    onValueChange = {
                        virtualizerVal = it
                        equalizerManager.setVirtualizer((it * 10).toInt().toShort())
                    },
                    valueRange = 0f..100f,
                    enabled = isEqEnabled,
                )
            }
        }
    }
}
