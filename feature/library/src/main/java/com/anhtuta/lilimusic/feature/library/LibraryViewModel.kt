package com.anhtuta.lilimusic.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhtuta.lilimusic.core.data.network.NetworkMonitor
import com.anhtuta.lilimusic.core.data.repository.DownloadRepository
import com.anhtuta.lilimusic.core.data.repository.SongRepository
import com.anhtuta.lilimusic.core.datastore.SortPreferences
import com.anhtuta.lilimusic.core.model.DownloadStatus
import com.anhtuta.lilimusic.core.model.OfflinePlaybackPolicy
import com.anhtuta.lilimusic.core.model.Song
import com.anhtuta.lilimusic.core.model.SongCatalog
import com.anhtuta.lilimusic.core.model.SongDownload
import com.anhtuta.lilimusic.core.model.SongSort
import com.anhtuta.lilimusic.core.model.SortOrder
import com.anhtuta.lilimusic.core.playback.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class LibraryViewModel
    @Inject
    constructor(
        private val songRepository: SongRepository,
        private val sortPreferences: SortPreferences,
        private val playerController: PlayerController,
        private val downloadRepository: DownloadRepository,
        private val networkMonitor: NetworkMonitor,
    ) : ViewModel() {
        private val query = MutableStateFlow("")
        private val isRefreshing = MutableStateFlow(false)
        private val loadFailed = MutableStateFlow(false)
        private val initialLoadFinished = MutableStateFlow(false)

        val uiState: StateFlow<LibraryUiState> =
            combine(
                combine(
                    songRepository.observePlayableSongs(),
                    query,
                    sortPreferences.preferences,
                ) { songs, currentQuery, prefs ->
                    CatalogSnapshot(songs, currentQuery, prefs.sort, prefs.order)
                },
                combine(isRefreshing, loadFailed, initialLoadFinished, ::RefreshSnapshot),
                downloadRepository.observeDownloads(),
                networkMonitor.isOnline,
                ::toUiState,
            ).stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = LibraryUiState(),
            )

        init {
            refresh()
        }

        fun onQueryChange(value: String) {
            query.value = value
        }

        fun onSortSelected(sort: SongSort) {
            viewModelScope.launch {
                sortPreferences.setSort(sort, sort.defaultOrder)
                refreshCatalog()
            }
        }

        fun onSortOrderToggle() {
            viewModelScope.launch {
                val current = sortPreferences.preferences.first()
                sortPreferences.setSort(current.sort, current.order.toggle())
                refreshCatalog()
            }
        }

        fun onSortFieldChosen(sort: SongSort) {
            viewModelScope.launch {
                val current = sortPreferences.preferences.first()
                val order =
                    if (sort == current.sort) {
                        current.order.toggle()
                    } else {
                        sort.defaultOrder
                    }
                sortPreferences.setSort(sort, order)
                refreshCatalog()
            }
        }

        fun onRefresh() {
            viewModelScope.launch { refreshCatalog() }
        }

        fun onSongClick(song: Song): Boolean {
            val state = uiState.value
            if (!OfflinePlaybackPolicy.isPlayable(state.isOnline, state.status(song.path))) {
                return false
            }
            val catalog = state.playableCatalog.ifEmpty { state.songs }
            val completed = state.downloads.filterValues { it.isComplete }.keys
            val queue =
                OfflinePlaybackPolicy
                    .playQueue(catalog, state.isOnline, completed)
                    .map { item ->
                        OfflinePlaybackPolicy.withDisplayImage(item, state.downloads[item.path]?.coverLocalUri)
                    }
            playerController.play(queue, song.path)
            return true
        }

        fun onDownloadAction(song: Song) {
            viewModelScope.launch {
                when (uiState.value.status(song.path)) {
                    DownloadStatus.NONE, DownloadStatus.FAILED -> downloadRepository.download(song)
                    DownloadStatus.COMPLETED -> downloadRepository.remove(song.path)
                    DownloadStatus.QUEUED, DownloadStatus.DOWNLOADING -> downloadRepository.cancel(song.path)
                }
            }
        }

        private fun refresh() {
            viewModelScope.launch { refreshCatalog() }
        }

        private suspend fun refreshCatalog() {
            isRefreshing.value = true
            val prefs = sortPreferences.preferences.first()
            val result = songRepository.refresh(prefs.sort, prefs.order)
            loadFailed.value = result.isFailure
            initialLoadFinished.value = true
            isRefreshing.value = false
        }

        private data class CatalogSnapshot(
            val songs: List<Song>,
            val query: String,
            val sort: SongSort,
            val order: SortOrder,
        )

        private data class RefreshSnapshot(
            val refreshing: Boolean,
            val failed: Boolean,
            val finished: Boolean,
        )

        private fun toUiState(
            catalog: CatalogSnapshot,
            refresh: RefreshSnapshot,
            downloads: List<SongDownload>,
            isOnline: Boolean,
        ): LibraryUiState {
            val waiting = !refresh.finished && catalog.songs.isEmpty()
            val playableCatalog = catalog.songs
            return LibraryUiState(
                songs = SongCatalog.filter(playableCatalog, catalog.query),
                playableCatalog = playableCatalog,
                query = catalog.query,
                sort = catalog.sort,
                sortOrder = catalog.order,
                isLoading = waiting && !refresh.failed,
                isRefreshing = refresh.refreshing && !waiting,
                isOfflineError = refresh.failed,
                downloads = downloads.associateBy { it.songPath },
                isOnline = isOnline,
            )
        }
    }
