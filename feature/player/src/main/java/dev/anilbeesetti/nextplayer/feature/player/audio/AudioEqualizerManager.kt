package dev.anilbeesetti.nextplayer.feature.player.audio

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer

data class EqualizerPreset(
    val name: String,
    val bandGains: List<Short>,
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

    val presets: List<EqualizerPreset> = listOf(
        EqualizerPreset("Normal", listOf(0, 0, 0, 0, 0), 0, 0),
        EqualizerPreset("Bass Boost", listOf(400, 300, 0, 0, 0), 800, 0),
        EqualizerPreset("Treble Boost", listOf(0, 0, 0, 300, 600), 0, 0),
        EqualizerPreset("Vocal Clarity", listOf(-200, 0, 400, 200, -100), 0, 0),
        EqualizerPreset("Rock", listOf(400, 200, -200, 200, 400), 300, 200),
        EqualizerPreset("Pop", listOf(-100, 200, 400, 200, -100), 0, 0),
        EqualizerPreset("Jazz", listOf(200, 0, 200, 0, -200), 100, 100),
        EqualizerPreset("Classical", listOf(400, 200, -200, 200, 400), 0, 0),
    )

    fun attachToAudioSession(audioSessionId: Int) {
        try {
            release()
            equalizer = Equalizer(0, audioSessionId).apply { enabled = true }
            bassBoost = BassBoost(0, audioSessionId).apply { enabled = true }
            virtualizer = Virtualizer(0, audioSessionId).apply { enabled = true }
            isEnabled = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun applyPreset(preset: EqualizerPreset) {
        currentPresetName = preset.name
        val eq = equalizer ?: return
        try {
            val bandCount = eq.numberOfBands
            preset.bandGains.take(bandCount.toInt()).forEachIndexed { i, gain ->
                eq.setBandLevel(i.toShort(), gain)
            }
            bassBoost?.setStrength(preset.bassBoostStrength)
            virtualizer?.setStrength(preset.virtualizerStrength)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getBandLevel(band: Int): Short {
        return try { equalizer?.getBandLevel(band.toShort()) ?: 0 } catch (e: Exception) { 0 }
    }

    fun setBandLevel(band: Int, level: Short) {
        try { equalizer?.setBandLevel(band.toShort(), level) } catch (e: Exception) { e.printStackTrace() }
    }

    fun getBandFreqRange(band: Int): IntArray {
        return try { equalizer?.getBandFreqRange(band.toShort()) ?: intArrayOf(0, 0) } catch (e: Exception) { intArrayOf(0, 0) }
    }

    fun getNumberOfBands(): Int {
        return try { equalizer?.numberOfBands?.toInt() ?: 5 } catch (e: Exception) { 5 }
    }

    fun getBandLevelRange(): ShortArray {
        return try { equalizer?.bandLevelRange ?: shortArrayOf(-1500, 1500) } catch (e: Exception) { shortArrayOf(-1500, 1500) }
    }

    fun release() {
        try { equalizer?.release() } catch (e: Exception) { }
        try { bassBoost?.release() } catch (e: Exception) { }
        try { virtualizer?.release() } catch (e: Exception) { }
        equalizer = null
        bassBoost = null
        virtualizer = null
        isEnabled = false
    }
}
