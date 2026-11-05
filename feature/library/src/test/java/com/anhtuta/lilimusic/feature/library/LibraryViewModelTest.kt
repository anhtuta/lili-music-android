package com.anhtuta.lilimusic.feature.library

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.anhtuta.lilimusic.core.model.SongSort
import com.anhtuta.lilimusic.core.model.SortOrder
import com.anhtuta.lilimusic.core.playback.FakePlayerController
import com.google.common.truth.Truth.assertThat
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class LibraryViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun showsLoadingUntilFirstRefreshFinishes() =
        runTest {
            val repository =
                FakeSongRepository().apply {
                    refreshGate = CompletableDeferred()
                }
            val viewModel = LibraryViewModel(repository, FakeSortPreferences(), FakePlayerController())

            viewModel.uiState.test {
                val loading = awaitItemUntil { it.isLoading }
                assertThat(loading.isLoading).isTrue()
                assertThat(loading.songs).isEmpty()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun successShowsSortedSongs() =
        runTest {
            val repository = FakeSongRepository()
            val viewModel = LibraryViewModel(repository, FakeSortPreferences(), FakePlayerController())
            repository.emit(
                listOf(
                    libraryTestSong(id = 2, title = "Beta"),
                    libraryTestSong(id = 1, title = "Alpha"),
                ),
            )

            viewModel.uiState.test {
                val state = awaitItemUntil { it.songs.map { song -> song.title } == listOf("Alpha", "Beta") }
                assertThat(state.isLoading).isFalse()
                assertThat(state.isOfflineError).isFalse()
                assertThat(repository.refreshCalls).isNotEmpty()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun searchFiltersVisibleSongs() =
        runTest {
            val repository = FakeSongRepository()
            val viewModel = LibraryViewModel(repository, FakeSortPreferences(), FakePlayerController())
            repository.emit(
                listOf(
                    libraryTestSong(id = 1, title = "Hello", artist = "Ann"),
                    libraryTestSong(id = 2, title = "World", artist = "Bob"),
                ),
            )

            viewModel.onQueryChange("bob")

            viewModel.uiState.test {
                val state = awaitItemUntil { it.songs.size == 1 }
                assertThat(state.songs.single().title).isEqualTo("World")
                assertThat(state.query).isEqualTo("bob")
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun emptyCatalogShowsEmptyState() =
        runTest {
            val repository = FakeSongRepository()
            val viewModel = LibraryViewModel(repository, FakeSortPreferences(), FakePlayerController())

            viewModel.uiState.test {
                val state = awaitItemUntil { !it.isLoading }
                assertThat(state.songs).isEmpty()
                assertThat(state.isOfflineError).isFalse()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun refreshFailureWithNoCacheShowsError() =
        runTest {
            val repository =
                FakeSongRepository().apply {
                    refreshResult = Result.failure(IOException("offline"))
                }
            val viewModel = LibraryViewModel(repository, FakeSortPreferences(), FakePlayerController())

            viewModel.uiState.test {
                val state = awaitItemUntil { it.isOfflineError }
                assertThat(state.songs).isEmpty()
                assertThat(state.isLoading).isFalse()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun refreshFailureKeepsCachedSongs() =
        runTest {
            val repository =
                FakeSongRepository().apply {
                    refreshResult = Result.failure(IOException("offline"))
                }
            repository.emit(listOf(libraryTestSong(title = "Cached")))
            val viewModel = LibraryViewModel(repository, FakeSortPreferences(), FakePlayerController())

            viewModel.uiState.test {
                val state = awaitItemUntil { it.isOfflineError && it.songs.isNotEmpty() }
                assertThat(state.songs.single().title).isEqualTo("Cached")
                assertThat(state.isLoading).isFalse()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun selectingSortUsesColumnDefaultOrder() =
        runTest {
            val preferences = FakeSortPreferences()
            val viewModel = LibraryViewModel(FakeSongRepository(), preferences, FakePlayerController())

            viewModel.onSortSelected(SongSort.LISTENS)

            viewModel.uiState.test {
                val state = awaitItemUntil { it.sort == SongSort.LISTENS }
                assertThat(state.sortOrder).isEqualTo(SortOrder.DESC)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun pullToRefreshSendsCurrentSortToRepository() =
        runTest {
            val repository = FakeSongRepository()
            val preferences = FakeSortPreferences()
            val viewModel = LibraryViewModel(repository, preferences, FakePlayerController())
            viewModel.onSortSelected(SongSort.TYPE)
            repository.refreshCalls.clear()

            viewModel.onRefresh()

            assertThat(repository.refreshCalls.last()).isEqualTo(SongSort.TYPE to SortOrder.ASC)
        }

    @Test
    fun songClickPlaysVisibleQueueFromThatPath() =
        runTest {
            val repository = FakeSongRepository()
            val controller = FakePlayerController()
            val viewModel = LibraryViewModel(repository, FakeSortPreferences(), controller)
            repository.emit(
                listOf(
                    libraryTestSong(id = 2, title = "Beta", path = "beta"),
                    libraryTestSong(id = 1, title = "Alpha", path = "alpha"),
                ),
            )
            viewModel.uiState.test {
                awaitItemUntil { it.songs.map { song -> song.path } == listOf("alpha", "beta") }
                cancelAndIgnoreRemainingEvents()
            }

            viewModel.onSongClick(libraryTestSong(id = 2, title = "Beta", path = "beta"))

            assertThat(
                controller.state.value.currentSong
                    ?.path,
            ).isEqualTo("beta")
            assertThat(controller.state.value.isPlaying).isTrue()
            assertThat(
                controller.state.value.queue
                    .map { it.path },
            ).containsExactly("alpha", "beta").inOrder()
        }
}

private suspend fun ReceiveTurbine<LibraryUiState>.awaitItemUntil(predicate: (LibraryUiState) -> Boolean): LibraryUiState {
    while (true) {
        val item = awaitItem()
        if (predicate(item)) return item
    }
}
