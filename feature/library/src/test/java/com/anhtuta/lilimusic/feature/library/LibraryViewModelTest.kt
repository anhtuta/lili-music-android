package com.anhtuta.lilimusic.feature.library

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.anhtuta.lilimusic.core.model.DownloadStatus
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
            val viewModel = libraryViewModel(repository = repository)

            viewModel.uiState.test {
                val loading = awaitItemUntil { it.isLoading }
                assertThat(loading.isLoading).isTrue()
                assertThat(loading.songs).isEmpty()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun successShowsCatalogInApiOrder() =
        runTest {
            val repository = FakeSongRepository()
            val viewModel = libraryViewModel(repository = repository)
            repository.emit(
                listOf(
                    libraryTestSong(id = 2, title = "Beta"),
                    libraryTestSong(id = 1, title = "Alpha"),
                ),
            )

            viewModel.uiState.test {
                val state = awaitItemUntil { it.songs.map { song -> song.title } == listOf("Beta", "Alpha") }
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
            val viewModel = libraryViewModel(repository = repository)
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
            val viewModel = libraryViewModel(repository = repository)

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
            val viewModel = libraryViewModel(repository = repository)

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
            val viewModel = libraryViewModel(repository = repository)

            viewModel.uiState.test {
                val state = awaitItemUntil { it.isOfflineError && it.songs.isNotEmpty() }
                assertThat(state.songs.single().title).isEqualTo("Cached")
                assertThat(state.isLoading).isFalse()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun selectingSortUsesColumnDefaultOrderAndRefetches() =
        runTest {
            val repository = FakeSongRepository()
            val preferences = FakeSortPreferences()
            val viewModel = libraryViewModel(repository = repository, prefs = preferences)

            viewModel.onSortSelected(SongSort.LISTENS)

            viewModel.uiState.test {
                val state = awaitItemUntil { it.sort == SongSort.LISTENS }
                assertThat(state.sortOrder).isEqualTo(SortOrder.DESC)
                cancelAndIgnoreRemainingEvents()
            }
            assertThat(repository.refreshCalls.last()).isEqualTo(SongSort.LISTENS to SortOrder.DESC)
        }

    @Test
    fun togglingSortOrderRefetchesFromApi() =
        runTest {
            val repository = FakeSongRepository()
            val preferences = FakeSortPreferences()
            val viewModel = libraryViewModel(repository = repository, prefs = preferences)
            repository.refreshCalls.clear()

            viewModel.onSortOrderToggle()

            viewModel.uiState.test {
                val state = awaitItemUntil { it.sortOrder == SortOrder.DESC }
                assertThat(state.sort).isEqualTo(SongSort.TITLE)
                cancelAndIgnoreRemainingEvents()
            }
            assertThat(repository.refreshCalls.last()).isEqualTo(SongSort.TITLE to SortOrder.DESC)
        }

    @Test
    fun pullToRefreshSendsCurrentSortToRepository() =
        runTest {
            val repository = FakeSongRepository()
            val preferences = FakeSortPreferences()
            val viewModel = libraryViewModel(repository = repository, prefs = preferences)
            viewModel.onSortSelected(SongSort.TYPE)
            repository.refreshCalls.clear()

            viewModel.onRefresh()

            assertThat(repository.refreshCalls.last()).isEqualTo(SongSort.TYPE to SortOrder.ASC)
        }

    @Test
    fun songClickPlaysFullCatalogFromThatPath() =
        runTest {
            val repository = FakeSongRepository()
            val controller = FakePlayerController()
            val viewModel = libraryViewModel(repository = repository, controller = controller)
            repository.emit(
                listOf(
                    libraryTestSong(id = 2, title = "Beta", path = "beta"),
                    libraryTestSong(id = 1, title = "Alpha", path = "alpha"),
                ),
            )
            viewModel.uiState.test {
                awaitItemUntil { it.songs.map { song -> song.path } == listOf("beta", "alpha") }
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
            ).containsExactly("beta", "alpha").inOrder()
        }

    @Test
    fun searchFiltersViewButPlayUsesFullCatalog() =
        runTest {
            val repository = FakeSongRepository()
            val controller = FakePlayerController()
            val viewModel = libraryViewModel(repository = repository, controller = controller)
            val hello = libraryTestSong(id = 1, title = "Hello", artist = "Ann", path = "hello")
            val world = libraryTestSong(id = 2, title = "World", artist = "Bob", path = "world")
            repository.emit(listOf(hello, world))
            viewModel.onQueryChange("bob")
            viewModel.uiState.test {
                val state = awaitItemUntil { it.songs.size == 1 }
                assertThat(state.songs.single().path).isEqualTo("world")
                assertThat(state.playableCatalog.map { it.path }).containsExactly("hello", "world").inOrder()
                cancelAndIgnoreRemainingEvents()
            }

            viewModel.onSongClick(world)

            assertThat(
                controller.state.value.queue
                    .map { it.path },
            ).containsExactly("hello", "world").inOrder()
            assertThat(
                controller.state.value.currentSong
                    ?.path,
            ).isEqualTo("world")
        }

    @Test
    fun downloadActionEnqueuesSong() =
        runTest {
            val downloads = FakeDownloadRepository()
            val repository = FakeSongRepository()
            val viewModel = libraryViewModel(repository = repository, downloads = downloads)
            val song = libraryTestSong(title = "Alpha", path = "alpha")
            repository.emit(listOf(song))

            viewModel.onDownloadAction(song)

            assertThat(downloads.downloaded).containsExactly("alpha")
        }

    @Test
    fun completedDownloadActionDeletes() =
        runTest {
            val downloads = FakeDownloadRepository()
            downloads.emit(libraryTestDownload(path = "alpha"))
            val viewModel = libraryViewModel(downloads = downloads)
            val song = libraryTestSong(title = "Alpha", path = "alpha")
            viewModel.uiState.test {
                awaitItemUntil { it.status("alpha") == DownloadStatus.COMPLETED }
                cancelAndIgnoreRemainingEvents()
            }

            viewModel.onDownloadAction(song)

            assertThat(downloads.removed).containsExactly("alpha")
        }

    @Test
    fun offlineUnavailableSongDoesNotPlay() =
        runTest {
            val controller = FakePlayerController()
            val repository = FakeSongRepository()
            val viewModel =
                libraryViewModel(
                    repository = repository,
                    controller = controller,
                    network = FakeNetworkMonitor(initiallyOnline = false),
                )
            val song = libraryTestSong(title = "Alpha", path = "alpha")
            repository.emit(listOf(song))
            viewModel.uiState.test {
                awaitItemUntil { it.songs.isNotEmpty() && !it.isOnline }
                cancelAndIgnoreRemainingEvents()
            }

            val started = viewModel.onSongClick(song)

            assertThat(started).isFalse()
            assertThat(controller.state.value.currentSong).isNull()
        }

    @Test
    fun offlinePlayUsesOnlyDownloadedQueue() =
        runTest {
            val controller = FakePlayerController()
            val repository = FakeSongRepository()
            val downloads = FakeDownloadRepository()
            downloads.emit(libraryTestDownload(path = "beta"))
            val viewModel =
                libraryViewModel(
                    repository = repository,
                    controller = controller,
                    downloads = downloads,
                    network = FakeNetworkMonitor(initiallyOnline = false),
                )
            repository.emit(
                listOf(
                    libraryTestSong(id = 1, title = "Alpha", path = "alpha"),
                    libraryTestSong(id = 2, title = "Beta", path = "beta"),
                ),
            )
            viewModel.uiState.test {
                awaitItemUntil { it.songs.size == 2 && it.status("beta").name == "COMPLETED" }
                cancelAndIgnoreRemainingEvents()
            }

            val started = viewModel.onSongClick(libraryTestSong(id = 2, title = "Beta", path = "beta"))

            assertThat(started).isTrue()
            assertThat(
                controller.state.value.queue
                    .map { it.path },
            ).containsExactly("beta")
        }

    @Test
    fun searchFiltersViewButOfflinePlayUsesDownloadedCatalog() =
        runTest {
            val controller = FakePlayerController()
            val repository = FakeSongRepository()
            val downloads = FakeDownloadRepository()
            downloads.emit(
                libraryTestDownload(path = "hello"),
                libraryTestDownload(path = "world"),
            )
            val viewModel =
                libraryViewModel(
                    repository = repository,
                    controller = controller,
                    downloads = downloads,
                    network = FakeNetworkMonitor(initiallyOnline = false),
                )
            val hello = libraryTestSong(id = 1, title = "Hello", artist = "Ann", path = "hello")
            val world = libraryTestSong(id = 2, title = "World", artist = "Bob", path = "world")
            val extra = libraryTestSong(id = 3, title = "Extra", artist = "Ann", path = "extra")
            repository.emit(listOf(hello, world, extra))
            viewModel.onQueryChange("bob")
            viewModel.uiState.test {
                val state = awaitItemUntil { it.songs.size == 1 && it.status("hello").name == "COMPLETED" }
                assertThat(state.songs.single().path).isEqualTo("world")
                assertThat(state.playableCatalog.map { it.path }).containsExactly("hello", "world", "extra").inOrder()
                cancelAndIgnoreRemainingEvents()
            }

            val started = viewModel.onSongClick(world)

            assertThat(started).isTrue()
            assertThat(
                controller.state.value.queue
                    .map { it.path },
            ).containsExactly("hello", "world").inOrder()
        }
}

private fun libraryViewModel(
    repository: FakeSongRepository = FakeSongRepository(),
    prefs: FakeSortPreferences = FakeSortPreferences(),
    controller: FakePlayerController = FakePlayerController(),
    downloads: FakeDownloadRepository = FakeDownloadRepository(),
    network: FakeNetworkMonitor = FakeNetworkMonitor(),
) = LibraryViewModel(repository, prefs, controller, downloads, network)

private suspend fun ReceiveTurbine<LibraryUiState>.awaitItemUntil(predicate: (LibraryUiState) -> Boolean): LibraryUiState {
    while (true) {
        val item = awaitItem()
        if (predicate(item)) return item
    }
}
