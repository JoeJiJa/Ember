package dev.anilbeesetti.nextplayer.feature.videopicker.tools

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.net.Uri
import java.io.File
import java.nio.ByteBuffer

object AudioExtractorManager {

    fun extractAudio(context: Context, inputUri: Uri, outputFile: File, onProgress: (Float) -> Unit = {}): Boolean {
        var extractor: MediaExtractor? = null
        var muxer: MediaMuxer? = null
        return try {
            extractor = MediaExtractor()
            extractor.setDataSource(context, inputUri, null)

            var audioTrackIndex = -1
            var format: MediaFormat? = null

            for (i in 0 until extractor.trackCount) {
                val trackFormat = extractor.getTrackFormat(i)
                val mime = trackFormat.getString(MediaFormat.KEY_MIME) ?: continue
                if (mime.startsWith("audio/")) {
                    audioTrackIndex = i
                    format = trackFormat
                    break
                }
            }

            if (audioTrackIndex == -1 || format == null) return false

            extractor.selectTrack(audioTrackIndex)

            muxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            val muxerAudioTrack = muxer.addTrack(format)
            muxer.start()

            val buffer = ByteBuffer.allocate(1024 * 1024)
            val bufferInfo = android.media.MediaCodec.BufferInfo()

            var totalBytesRead = 0L
            val durationUs = format.getLong(MediaFormat.KEY_DURATION)

            while (true) {
                val sampleSize = extractor.readSampleData(buffer, 0)
                if (sampleSize < 0) break

                bufferInfo.offset = 0
                bufferInfo.size = sampleSize
                bufferInfo.presentationTimeUs = extractor.sampleTime
                bufferInfo.flags = extractor.sampleFlags

                muxer.writeSampleData(muxerAudioTrack, buffer, bufferInfo)
                extractor.advance()

                totalBytesRead += sampleSize
                if (durationUs > 0) {
                    onProgress((extractor.sampleTime.toFloat() / durationUs.toFloat()).coerceIn(0f, 1f))
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            try {
                muxer?.stop()
                muxer?.release()
                extractor?.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
