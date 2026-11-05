package com.anhtuta.lilimusic.core.network

import com.anhtuta.lilimusic.core.model.LyricFormat
import com.anhtuta.lilimusic.core.model.SongSort
import com.anhtuta.lilimusic.core.model.SortOrder
import com.anhtuta.lilimusic.core.network.model.NetworkSong
import javax.inject.Inject
import javax.inject.Singleton
import retrofit2.HttpException

interface LumenNetworkDataSource {
    suspend fun getSongs(
        sort: SongSort,
        order: SortOrder,
    ): List<NetworkSong>

    suspend fun recordListen(path: String)

    suspend fun getLyric(fileName: String): LyricNetworkResult
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

        override suspend fun recordListen(path: String) {
            val response = lumenApi.recordListen(path)
            if (!response.isSuccessful) {
                throw HttpException(response)
            }
        }

        override suspend fun getLyric(fileName: String): LyricNetworkResult {
            val response = lumenApi.getLyric(fileName)
            if (!response.isSuccessful) {
                throw HttpException(response)
            }
            val text = response.body()?.string().orEmpty()
            return if (LyricFormat.isMissingBody(text)) {
                LyricNetworkResult.Missing
            } else {
                LyricNetworkResult.Body(text)
            }
        }
    }
