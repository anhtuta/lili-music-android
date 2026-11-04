package com.nenmongvietnam.lilimusic.feature.library

import com.nenmongvietnam.lilimusic.core.datastore.LibrarySortPreferences
import com.nenmongvietnam.lilimusic.core.datastore.SortPreferences
import com.nenmongvietnam.lilimusic.core.model.SongSort
import com.nenmongvietnam.lilimusic.core.model.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSortPreferences(
    initial: LibrarySortPreferences = LibrarySortPreferences(),
) : SortPreferences {
    private val state = MutableStateFlow(initial)
    override val preferences: Flow<LibrarySortPreferences> = state

    override suspend fun setSort(
        sort: SongSort,
        order: SortOrder,
    ) {
        state.value = LibrarySortPreferences(sort, order)
    }
}
