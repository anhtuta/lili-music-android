package com.anhtuta.lilimusic.core.network

import com.anhtuta.lilimusic.core.network.model.NetworkSong
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface LumenApi {
    @GET("api/song/all")
    suspend fun getSongs(
        @Query("sortBy") sortBy: String,
        @Query("sortOrder") sortOrder: String,
    ): List<NetworkSong>

    @PUT("api/song/listens")
    suspend fun recordListen(
        @Query("path") path: String,
    ): Response<ResponseBody>
}
