package com.anhtuta.lilimusic.core.data.testdoubles

import com.anhtuta.lilimusic.core.model.SongSort
import com.anhtuta.lilimusic.core.model.SortOrder
import com.anhtuta.lilimusic.core.network.LumenNetworkDataSource
import com.anhtuta.lilimusic.core.network.LyricNetworkResult
import com.anhtuta.lilimusic.core.network.model.NetworkSong

class FakeLumenNetworkDataSource : LumenNetworkDataSource {
    var songs: List<NetworkSong> = emptyList()
    var error: Throwable? = null
    var lyricError: Throwable? = null
    var lyrics: MutableMap<String, LyricNetworkResult> = mutableMapOf()
    val requests = mutableListOf<Pair<SongSort, SortOrder>>()
    val recordedPaths = mutableListOf<String>()
    val lyricRequests = mutableListOf<String>()

    override suspend fun getSongs(
        sort: SongSort,
        order: SortOrder,
    ): List<NetworkSong> {
        requests += sort to order
        error?.let { throw it }
        return songs
    }

    override suspend fun recordListen(path: String) {
        recordedPaths += path
        error?.let { throw it }
    }

    override suspend fun getLyric(fileName: String): LyricNetworkResult {
        lyricRequests += fileName
        lyricError?.let { throw it }
        return lyrics[fileName] ?: LyricNetworkResult.Missing
    }
}
