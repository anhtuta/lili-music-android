package com.anhtuta.lilimusic.core.data.download

interface CoverStore {
    suspend fun save(
        songPath: String,
        imageUrl: String,
    ): String?

    suspend fun delete(songPath: String)

    suspend fun deleteAll()
}
