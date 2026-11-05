package com.anhtuta.lilimusic.feature.player

import app.cash.turbine.test
import com.anhtuta.lilimusic.core.datastore.PlaybackPreferenceState
import com.anhtuta.lilimusic.core.model.PlayMode
import com.anhtuta.lilimusic.core.playback.FakePlayerController
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class PlayerViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val songs =
        listOf(
            playerTestSong(id = 1, title = "Alpha", path = "alpha"),
            playerTestSong(id = 2, title = "Beta", path = "beta"),
        )

    @Test
    fun ensurePlayingStartsRequestedSong() =
        runTest {
            val controller = FakePlayerController()
            val repository = FakePlayerSongRepository().apply { emit(songs) }
            val viewModel = PlayerViewModel(controller, repository, FakePlaybackPreferences())

            viewModel.ensurePlaying("beta")

            viewModel.uiState.test {
                val state = awaitItemUntil { it.song?.path == "beta" && it.isPlaying }
                assertThat(state.song?.title).isEqualTo("Beta")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun restoreLastSongWithoutAutoplay() =
        runTest {
            val controller = FakePlayerController()
            val repository = FakePlayerSongRepository().apply { emit(songs) }
            val prefs =
                FakePlaybackPreferences(
                    PlaybackPreferenceState(
                        playMode = PlayMode.REPEAT_ONE,
                        lastSongPath = "alpha",
                        speed = 1.2f,
                    ),
                )

            val viewModel = PlayerViewModel(controller, repository, prefs)

            viewModel.uiState.test {
                val state = awaitItemUntil { it.song?.path == "alpha" }
                assertThat(state.isPlaying).isFalse()
                assertThat(state.playMode).isEqualTo(PlayMode.REPEAT_ONE)
                assertThat(state.speed).isEqualTo(1.2f)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun playPauseAndSkipDelegateToController() =
        runTest {
            val controller = FakePlayerController()
            val repository = FakePlayerSongRepository().apply { emit(songs) }
            val viewModel = PlayerViewModel(controller, repository, FakePlaybackPreferences())
            viewModel.ensurePlaying("alpha")

            viewModel.playPause()
            assertThat(controller.state.value.isPlaying).isFalse()

            viewModel.playPause()
            viewModel.skipToNext()
            assertThat(
                controller.state.value.currentSong
                    ?.path,
            ).isEqualTo("beta")

            viewModel.toggleShuffle()
            assertThat(controller.state.value.playMode).isEqualTo(PlayMode.SHUFFLE)

            viewModel.increaseSpeed()
            assertThat(controller.state.value.speed).isEqualTo(1.1f)
        }

    @Test
    fun seekUpdatesPosition() =
        runTest {
            val controller = FakePlayerController()
            val repository = FakePlayerSongRepository().apply { emit(songs) }
            val viewModel = PlayerViewModel(controller, repository, FakePlaybackPreferences())
            viewModel.ensurePlaying("alpha")

            viewModel.seekTo(12_000L)

            assertThat(controller.state.value.positionMs).isEqualTo(12_000L)
        }
}

private suspend fun app.cash.turbine.ReceiveTurbine<PlayerUiState>.awaitItemUntil(predicate: (PlayerUiState) -> Boolean): PlayerUiState {
    while (true) {
        val item = awaitItem()
        if (predicate(item)) return item
    }
}
