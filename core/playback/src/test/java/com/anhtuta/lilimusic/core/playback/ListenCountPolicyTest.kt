package com.anhtuta.lilimusic.core.playback

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ListenCountPolicyTest {
    @Test
    fun playFromStartSchedulesListen() {
        val decision =
            ListenCountPolicy.decide(
                previousPath = null,
                previousPlaying = false,
                currentPath = "hello",
                currentPlaying = true,
                positionMs = 0L,
            )

        assertThat(decision.cancelPending).isFalse()
        assertThat(decision.schedulePath).isEqualTo("hello")
    }

    @Test
    fun playFromNonZeroPositionDoesNotSchedule() {
        val decision =
            ListenCountPolicy.decide(
                previousPath = null,
                previousPlaying = false,
                currentPath = "hello",
                currentPlaying = true,
                positionMs = 1L,
            )

        assertThat(decision.cancelPending).isFalse()
        assertThat(decision.schedulePath).isNull()
    }

    @Test
    fun pauseDoesNotCancelPendingListen() {
        val decision =
            ListenCountPolicy.decide(
                previousPath = "hello",
                previousPlaying = true,
                currentPath = "hello",
                currentPlaying = false,
                positionMs = 30_000L,
            )

        assertThat(decision.cancelPending).isFalse()
        assertThat(decision.schedulePath).isNull()
    }

    @Test
    fun finishingDoesNotCancelPendingListen() {
        val decision =
            ListenCountPolicy.decide(
                previousPath = "hello",
                previousPlaying = true,
                currentPath = "hello",
                currentPlaying = false,
                positionMs = 180_000L,
            )

        assertThat(decision.cancelPending).isFalse()
        assertThat(decision.schedulePath).isNull()
    }

    @Test
    fun resumeFromMiddleDoesNotReschedule() {
        val decision =
            ListenCountPolicy.decide(
                previousPath = "hello",
                previousPlaying = false,
                currentPath = "hello",
                currentPlaying = true,
                positionMs = 30_000L,
            )

        assertThat(decision.cancelPending).isFalse()
        assertThat(decision.schedulePath).isNull()
    }

    @Test
    fun resumeFromStartSchedulesNewPlay() {
        val decision =
            ListenCountPolicy.decide(
                previousPath = "hello",
                previousPlaying = false,
                currentPath = "hello",
                currentPlaying = true,
                positionMs = 0L,
            )

        assertThat(decision.cancelPending).isFalse()
        assertThat(decision.schedulePath).isEqualTo("hello")
    }

    @Test
    fun songChangeCancelsAndSchedulesNewPath() {
        val decision =
            ListenCountPolicy.decide(
                previousPath = "hello",
                previousPlaying = true,
                currentPath = "world",
                currentPlaying = true,
                positionMs = 0L,
            )

        assertThat(decision.cancelPending).isTrue()
        assertThat(decision.schedulePath).isEqualTo("world")
    }

    @Test
    fun positionTickerWhilePlayingDoesNothing() {
        val decision =
            ListenCountPolicy.decide(
                previousPath = "hello",
                previousPlaying = true,
                currentPath = "hello",
                currentPlaying = true,
                positionMs = 1_000L,
            )

        assertThat(decision.cancelPending).isFalse()
        assertThat(decision.schedulePath).isNull()
    }
}
