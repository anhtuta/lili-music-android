package com.anhtuta.lilimusic.core.network

import com.anhtuta.lilimusic.core.network.model.NetworkSong
import retrofit2.http.GET
import retrofit2.http.Query

interface LumenApi {
    @GET("api/song/all")
    suspend fun getSongs(
        @Query("sortBy") sortBy: String,
        @Query("sortOrder") sortOrder: String,
    ): List<NetworkSong>
}
