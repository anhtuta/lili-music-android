package com.anhtuta.lilimusic.feature.library

import androidx.compose.foundation.lazy.LazyListState
import com.anhtuta.lilimusic.core.model.Song

internal object LibraryListScroll {
    private const val HEADER_ITEMS = 3

    fun listIndexForSong(
        songs: List<Song>,
        songPath: String,
        showOfflineBanner: Boolean,
    ): Int? {
        if (songs.isEmpty()) return null
        val songIndex = songs.indexOfFirst { it.path == songPath }
        if (songIndex < 0) return null
        var listIndex = HEADER_ITEMS
        if (showOfflineBanner) {
            listIndex++
        }
        return listIndex + songIndex
    }
}

internal suspend fun LazyListState.scrollToNowPlayingSong(state: LibraryUiState) {
    val path = state.nowPlayingPath ?: return
    val index =
        LibraryListScroll.listIndexForSong(
            songs = state.songs,
            songPath = path,
            showOfflineBanner = state.isOfflineError,
        ) ?: return
    scrollToItem(index)
}
