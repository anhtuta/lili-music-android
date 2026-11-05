package com.anhtuta.lilimusic.core.lyrics

import kotlin.math.roundToLong

/**
 * Converts LRC timestamps to milliseconds, matching web `decodeTime` in UtilService.js.
 */
object LyricTimeDecoder {
    private val hourPattern = Regex("""\[(\d+):(\d+):(\d+\.\d+)\]""")
    private val minutePattern = Regex("""\[(\d+):(\d+\.\d+)\]""")

    fun decode(timestamp: String): Long {
        hourPattern.matchEntire(timestamp)?.let { match ->
            val hours = match.groupValues[1].toDouble()
            val minutes = match.groupValues[2].toDouble()
            val seconds = match.groupValues[3].toDouble()
            return secondsToMs(hours * 3600.0 + minutes * 60.0 + seconds)
        }
        minutePattern.matchEntire(timestamp)?.let { match ->
            val minutes = match.groupValues[1].toDouble()
            val seconds = match.groupValues[2].toDouble()
            return secondsToMs(minutes * 60.0 + seconds)
        }
        return 0L
    }

    internal fun secondsToMs(seconds: Double): Long = (seconds * 1000.0).roundToLong()
}
