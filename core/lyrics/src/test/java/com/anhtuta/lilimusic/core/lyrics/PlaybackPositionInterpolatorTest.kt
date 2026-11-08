package com.anhtuta.lilimusic.core.lyrics

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PlaybackPositionInterpolatorTest {
    @Test
    fun pausedKeepsSampledPosition() {
        val snapshot =
            PlaybackPositionInterpolator.Snapshot(
                positionMs = 1_000L,
                speed = 1f,
                isPlaying = false,
                capturedAtElapsedMs = 5_000L,
            )
        assertThat(PlaybackPositionInterpolator.interpolate(snapshot, nowElapsedMs = 5_250L))
            .isEqualTo(1_000L)
    }

    @Test
    fun playingAtOneTimesAdvancesWithElapsedRealtime() {
        val clock = FakeElapsedClock(10_000L)
        val snapshot =
            PlaybackPositionInterpolator.Snapshot(
                positionMs = 400L,
                speed = 1f,
                isPlaying = true,
                capturedAtElapsedMs = clock.now(),
            )
        clock.advance(100L)
        assertThat(PlaybackPositionInterpolator.interpolate(snapshot, clock.now())).isEqualTo(500L)
    }

    @Test
    fun playingAtOnePointFiveTimesScalesElapsed() {
        val clock = FakeElapsedClock(1_000L)
        val snapshot =
            PlaybackPositionInterpolator.Snapshot(
                positionMs = 0L,
                speed = 1.5f,
                isPlaying = true,
                capturedAtElapsedMs = clock.now(),
            )
        clock.advance(100L)
        assertThat(PlaybackPositionInterpolator.interpolate(snapshot, clock.now())).isEqualTo(150L)
    }

    @Test
    fun missingStampDoesNotInventPosition() {
        val snapshot =
            PlaybackPositionInterpolator.Snapshot(
                positionMs = 250L,
                speed = 1f,
                isPlaying = true,
                capturedAtElapsedMs = 0L,
            )
        assertThat(PlaybackPositionInterpolator.interpolate(snapshot, nowElapsedMs = 8_000L))
            .isEqualTo(250L)
    }

    @Test
    fun interpolatedClockAdvancesTrcFillWhilePlayerHolds() {
        val clock = FakeElapsedClock(1_000L)
        val snapshot =
            PlaybackPositionInterpolator.Snapshot(
                positionMs = 200L,
                speed = 1f,
                isPlaying = true,
                capturedAtElapsedMs = clock.now(),
            )
        clock.advance(40L)
        val displayMs = PlaybackPositionInterpolator.interpolate(snapshot, clock.now())
        assertThat(displayMs).isEqualTo(240L)
        assertThat(
            LyricSynchronizer.karaokeFill(
                startMs = 0L,
                endMs = 400L,
                positionMs = displayMs,
                offsetMs = 0L,
            ),
        ).isWithin(0.001f).of(0.6f)
    }

    @Test
    fun elapsedGoingBackwardDoesNotRewind() {
        val snapshot =
            PlaybackPositionInterpolator.Snapshot(
                positionMs = 800L,
                speed = 1f,
                isPlaying = true,
                capturedAtElapsedMs = 2_000L,
            )
        assertThat(PlaybackPositionInterpolator.interpolate(snapshot, nowElapsedMs = 1_500L))
            .isEqualTo(800L)
    }

    private class FakeElapsedClock(
        private var elapsedMs: Long,
    ) {
        fun now(): Long = elapsedMs

        fun advance(deltaMs: Long) {
            elapsedMs += deltaMs
        }
    }
}
