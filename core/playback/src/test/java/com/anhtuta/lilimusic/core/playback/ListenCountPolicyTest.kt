package com.anhtuta.lilimusic.core.playback

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ListenCountPolicyTest {
    @Test
    fun requiredListeningMsIsHalfOfDuration() {
        assertThat(ListenCountPolicy.requiredListeningMs(240_000L)).isEqualTo(120_000L)
        assertThat(ListenCountPolicy.requiredListeningMs(60_000L)).isEqualTo(30_000L)
    }

    @Test
    fun requiredListeningMsUsesUnknownFallback() {
        assertThat(ListenCountPolicy.requiredListeningMs(androidx.media3.common.C.TIME_UNSET))
            .isEqualTo(PlaybackDuration.UNKNOWN_FALLBACK_MS / 2)
    }

    @Test
    fun mediaAdvanceIgnoresNegativeAndLargeJumps() {
        assertThat(
            ListenCountAccumulation.mediaAdvanceDelta(
                previousPositionMs = 10_000L,
                positionMs = 9_000L,
                samePath = true,
                wasPlaying = true,
            ),
        ).isEqualTo(0L)
        assertThat(
            ListenCountAccumulation.mediaAdvanceDelta(
                previousPositionMs = 0L,
                positionMs = 60_000L,
                samePath = true,
                wasPlaying = true,
            ),
        ).isEqualTo(0L)
        assertThat(
            ListenCountAccumulation.mediaAdvanceDelta(
                previousPositionMs = 0L,
                positionMs = 400L,
                samePath = true,
                wasPlaying = true,
            ),
        ).isEqualTo(400L)
    }
}

class ListenCountSessionTest {
    private val song = playbackTestSong(id = 1, path = "alpha")

    private fun state(
        path: String? = song.path,
        playing: Boolean = false,
        positionMs: Long = 0L,
        durationMs: Long = 120_000L,
    ) = PlaybackState(
        currentSong = if (path == null) null else song.copy(path = path),
        isPlaying = playing,
        positionMs = positionMs,
        durationMs = durationMs,
    )

    @Test
    fun playFromMiddleSchedulesRemainingHalf() {
        val session = ListenCountSession()
        val idle = state(playing = false, positionMs = 60_000L)
        val playing = state(playing = true, positionMs = 60_000L)

        val decision = session.onPlaybackTransition(idle, playing, queueRestart = false)

        assertThat(decision.schedulePath).isEqualTo("alpha")
        assertThat(decision.delayMs).isEqualTo(60_000L)
    }

    @Test
    fun pauseCancelsPendingWork() {
        val session = ListenCountSession()
        session.onPlaybackTransition(
            state(playing = false),
            state(playing = true),
            queueRestart = true,
        )
        val paused = session.onPlaybackTransition(state(playing = true), state(playing = false), queueRestart = false)

        assertThat(paused.cancelPending).isTrue()
        assertThat(paused.schedulePath).isNull()
    }

    @Test
    fun resumeAfterPauseSchedulesRemaining() {
        val session = ListenCountSession()
        session.onPlaybackTransition(state(playing = false), state(playing = true, positionMs = 0L), queueRestart = true)
        var pos = 0L
        repeat(60) {
            val next = pos + 500L
            session.onPlayingTick(next, pos, 120_000L, "alpha")
            pos = next
        }
        session.onPlaybackTransition(
            state(playing = true, positionMs = pos),
            state(playing = false, positionMs = pos),
            queueRestart = false,
        )

        val resume =
            session.onPlaybackTransition(
                state(playing = false, positionMs = pos),
                state(playing = true, positionMs = pos),
                queueRestart = false,
            )

        assertThat(resume.schedulePath).isEqualTo("alpha")
        assertThat(resume.delayMs).isEqualTo(30_000L)
    }

    @Test
    fun pathChangeCancelsAndResets() {
        val session = ListenCountSession()
        session.onPlaybackTransition(state(playing = false), state(playing = true), queueRestart = true)
        val beta = playbackTestSong(id = 2, path = "beta")

        val decision =
            session.onPlaybackTransition(
                state(playing = true),
                state(path = "beta", playing = true).copy(currentSong = beta),
                queueRestart = false,
            )

        assertThat(decision.cancelPending).isTrue()
        assertThat(session.state.accumulatedPlayingMs).isEqualTo(0L)
    }

    @Test
    fun tickPastHalfSchedulesImmediately() {
        val session = ListenCountSession()
        session.onPlaybackTransition(state(playing = false), state(playing = true), queueRestart = true)

        var pos = 0L
        var decision: ListenCountDecision? = null
        repeat(120) {
            val nextPos = pos + 500L
            decision =
                session.onPlayingTick(
                    positionMs = nextPos,
                    previousPositionMs = pos,
                    durationMs = 120_000L,
                    path = "alpha",
                )
            pos = nextPos
        }

        assertThat(decision?.schedulePath).isEqualTo("alpha")
        assertThat(decision?.delayMs).isEqualTo(0L)
        assertThat(session.state.listenRecorded).isTrue()
    }
}
