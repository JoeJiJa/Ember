package dev.anilbeesetti.nextplayer.feature.player.audio

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi

data class EqualizerPreset(
    val name: String,
    val bandGains: List<Short>, // Band gains in millibels (mB)
    val bassBoostStrength: Short = 0,
    val virtualizerStrength: Short = 0,
)

class AudioEqualizerManager {

    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null

    var isEnabled: Boolean = false
        private set

    var currentPresetName: String = "Normal"
        private set

    val presets = listOf(
        EqualizerPreset("Normal", listOf(0, 0, 0, 0, 0), 0, 0),
        EqualizerPreset("Bass Boost", listOf(600, 400, 0, 0, 0), 800, 200),
        EqualizerPreset("Treble Boost", listOf(0, 0, 200, 500, 700), 0, 100),
        EqualizerPreset("Vocal", listOf(-200, 300, 600, 400, -100), 100, 0),
        EqualizerPreset("Rock", listOf(500, 300, -100, 200, 500), 400, 300),
        EqualizerPreset("Pop", listOf(-100, 200, 500, 200, -100), 200, 200),
        EqualizerPreset("Classical", listOf(400, 300, 0, 200, 400), 0, 400),
        EqualizerPreset("Flat", listOf(0, 0, 0, 0, 0), 0, 0),
    )

    fun initialize(audioSessionId: Int) {
        if (audioSessionId == C.AUDIO_SESSION_ID_UNSET || audioSessionId <= 0) return
        try {
            release()
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = isEnabled
            }
            bassBoost = BassBoost(0, audioSessionId).apply {
                enabled = isEnabled
            }
            virtualizer = Virtualizer(0, audioSessionId).apply {
                enabled = isEnabled
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
        try {
            equalizer?.enabled = enabled
            bassBoost?.enabled = enabled
            virtualizer?.enabled = enabled
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun applyPreset(preset: EqualizerPreset) {
        currentPresetName = preset.name
        try {
            val eq = equalizer ?: return
            val numBands = eq.numberOfBands.toInt()
            for (i in 0 until minOf(numBands, preset.bandGains.size)) {
                eq.setBandLevel(i.toShort(), preset.bandGains[i])
            }
            bassBoost?.setStrength(preset.bassBoostStrength)
            virtualizer?.setStrength(preset.virtualizerStrength)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setBandLevel(band: Short, levelMb: Short) {
        currentPresetName = "Custom"
        try {
            equalizer?.setBandLevel(band, levelMb)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setBassBoost(strength: Short) {
        try {
            if (bassBoost?.supportedStrength == true) {
                bassBoost?.setStrength(strength)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setVirtualizer(strength: Short) {
        try {
            if (virtualizer?.strengthSupported == true) {
                virtualizer?.setStrength(strength)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        try {
            equalizer?.release()
            bassBoost?.release()
            virtualizer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            equalizer = null
            bassBoost = null
            virtualizer = null
        }
    }
}
