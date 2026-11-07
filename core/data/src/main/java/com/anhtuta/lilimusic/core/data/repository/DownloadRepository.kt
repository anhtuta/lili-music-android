package com.anhtuta.lilimusic.core.data.repository

import com.anhtuta.lilimusic.core.model.Song
import com.anhtuta.lilimusic.core.model.SongDownload
import kotlinx.coroutines.flow.Flow

interface DownloadRepository {
    fun observeDownloads(): Flow<List<SongDownload>>

    fun observeDownload(songPath: String): Flow<SongDownload?>

    fun observeCompletedPaths(): Flow<Set<String>>

    fun observeHasActiveDownloads(): Flow<Boolean>

    suspend fun download(song: Song)

    suspend fun retry(song: Song)

    suspend fun remove(songPath: String)

    suspend fun cancel(songPath: String)

    suspend fun removeAll()
}
