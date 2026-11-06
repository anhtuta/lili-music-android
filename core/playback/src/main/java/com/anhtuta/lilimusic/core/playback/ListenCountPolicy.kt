package com.anhtuta.lilimusic.core.playback

data class ListenCountDecision(
    val cancelPending: Boolean,
    val schedulePath: String? = null,
)

object ListenCountPolicy {
    const val DELAY_MS = 60_000L

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
        val startedFromStart =
            currentPlaying &&
                !currentPath.isNullOrBlank() &&
                positionMs == 0L &&
                (!previousPlaying || pathChanged)
        val cancelPending = pathChanged && previousPath != null
        return ListenCountDecision(
            cancelPending = cancelPending,
            schedulePath = currentPath.takeIf { startedFromStart },
        )
    }
}
