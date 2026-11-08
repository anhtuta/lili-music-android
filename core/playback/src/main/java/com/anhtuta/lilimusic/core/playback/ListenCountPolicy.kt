package com.anhtuta.lilimusic.core.playback

data class ListenCountDecision(
    val cancelPending: Boolean,
    val schedulePath: String? = null,
)

object ListenCountPolicy {
    const val DELAY_MS = 60_000L

    /** ExoPlayer often reports a few ms of position on the first playing frame after a new item. */
    const val START_TOLERANCE_MS = 1_500L

    /**
     * Web `BaiHat.js` `onPlay`: arm a 60s listen only when `currentTime === 0`.
     * The timeout is not cleared on pause/resume/finish; it no-ops if `path` changed.
     */
    fun decide(
        previousPath: String?,
        previousPlaying: Boolean,
        currentPath: String?,
        currentPlaying: Boolean,
        positionMs: Long,
    ): ListenCountDecision {
        val pathChanged = previousPath != currentPath
        val atPlaybackStart = atPlaybackStart(positionMs, pathChanged)
        val startedFromStart =
            currentPlaying &&
                !currentPath.isNullOrBlank() &&
                atPlaybackStart &&
                (!previousPlaying || pathChanged)
        val cancelPending = pathChanged && previousPath != null
        return ListenCountDecision(
            cancelPending = cancelPending,
            schedulePath = currentPath.takeIf { startedFromStart },
        )
    }

    internal fun atPlaybackStart(
        positionMs: Long,
        pathChanged: Boolean,
    ): Boolean =
        positionMs == 0L ||
            (pathChanged && positionMs in 1..START_TOLERANCE_MS)
}
