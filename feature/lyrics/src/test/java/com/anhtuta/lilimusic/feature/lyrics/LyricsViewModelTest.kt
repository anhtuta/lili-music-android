package com.anhtuta.lilimusic.feature.lyrics

import app.cash.turbine.test
import com.anhtuta.lilimusic.core.data.repository.LyricLoadResult
import com.anhtuta.lilimusic.core.lyrics.LyricSynchronizer
import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricTheme
import com.anhtuta.lilimusic.core.model.PlayerSettings
import com.anhtuta.lilimusic.core.playback.FakePlayerController
import com.google.common.truth.Truth.assertThat
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class LyricsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val song = lyricsTestSong(lyricFileName = "song.xlrc")
    private val other = lyricsTestSong(id = 2, title = "Beta", path = "beta", lyricFileName = "other.lrc")

    @Test
    fun loadsLyricWhenSongStarts() =
        runTest {
            val controller = FakePlayerController()
            val repository = FakeLyricRepository(LyricLoadResult.Ready(sampleParsedLyric()))
            val viewModel = LyricsViewModel(controller, repository, FakeSettingsRepository())

            controller.play(listOf(song), "alpha")

            viewModel.uiState.test {
                val state = awaitItemUntil { it.frame != null }
                assertThat(repository.requests).contains("song.xlrc")
                assertThat(state.frame?.lines).isNotEmpty()
                assertThat(
                    state.frame
                        ?.lines
                        ?.get(1)
                        ?.translations
                        ?.single()
                        ?.text,
                ).isEqualTo("Segundo")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun highlightsLineAsPositionAdvances() =
        runTest {
            val controller = FakePlayerController()
            val repository = FakeLyricRepository(LyricLoadResult.Ready(sampleParsedLyric()))
            val viewModel = LyricsViewModel(controller, repository, FakeSettingsRepository())
            controller.play(listOf(song), "alpha")

            controller.advancePosition(1_200L)

            viewModel.uiState.test {
                val state = awaitItemUntil { it.frame?.activeLineIndex == 1 }
                assertThat(
                    state.frame
                        ?.lines
                        ?.get(1)
                        ?.isActive,
                ).isTrue()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun copiesPlaybackClockForKaraokeInterpolation() =
        runTest {
            val controller = FakePlayerController()
            val repository = FakeLyricRepository(LyricLoadResult.Ready(sampleParsedLyric()))
            val viewModel = LyricsViewModel(controller, repository, FakeSettingsRepository())
            controller.play(listOf(song), "alpha")
            controller.advancePosition(1_200L)

            viewModel.uiState.test {
                val state = awaitItemUntil { it.positionMs == 1_200L && it.frame != null }
                assertThat(state.isPlaying).isTrue()
                assertThat(state.positionSampledAtElapsedMs)
                    .isEqualTo(controller.state.value.positionSampledAtElapsedMs)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun nudgeOffsetIsSessionOnlyAndResetsOnSongChange() =
        runTest {
            val controller = FakePlayerController()
            val repository = FakeLyricRepository(LyricLoadResult.Ready(sampleParsedLyric()))
            val viewModel = LyricsViewModel(controller, repository, FakeSettingsRepository())
            controller.play(listOf(song, other), "alpha")

            viewModel.nudgeOffset(LyricSynchronizer.USER_OFFSET_STEP_MS)
            viewModel.uiState.test {
                val offset = awaitItemUntil { it.userOffsetMs == 100L }
                assertThat(offset.frame?.userOffsetMs).isEqualTo(100L)
                cancelAndIgnoreRemainingEvents()
            }

            controller.play(listOf(song, other), "beta")
            viewModel.uiState.test {
                val reset = awaitItemUntil { it.hasSong && it.userOffsetMs == 0L && it.frame != null }
                assertThat(reset.userOffsetMs).isEqualTo(0L)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun tapLineSeeksToCombinedOffset() =
        runTest {
            val controller = FakePlayerController()
            val repository = FakeLyricRepository(LyricLoadResult.Ready(sampleParsedLyric()))
            val viewModel = LyricsViewModel(controller, repository, FakeSettingsRepository())
            controller.play(listOf(song), "alpha")
            controller.advancePosition(50L)

            viewModel.seekToLine(1_000L)

            assertThat(controller.state.value.positionMs).isEqualTo(1_000L)
        }

    @Test
    fun missingLyricShowsEmptyState() =
        runTest {
            val controller = FakePlayerController()
            val repository = FakeLyricRepository(LyricLoadResult.Missing)
            val viewModel = LyricsViewModel(controller, repository, FakeSettingsRepository())
            controller.play(listOf(lyricsTestSong(lyricFileName = null)), "alpha")

            viewModel.uiState.test {
                val state = awaitItemUntil { it.isMissing }
                assertThat(state.frame).isNull()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun errorStateSurfaces() =
        runTest {
            val controller = FakePlayerController()
            val repository = FakeLyricRepository(LyricLoadResult.Error(IOException("offline")))
            val viewModel = LyricsViewModel(controller, repository, FakeSettingsRepository())
            controller.play(listOf(song), "alpha")

            viewModel.uiState.test {
                val state = awaitItemUntil { it.errorMessage != null }
                assertThat(state.errorMessage).isEqualTo("offline")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun settingsAndCoverUrlFollowRepositoryAndSong() =
        runTest {
            val controller = FakePlayerController()
            val repository = FakeLyricRepository(LyricLoadResult.Ready(sampleParsedLyric()))
            val settingsRepository =
                FakeSettingsRepository(
                    PlayerSettings(translate = true, lyricAlign = LyricAlign.LEFT),
                )
            val viewModel = LyricsViewModel(controller, repository, settingsRepository)
            val covered = lyricsTestSong(imageUrl = "https://example.com/cover.jpg")
            controller.play(listOf(covered), "alpha")

            viewModel.uiState.test {
                val loaded = awaitItemUntil { it.frame != null && it.coverUrl != null }
                assertThat(loaded.settings.translate).isTrue()
                assertThat(loaded.settings.lyricAlign).isEqualTo(LyricAlign.LEFT)
                assertThat(loaded.coverUrl).isEqualTo("https://example.com/cover.jpg")
                cancelAndIgnoreRemainingEvents()
            }

            settingsRepository.emit(PlayerSettings(translate = false, lyricTheme = LyricTheme.DARK))
            viewModel.uiState.test {
                val updated = awaitItemUntil { !it.settings.translate && it.settings.lyricTheme == LyricTheme.DARK }
                assertThat(updated.settings.translate).isFalse()
                cancelAndIgnoreRemainingEvents()
            }
        }
}

private suspend fun app.cash.turbine.ReceiveTurbine<LyricsUiState>.awaitItemUntil(predicate: (LyricsUiState) -> Boolean): LyricsUiState {
    while (true) {
        val item = awaitItem()
        if (predicate(item)) return item
    }
}
