package com.anhtuta.lilimusic.feature.library

import com.anhtuta.lilimusic.core.data.repository.SongRepository
import com.anhtuta.lilimusic.core.model.Song
import com.anhtuta.lilimusic.core.model.SongSort
import com.anhtuta.lilimusic.core.model.SortOrder
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeSongRepository : SongRepository {
    private val songs = MutableStateFlow<List<Song>>(emptyList())
    val refreshCalls = mutableListOf<Pair<SongSort, SortOrder>>()
    var refreshResult: Result<Unit> = Result.success(Unit)
    var refreshGate: CompletableDeferred<Result<Unit>>? = null

    val recordedListens = mutableListOf<String>()
    var recordListenResult: Result<Unit> = Result.success(Unit)

    override fun observePlayableSongs(): Flow<List<Song>> = songs

    override fun observeSongByPath(path: String): Flow<Song?> = songs.map { list -> list.find { it.path == path } }

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

    override suspend fun recordListen(path: String): Result<Unit> {
        recordedListens += path
        return recordListenResult
    }

    fun emit(value: List<Song>) {
        songs.value = value
    }
}
