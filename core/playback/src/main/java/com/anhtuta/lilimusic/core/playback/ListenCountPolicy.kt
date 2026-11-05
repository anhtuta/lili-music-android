package com.anhtuta.lilimusic.core.playback

data class ListenCountDecision(
    val cancelPending: Boolean,
    val schedulePath: String? = null,
)

object ListenCountPolicy {
    const val DELAY_MS = 60_000L
    const val FROM_START_MAX_POSITION_MS = 1_000L

    fun decide(
        previousPath: String?,
        previousPlaying: Boolean,
        currentPath: String?,
        currentPlaying: Boolean,
        positionMs: Long,
    ): ListenCountDecision {
        val pathChanged = previousPath != currentPath
        val stopped = previousPlaying && !currentPlaying
        val startedFromStart =
            currentPlaying &&
                !currentPath.isNullOrBlank() &&
                positionMs <= FROM_START_MAX_POSITION_MS &&
                (!previousPlaying || pathChanged)
        val cancelPending = (pathChanged && previousPath != null) || stopped
        return ListenCountDecision(
            cancelPending = cancelPending,
            schedulePath = currentPath.takeIf { startedFromStart },
        )
    }
}
