package dev.anilbeesetti.nextplayer.feature.player.tools

import androidx.media3.common.Player

class AbRepeatManager {

    var pointA: Long? = null
        private set

    var pointB: Long? = null
        private set

    val isActive: Boolean
        get() = pointA != null && pointB != null

    fun setPointA(positionMs: Long) {
        pointA = positionMs
        if (pointB != null && pointB!! <= positionMs) {
            pointB = null // Reset point B if invalid
        }
    }

    fun setPointB(positionMs: Long) {
        if (pointA != null && positionMs > pointA!!) {
            pointB = positionMs
        }
    }

    fun clear() {
        pointA = null
        pointB = null
    }

    fun checkLoop(player: Player?) {
        val p = player ?: return
        val start = pointA ?: return
        val end = pointB ?: return
        if (p.currentPosition >= end) {
            p.seekTo(start)
        }
    }
}
