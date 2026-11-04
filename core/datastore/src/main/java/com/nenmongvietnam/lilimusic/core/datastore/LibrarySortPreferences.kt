package com.nenmongvietnam.lilimusic.core.datastore

import com.nenmongvietnam.lilimusic.core.model.SongSort
import com.nenmongvietnam.lilimusic.core.model.SortOrder

data class LibrarySortPreferences(
    val sort: SongSort = SongSort.TITLE,
    val order: SortOrder = SongSort.TITLE.defaultOrder,
)

object LibrarySortPreferencesParser {
    fun parse(
        sortBy: String?,
        sortOrder: String?,
    ): LibrarySortPreferences {
        val sort = sortBy?.let(SongSort::fromApiValue) ?: SongSort.TITLE
        val order = SortOrder.entries.find { it.apiValue == sortOrder } ?: sort.defaultOrder
        return LibrarySortPreferences(sort = sort, order = order)
    }
}
