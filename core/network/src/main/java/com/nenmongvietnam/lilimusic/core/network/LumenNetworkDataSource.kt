package com.nenmongvietnam.lilimusic.core.network

import com.nenmongvietnam.lilimusic.core.model.SongSort
import com.nenmongvietnam.lilimusic.core.model.SortOrder
import com.nenmongvietnam.lilimusic.core.network.model.NetworkSong
import javax.inject.Inject
import javax.inject.Singleton

interface LumenNetworkDataSource {
    suspend fun getSongs(
        sort: SongSort,
        order: SortOrder,
    ): List<NetworkSong>
}

@Singleton
class RetrofitLumenNetworkDataSource
    @Inject
    constructor(
        private val lumenApi: LumenApi,
    ) : LumenNetworkDataSource {
        override suspend fun getSongs(
            sort: SongSort,
            order: SortOrder,
        ): List<NetworkSong> = lumenApi.getSongs(sort.apiValue, order.apiValue)
    }
