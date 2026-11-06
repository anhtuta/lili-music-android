package com.anhtuta.lilimusic.core.playback

import app.cash.turbine.test
import com.anhtuta.lilimusic.core.model.PlayMode
import com.google.common.truth.Truth.assertThat
import kotlin.random.Random
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FakePlayerControllerTest {
    private val songs =
        listOf(
            playbackTestSong(id = 1, title = "Alpha", path = "alpha"),
            playbackTestSong(id = 2, title = "Beta", path = "beta"),
            playbackTestSong(id = 3, title = "Gamma", path = "gamma"),
        )

    @Test
    fun playStartsFromRequestedSongAndSchedulesListen() =
        runTest {
            val scheduler = RecordingListenCountScheduler()
            val controller = FakePlayerController(listenCountScheduler = scheduler)

            controller.state.test {
                skipItems(1)
                controller.play(songs, "beta")
                val playing = awaitItem()
                assertThat(playing.currentSong?.path).isEqualTo("beta")
                assertThat(playing.isPlaying).isTrue()
                assertThat(playing.positionMs).isEqualTo(0L)
                assertThat(scheduler.decisions.last().schedulePath).isEqualTo("beta")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun prepareRestoresSongWithoutPlaying() =
        runTest {
            val scheduler = RecordingListenCountScheduler()
            val controller = FakePlayerController(listenCountScheduler = scheduler)

            controller.prepare(songs, "gamma")

            assertThat(
                controller.state.value.currentSong
                    ?.path,
            ).isEqualTo("gamma")
            assertThat(controller.state.value.isPlaying).isFalse()
            assertThat(scheduler.decisions.last().schedulePath).isNull()
        }

    @Test
    fun sequenceStopsAtEndOfQueue() {
        val controller = FakePlayerController()
        controller.play(songs, "gamma")
        controller.skipToNext()

        assertThat(controller.state.value.isPlaying).isFalse()
        assertThat(controller.state.value.reachedEnd).isTrue()
        assertThat(
            controller.state.value.currentSong
                ?.path,
        ).isEqualTo("gamma")
    }

    @Test
    fun repeatOneSkipStaysOnSameSong() {
        val controller = FakePlayerController(initialPlayMode = PlayMode.REPEAT_ONE)
        controller.play(songs, "beta")
        controller.skipToNext()

        assertThat(
            controller.state.value.currentSong
                ?.path,
        ).isEqualTo("beta")
        assertThat(controller.state.value.positionMs).isEqualTo(0L)
    }

    @Test
    fun skipPreviousFromMiddleGoesToPreviousTrackNotRestart() {
        val controller = FakePlayerController()
        controller.play(songs, "beta")
        controller.advancePosition(20_000L)
        controller.skipToPrevious()

        assertThat(
            controller.state.value.currentSong
                ?.path,
        ).isEqualTo("alpha")
        assertThat(controller.state.value.positionMs).isEqualTo(0L)
    }

    @Test
    fun skipPreviousOnFirstTrackKeepsPositionAndSignalsStart() {
        val controller = FakePlayerController()
        controller.play(songs, "alpha")
        controller.advancePosition(20_000L)
        controller.skipToPrevious()

        assertThat(
            controller.state.value.currentSong
                ?.path,
        ).isEqualTo("alpha")
        assertThat(controller.state.value.positionMs).isEqualTo(20_000L)
        assertThat(controller.state.value.atStartOfQueue).isTrue()
        assertThat(controller.state.value.isPlaying).isTrue()
    }

    @Test
    fun skipPreviousInRepeatOneReplaysTheSameSong() {
        val controller = FakePlayerController(initialPlayMode = PlayMode.REPEAT_ONE)
        controller.play(songs, "beta")
        controller.advancePosition(20_000L)
        controller.skipToPrevious()

        assertThat(
            controller.state.value.currentSong
                ?.path,
        ).isEqualTo("beta")
        assertThat(controller.state.value.positionMs).isEqualTo(0L)
        assertThat(controller.state.value.isPlaying).isTrue()
    }

    @Test
    fun seekBackAndForwardMoveFiveSeconds() {
        val controller = FakePlayerController()
        controller.play(songs, "alpha")
        controller.advancePosition(20_000L)

        controller.seekBack()
        assertThat(controller.state.value.positionMs).isEqualTo(15_000L)

        controller.seekForward()
        assertThat(controller.state.value.positionMs).isEqualTo(20_000L)
    }

    @Test
    fun shufflePutsCurrentSongFirst() {
        val controller = FakePlayerController(random = Random(4))
        controller.play(songs, "gamma")
        controller.setPlayMode(PlayMode.SHUFFLE)

        assertThat(
            controller.state.value.queue
                .first()
                .path,
        ).isEqualTo("gamma")
        assertThat(controller.state.value.playMode).isEqualTo(PlayMode.SHUFFLE)
    }

    @Test
    fun pauseDoesNotCancelListenAndResumeFromMiddleDoesNotReschedule() {
        val scheduler = RecordingListenCountScheduler()
        val controller = FakePlayerController(listenCountScheduler = scheduler)
        controller.play(songs, "alpha")
        controller.advancePosition(20_000L)
        controller.playPause()

        assertThat(scheduler.decisions.last().cancelPending).isFalse()
        assertThat(scheduler.decisions.last().schedulePath).isNull()

        controller.playPause()
        assertThat(scheduler.decisions.last().cancelPending).isFalse()
        assertThat(scheduler.decisions.last().schedulePath).isNull()
    }
}
