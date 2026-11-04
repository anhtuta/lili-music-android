package com.nenmongvietnam.lilimusic.feature.library

import com.nenmongvietnam.lilimusic.core.model.SongSort

object LibraryTestTags {
    const val LIST = "library-list"
    const val SEARCH = "library-search"
    const val LOADING = "library-loading"
    const val ERROR = "library-error"
    const val EMPTY = "library-empty"
    const val RETRY = "library-retry"
    const val SORT_ORDER = "library-sort-order"
    const val ERROR_BANNER = "library-error-banner"

    fun song(id: Long): String = "library-song-$id"

    fun sort(sort: SongSort): String = "library-sort-${sort.name.lowercase()}"
}
