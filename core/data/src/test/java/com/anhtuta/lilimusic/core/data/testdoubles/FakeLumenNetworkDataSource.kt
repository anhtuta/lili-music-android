package com.anhtuta.lilimusic.core.data.testdoubles

import com.anhtuta.lilimusic.core.model.SongSort
import com.anhtuta.lilimusic.core.model.SortOrder
import com.anhtuta.lilimusic.core.network.LumenNetworkDataSource
import com.anhtuta.lilimusic.core.network.model.NetworkSong

class FakeLumenNetworkDataSource : LumenNetworkDataSource {
    var songs: List<NetworkSong> = emptyList()
    var error: Throwable? = null
    val requests = mutableListOf<Pair<SongSort, SortOrder>>()

    override suspend fun getSongs(
        sort: SongSort,
        order: SortOrder,
    ): List<NetworkSong> {
        requests += sort to order
        error?.let { throw it }
        return songs
    }
}
