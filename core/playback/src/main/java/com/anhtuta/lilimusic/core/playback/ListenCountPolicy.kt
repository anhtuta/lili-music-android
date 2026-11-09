package com.anhtuta.lilimusic.core.playback

data class ListenCountDecision(
    val cancelPending: Boolean,
    val schedulePath: String? = null,
    val delayMs: Long = 0L,
)

data class ListenCountSessionState(
    val path: String? = null,
    val accumulatedPlayingMs: Long = 0L,
    val listenRecorded: Boolean = false,
)

object ListenCountPolicy {
    fun requiredListeningMs(durationMs: Long): Long {
        val safe = PlaybackDuration.fromMedia3(durationMs)
        return safe / 2
    }
}

object ListenCountAccumulation {
    const val MAX_POSITION_DELTA_MS = 500L

    /** Count normal playback advancement; ignore seeks and oversized jumps between samples. */
    fun mediaAdvanceDelta(
        previousPositionMs: Long,
        positionMs: Long,
        samePath: Boolean,
        wasPlaying: Boolean,
    ): Long {
        if (!wasPlaying || !samePath) return 0L
        val delta = positionMs - previousPositionMs
        return if (delta in 0..MAX_POSITION_DELTA_MS) delta else 0L
    }
}

/**
 * Android listen rule: PUT after ≥ 50% of track duration has been played on the same path.
 * Pause cancels pending WorkManager work but keeps accumulated progress.
 */
class ListenCountSession(
    initial: ListenCountSessionState = ListenCountSessionState(),
) {
    var state: ListenCountSessionState = initial
        private set

    fun onPlaybackTransition(
        previous: PlaybackState,
        next: PlaybackState,
        queueRestart: Boolean,
    ): ListenCountDecision {
        val previousPath = previous.currentSong?.path
        val currentPath = next.currentSong?.path
        val pathChanged = previousPath != currentPath
        val samePath = previousPath != null && previousPath == currentPath

        if (pathChanged || queueRestart) {
            val cancel = state.path != null || previousPath != null
            state = ListenCountSessionState(path = currentPath)
            if (currentPath == null) {
                return ListenCountDecision(cancelPending = cancel)
            }
        }

        if (previous.isPlaying && samePath) {
            val added =
                ListenCountAccumulation.mediaAdvanceDelta(
                    previousPositionMs = previous.positionMs,
                    positionMs = next.positionMs,
                    samePath = true,
                    wasPlaying = true,
                )
            if (added > 0L) {
                state = state.copy(accumulatedPlayingMs = state.accumulatedPlayingMs + added)
            }
        }

        val durationMs = next.durationMs
        val threshold = ListenCountPolicy.requiredListeningMs(durationMs)
        val currentPathOrNull = currentPath

        if (currentPathOrNull.isNullOrBlank() || state.listenRecorded) {
            return ListenCountDecision(cancelPending = pathChanged && previousPath != null)
        }

        if (state.accumulatedPlayingMs >= threshold) {
            state = state.copy(listenRecorded = true)
            return ListenCountDecision(
                cancelPending = false,
                schedulePath = currentPathOrNull,
                delayMs = 0L,
            )
        }

        val playingStarted = !previous.isPlaying && next.isPlaying
        val playingPaused = previous.isPlaying && !next.isPlaying
        val durationChanged = previous.durationMs != next.durationMs

        if (playingPaused) {
            return ListenCountDecision(cancelPending = true)
        }

        if ((playingStarted || durationChanged) && next.isPlaying) {
            val remaining = (threshold - state.accumulatedPlayingMs).coerceAtLeast(0L)
            return ListenCountDecision(
                cancelPending = false,
                schedulePath = currentPathOrNull,
                delayMs = remaining,
            )
        }

        return ListenCountDecision(cancelPending = pathChanged && previousPath != null)
    }

    fun onPlayingTick(
        positionMs: Long,
        previousPositionMs: Long,
        durationMs: Long,
        path: String?,
    ): ListenCountDecision? {
        if (path.isNullOrBlank() || state.listenRecorded || state.path != path) return null
        val added =
            ListenCountAccumulation.mediaAdvanceDelta(
                previousPositionMs = previousPositionMs,
                positionMs = positionMs,
                samePath = true,
                wasPlaying = true,
            )
        if (added > 0L) {
            state = state.copy(accumulatedPlayingMs = state.accumulatedPlayingMs + added)
        }
        val threshold = ListenCountPolicy.requiredListeningMs(durationMs)
        if (state.accumulatedPlayingMs < threshold) return null
        state = state.copy(listenRecorded = true)
        return ListenCountDecision(
            cancelPending = false,
            schedulePath = path,
            delayMs = 0L,
        )
    }
}
