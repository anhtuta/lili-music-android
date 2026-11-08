package com.anhtuta.lilimusic.core.lyrics

/**
 * Web karaoke uses `HTMLAudioElement.currentTime`, a continuous clock sampled every 30ms.
 * ExoPlayer `currentPosition` is millisecond and often holds between renderer updates, so a
 * short `.trc` word (150–400ms) jumps if fill is tied to that sample alone.
 *
 * Interpolate: `sampledPosition + (elapsedRealtime - sampledAt) * speed`.
 */
object PlaybackPositionInterpolator {
    data class Snapshot(
        val positionMs: Long,
        val speed: Float,
        val isPlaying: Boolean,
        val capturedAtElapsedMs: Long,
    )

    fun interpolate(
        snapshot: Snapshot,
        nowElapsedMs: Long,
    ): Long {
        if (!snapshot.isPlaying || snapshot.capturedAtElapsedMs <= 0L) {
            return snapshot.positionMs
        }
        val elapsedMs = (nowElapsedMs - snapshot.capturedAtElapsedMs).coerceAtLeast(0L)
        return snapshot.positionMs + (elapsedMs * snapshot.speed).toLong()
    }
}
