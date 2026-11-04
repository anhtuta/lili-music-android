package com.nenmongvietnam.lilimusic.feature.library

import com.nenmongvietnam.lilimusic.core.data.repository.SongRepository
import com.nenmongvietnam.lilimusic.core.model.Song
import com.nenmongvietnam.lilimusic.core.model.SongSort
import com.nenmongvietnam.lilimusic.core.model.SortOrder
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSongRepository : SongRepository {
    private val songs = MutableStateFlow<List<Song>>(emptyList())
    val refreshCalls = mutableListOf<Pair<SongSort, SortOrder>>()
    var refreshResult: Result<Unit> = Result.success(Unit)
    var refreshGate: CompletableDeferred<Result<Unit>>? = null

    override fun observePlayableSongs(): Flow<List<Song>> = songs

    override suspend fun refresh(
        sort: SongSort,
        order: SortOrder,
    ): Result<Unit> {
        refreshCalls += sort to order
        val gate = refreshGate
        return if (gate != null) {
            gate.await()
        } else {
            refreshResult
        }
    }

    fun emit(value: List<Song>) {
        songs.value = value
    }
}
