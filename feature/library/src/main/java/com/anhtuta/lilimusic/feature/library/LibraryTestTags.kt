package com.anhtuta.lilimusic.feature.library

import com.anhtuta.lilimusic.core.model.SongSort

object LibraryTestTags {
    const val HEADER = "library-header"
    const val LIST = "library-list"
    const val SCROLLBAR = "library-scrollbar"
    const val SCROLLBAR_THUMB = "library-scrollbar-thumb"
    const val SEARCH = "library-search"
    const val LOADING = "library-loading"
    const val ERROR = "library-error"
    const val EMPTY = "library-empty"
    const val RETRY = "library-retry"
    const val SORT_ORDER = "library-sort-order"
    const val SORT_PILL = "library-sort-pill"
    const val SORT_MENU = "library-sort-menu"
    const val ERROR_BANNER = "library-error-banner"
    const val SETTINGS = "library-settings"
    const val DOWNLOADS = "library-downloads"
    const val REFRESH = "library-refresh"
    const val CLEAR_SEARCH = "library-clear-search"

    const val COVER_PREVIEW = "library-cover-preview"
    const val SONG_INFO = "library-song-info"
    const val MORE_MENU = "library-more-menu"

    fun song(id: Long): String = "library-song-$id"

    fun cover(id: Long): String = "library-cover-$id"

    fun playbackBadge(id: Long): String = "library-playback-badge-$id"

    fun nowPlayingRow(id: Long): String = "library-now-playing-row-$id"

    fun listens(id: Long): String = "library-listens-$id"

    fun soy(id: Long): String = "library-soy-$id"

    fun type(id: Long): String = "library-type-$id"

    fun date(id: Long): String = "library-date-$id"

    fun download(id: Long): String = "library-download-$id"

    fun more(id: Long): String = "library-more-$id"

    fun unavailable(id: Long): String = "library-unavailable-$id"

    fun sort(sort: SongSort): String = "library-sort-${sort.name.lowercase()}"
}
