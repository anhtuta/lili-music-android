package com.anhtuta.lilimusic.feature.downloads

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.anhtuta.lilimusic.core.model.DownloadStatus
import com.anhtuta.lilimusic.core.playback.FakePlayerController
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class DownloadsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun listsDownloadsFromRepository() =
        runTest {
            val repository = FakeDownloadRepository()
            val viewModel = DownloadsViewModel(repository, FakePlayerController())
            repository.emit(testDownload(title = "Hello"))

            viewModel.uiState.test {
                val state = awaitItemUntil { it.items.isNotEmpty() }
                assertThat(state.items.single().title).isEqualTo("Hello")
                assertThat(state.isEmpty).isFalse()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun retryAndDeleteForwardToRepository() =
        runTest {
            val repository = FakeDownloadRepository()
            val viewModel = DownloadsViewModel(repository, FakePlayerController())
            val failed = testDownload(status = DownloadStatus.FAILED)

            viewModel.retry(failed)
            viewModel.remove(failed)

            assertThat(repository.retried).containsExactly("hello-artist")
            assertThat(repository.removed).containsExactly("hello-artist")
        }

    @Test
    fun cancelForwardsActiveDownloadToRepository() =
        runTest {
            val repository = FakeDownloadRepository()
            val viewModel = DownloadsViewModel(repository, FakePlayerController())
            val downloading = testDownload(status = DownloadStatus.DOWNLOADING, progressPercent = 40)

            viewModel.cancel(downloading)

            assertThat(repository.cancelled).containsExactly("hello-artist")
        }

    @Test
    fun cancelIgnoresCompletedDownload() =
        runTest {
            val repository = FakeDownloadRepository()
            val viewModel = DownloadsViewModel(repository, FakePlayerController())

            viewModel.cancel(testDownload(status = DownloadStatus.COMPLETED))

            assertThat(repository.cancelled).isEmpty()
        }

    @Test
    fun deleteAllRequiresConfirm() =
        runTest {
            val repository = FakeDownloadRepository()
            val viewModel = DownloadsViewModel(repository, FakePlayerController())
            repository.emit(testDownload())

            viewModel.onDeleteAllClick()
            viewModel.uiState.test {
                assertThat(awaitItemUntil { it.confirmDeleteAll }.confirmDeleteAll).isTrue()
                cancelAndIgnoreRemainingEvents()
            }

            viewModel.onConfirmDeleteAll()
            assertThat(repository.removedAll).isTrue()
        }

    @Test
    fun playCompletedStartsPlayer() =
        runTest {
            val controller = FakePlayerController()
            val viewModel = DownloadsViewModel(FakeDownloadRepository(), controller)
            val item = testDownload(status = DownloadStatus.COMPLETED)

            viewModel.play(item)

            assertThat(
                controller.state.value.currentSong
                    ?.path,
            ).isEqualTo("hello-artist")
            assertThat(controller.state.value.isPlaying).isTrue()
        }

    @Test
    fun playSkipsIncompleteDownloads() =
        runTest {
            val controller = FakePlayerController()
            val viewModel = DownloadsViewModel(FakeDownloadRepository(), controller)

            viewModel.play(testDownload(status = DownloadStatus.DOWNLOADING))

            assertThat(controller.state.value.currentSong).isNull()
        }
}

private suspend fun ReceiveTurbine<DownloadsUiState>.awaitItemUntil(predicate: (DownloadsUiState) -> Boolean): DownloadsUiState {
    while (true) {
        val item = awaitItem()
        if (predicate(item)) return item
    }
}
