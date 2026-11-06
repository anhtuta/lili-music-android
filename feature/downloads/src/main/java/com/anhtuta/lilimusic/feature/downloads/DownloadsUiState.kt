package com.anhtuta.lilimusic.feature.downloads

import com.anhtuta.lilimusic.core.model.SongDownload

data class DownloadsUiState(
    val items: List<SongDownload> = emptyList(),
    val confirmDeleteAll: Boolean = false,
) {
    val isEmpty: Boolean
        get() = items.isEmpty()
}
