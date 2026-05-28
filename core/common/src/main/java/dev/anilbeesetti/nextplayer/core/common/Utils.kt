package dev.anilbeesetti.nextplayer.core.common

import android.Manifest
import android.content.res.Resources
import android.os.Build
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.abs

val storagePermission = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> Manifest.permission.READ_MEDIA_VIDEO
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> Manifest.permission.READ_EXTERNAL_STORAGE
    else -> Manifest.permission.WRITE_EXTERNAL_STORAGE
}

/**
 * Utility functions.
 */
object Utils {

    /**
     * Converts px to dp.
     */
    fun pxToDp(px: Float): Float {
        return px / Resources.getSystem().displayMetrics.density
    }

    /**
     * Formats the given duration in milliseconds to a string in the format of `mm:ss` or `hh:mm:ss`.
     */
    fun formatDurationMillis(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(hours)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) -
            TimeUnit.MINUTES.toSeconds(minutes) -
            TimeUnit.HOURS.toSeconds(hours)
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    /**
     * Formats the given duration in milliseconds to a string in the format of
     * `+mm:ss` or `+hh:mm:ss` or `-mm:ss` or `-hh:mm:ss`.
     */
    fun formatDurationMillisSign(millis: Long): String {
        return if (millis >= 0) {
            "+${formatDurationMillis(millis)}"
        } else {
            "-${formatDurationMillis(abs(millis))}"
        }
    }

    fun formatFileSize(size: Long): String {
        val kb = 1024
        val mb = kb * 1024
        val gb = mb * 1024

        return when {
            size < kb -> "$size B"
            size < mb -> "%.2f KB".format(size / kb.toDouble())
            size < gb -> "%.2f MB".format(size / mb.toDouble())
            else -> "%.2f GB".format(size / gb.toDouble())
        }
    }

    fun formatBitrate(bitrate: Long): String? {
        if (bitrate <= 0) {
            return null
        }

        val kiloBitrate = bitrate.toDouble() / 1000.0
        val megaBitrate = kiloBitrate / 1000.0
        val gigaBitrate = megaBitrate / 1000.0

        return when {
            gigaBitrate >= 1.0 -> String.format("%.1f Gbps", gigaBitrate)
            megaBitrate >= 1.0 -> String.format("%.1f Mbps", megaBitrate)
            kiloBitrate >= 1.0 -> String.format("%.1f kbps", kiloBitrate)
            else -> String.format("%d bps", bitrate)
        }
    }

    fun formatLanguage(language: String?): String? {
        return language?.let { lang -> Locale.forLanguageTag(lang).displayLanguage.takeIf { it.isNotEmpty() } }
    }

    fun sanitizeTitle(title: String): String {
        var clean = title
        
        // Regex for domain patterns (e.g. www.site.com, site.live, site.futbol, etc.)
        val domainRegex = "(?i)(www\\.)?[\\w-]+\\.(futbol|live|pm|cool|com|org|net|xyz|me|info|tv|in|co|ws|biz|cc|net)\\b".toRegex()
        clean = domainRegex.replace(clean, "")

        // Replace hyphens, dots, underscores, brackets with space
        clean = clean.replace(listOf(".", "-", "_", "[", "]", "(", ")"), " ")

        // Replace multiple spaces with a single space
        clean = clean.replace("\\s+".toRegex(), " ").trim()

        // Capitalize words for premium look
        clean = clean.split(" ").joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }

        return if (clean.isEmpty()) title else clean
    }

    fun sanitizeFileName(name: String): String {
        var clean = name
        // Strip file extension if present at the end
        val extIndex = clean.lastIndexOf('.')
        if (extIndex > clean.length - 6 && extIndex > 0) {
            clean = clean.substring(0, extIndex)
        }

        // Regex for domain patterns
        val domainRegex = "(?i)(www\\.)?[\\w-]+\\.(futbol|live|pm|cool|com|org|net|xyz|me|info|tv|in|co|ws|biz|cc|net)\\b".toRegex()
        clean = domainRegex.replace(clean, "")

        // Regex for quality/codec/source tags
        val tagsRegex = "(?i)\\b(1080p|720p|480p|2160p|4k|x264|x265|hevc|h264|h265|web-dl|webdl|bluray|hdrip|webrip|dvdrip|dd5\\.1|dual[- ]audio|multi|hq|aac|mkv|mp4|avi|hdr|hdtv)\\b".toRegex()
        clean = tagsRegex.replace(clean, "")

        // Replace hyphens, dots, underscores, brackets with space
        clean = clean.replace(listOf(".", "-", "_", "[", "]", "(", ")"), " ")

        // Replace multiple spaces with a single space
        clean = clean.replace("\\s+".toRegex(), " ").trim()

        // Capitalize words for premium look
        clean = clean.split(" ").joinToString(" ") { word ->
            if (word.matches("(?i)S\\d+E\\d+".toRegex())) {
                word.uppercase()
            } else {
                word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            }
        }

        // If the result is empty, return original name
        return if (clean.isEmpty()) name else clean
    }

    private fun String.replace(chars: List<String>, replacement: String): String {
        var result = this
        for (c in chars) {
            result = result.replace(c, replacement)
        }
        return result
    }
}
