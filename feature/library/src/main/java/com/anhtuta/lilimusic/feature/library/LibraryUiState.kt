package com.anhtuta.lilimusic.feature.library

import com.anhtuta.lilimusic.core.model.DownloadStatus
import com.anhtuta.lilimusic.core.model.OfflinePlaybackPolicy
import com.anhtuta.lilimusic.core.model.Song
import com.anhtuta.lilimusic.core.model.SongDownload
import com.anhtuta.lilimusic.core.model.SongSort
import com.anhtuta.lilimusic.core.model.SortOrder

data class LibraryUiState(
    val songs: List<Song> = emptyList(),
    val query: String = "",
    val sort: SongSort = SongSort.TITLE,
    val sortOrder: SortOrder = SortOrder.ASC,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isOfflineError: Boolean = false,
    val downloads: Map<String, SongDownload> = emptyMap(),
    val isOnline: Boolean = true,
) {
    fun status(path: String): DownloadStatus = downloads[path]?.status ?: DownloadStatus.NONE

    fun isUnavailableOffline(path: String): Boolean = OfflinePlaybackPolicy.isUnavailableOffline(isOnline, status(path))

    fun coverUrl(song: Song): String? = OfflinePlaybackPolicy.displayImageUrl(song.imageUrl, downloads[song.path]?.coverLocalUri)
}
