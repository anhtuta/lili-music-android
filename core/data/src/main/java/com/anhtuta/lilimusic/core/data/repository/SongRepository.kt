package com.anhtuta.lilimusic.core.data.repository

import com.anhtuta.lilimusic.core.model.Song
import com.anhtuta.lilimusic.core.model.SongSort
import com.anhtuta.lilimusic.core.model.SortOrder
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun observePlayableSongs(): Flow<List<Song>>

    suspend fun refresh(
        sort: SongSort = SongSort.TITLE,
        order: SortOrder = sort.defaultOrder,
    ): Result<Unit>
}
