package com.nenmongvietnam.lilimusic.feature.library

import com.nenmongvietnam.lilimusic.core.model.Song
import com.nenmongvietnam.lilimusic.core.model.SongSort
import com.nenmongvietnam.lilimusic.core.model.SortOrder

data class LibraryUiState(
    val songs: List<Song> = emptyList(),
    val query: String = "",
    val sort: SongSort = SongSort.TITLE,
    val sortOrder: SortOrder = SortOrder.ASC,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isOfflineError: Boolean = false,
)
