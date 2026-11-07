package com.anhtuta.lilimusic.feature.player

import com.anhtuta.lilimusic.core.data.repository.DownloadRepository
import com.anhtuta.lilimusic.core.model.DownloadStatus
import com.anhtuta.lilimusic.core.model.Song
import com.anhtuta.lilimusic.core.model.SongDownload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeDownloadRepository : DownloadRepository {
    private val items = MutableStateFlow<List<SongDownload>>(emptyList())
    val downloaded = mutableListOf<String>()
    val removed = mutableListOf<String>()
    val cancelled = mutableListOf<String>()

    override fun observeDownloads(): Flow<List<SongDownload>> = items

    override fun observeDownload(songPath: String): Flow<SongDownload?> = items.map { list -> list.find { it.songPath == songPath } }

    override fun observeCompletedPaths(): Flow<Set<String>> =
        items.map { list -> list.filter { it.isComplete }.map { it.songPath }.toSet() }

    override fun observeHasActiveDownloads(): Flow<Boolean> = items.map { list -> list.any { it.isActive } }

    override suspend fun download(song: Song) {
        downloaded += song.path
        emit(playerTestDownload(path = song.path, status = DownloadStatus.QUEUED))
    }

    override suspend fun retry(song: Song) {
        download(song)
    }

    override suspend fun remove(songPath: String) {
        removed += songPath
        items.value = items.value.filterNot { it.songPath == songPath }
    }

    override suspend fun cancel(songPath: String) {
        cancelled += songPath
        remove(songPath)
    }

    override suspend fun removeAll() {
        items.value = emptyList()
    }

    fun emit(vararg downloads: SongDownload) {
        items.value = downloads.toList()
    }
}

class FakeNetworkMonitor(
    initiallyOnline: Boolean = true,
) : com.anhtuta.lilimusic.core.data.network.NetworkMonitor {
    private val online = MutableStateFlow(initiallyOnline)
    override val isOnline: Flow<Boolean> = online
}

fun playerTestDownload(
    path: String,
    status: DownloadStatus = DownloadStatus.COMPLETED,
    coverLocalUri: String? = null,
): SongDownload =
    SongDownload(
        songPath = path,
        songId = 1L,
        title = path,
        artist = "Artist",
        streamUrl = "https://example.com/api/song/file?file=$path.mp3",
        fileName = "$path.mp3",
        imageUrl = null,
        lyricFileName = null,
        coverLocalUri = coverLocalUri,
        status = status,
        progressPercent = if (status == DownloadStatus.COMPLETED) 100 else 0,
        bytesDownloaded = 0L,
        errorMessage = null,
        updatedAtEpochMs = 1L,
    )
