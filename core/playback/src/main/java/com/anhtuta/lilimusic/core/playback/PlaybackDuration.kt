package com.anhtuta.lilimusic.core.playback

import androidx.media3.common.C

/**
 * Web `Player.js` `getDuration`: if the stream reports Infinity, treat length as 10 minutes
 * until a real duration arrives.
 */
object PlaybackDuration {
    const val UNKNOWN_FALLBACK_MS = 600_000L

    fun fromMedia3(durationMs: Long): Long =
        if (durationMs == C.TIME_UNSET) {
            UNKNOWN_FALLBACK_MS
        } else {
            durationMs.coerceAtLeast(0L)
        }
}
