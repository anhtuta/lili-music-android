package com.anhtuta.lilimusic.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anhtuta.lilimusic.core.data.repository.SongRepository
import com.anhtuta.lilimusic.core.datastore.SortPreferences
import com.anhtuta.lilimusic.core.model.Song
import com.anhtuta.lilimusic.core.model.SongCatalog
import com.anhtuta.lilimusic.core.model.SongSort
import com.anhtuta.lilimusic.core.model.SortOrder
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
            }
        }

        fun onSortOrderToggle() {
            viewModelScope.launch {
                val current = sortPreferences.preferences.first()
                sortPreferences.setSort(current.sort, current.order.toggle())
            }
        }

        fun onRefresh() {
            refresh()
        }

        private fun refresh() {
            viewModelScope.launch {
                isRefreshing.value = true
                val prefs = sortPreferences.preferences.first()
                val result = songRepository.refresh(prefs.sort, prefs.order)
                loadFailed.value = result.isFailure
                initialLoadFinished.value = true
                isRefreshing.value = false
            }
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
        ): LibraryUiState {
            val waiting = !refresh.finished && catalog.songs.isEmpty()
            return LibraryUiState(
                songs =
                    SongCatalog.filterAndSort(
                        songs = catalog.songs,
                        query = catalog.query,
                        sort = catalog.sort,
                        order = catalog.order,
                    ),
                query = catalog.query,
                sort = catalog.sort,
                sortOrder = catalog.order,
                isLoading = waiting && !refresh.failed,
                isRefreshing = refresh.refreshing && !waiting,
                isOfflineError = refresh.failed,
            )
        }
    }
