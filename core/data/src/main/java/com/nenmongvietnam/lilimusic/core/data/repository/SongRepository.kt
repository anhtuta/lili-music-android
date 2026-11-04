package com.nenmongvietnam.lilimusic.core.data.repository

import com.nenmongvietnam.lilimusic.core.model.Song
import com.nenmongvietnam.lilimusic.core.model.SongSort
import com.nenmongvietnam.lilimusic.core.model.SortOrder
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun observePlayableSongs(): Flow<List<Song>>

    suspend fun refresh(
        sort: SongSort = SongSort.TITLE,
        order: SortOrder = sort.defaultOrder,
    ): Result<Unit>
}
