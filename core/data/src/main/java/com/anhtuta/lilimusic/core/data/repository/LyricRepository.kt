package com.anhtuta.lilimusic.core.data.repository

interface LyricRepository {
    suspend fun loadLyric(fileName: String?): LyricLoadResult

    suspend fun deleteCached(fileName: String?)
}
