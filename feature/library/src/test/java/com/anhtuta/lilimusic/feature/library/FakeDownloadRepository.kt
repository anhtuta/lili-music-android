package com.anhtuta.lilimusic.feature.library

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

    override fun observeDownloads(): Flow<List<SongDownload>> = items

    override fun observeDownload(songPath: String): Flow<SongDownload?> = items.map { list -> list.find { it.songPath == songPath } }

    override fun observeCompletedPaths(): Flow<Set<String>> =
        items.map { list -> list.filter { it.isComplete }.map { it.songPath }.toSet() }

    override fun observeHasActiveDownloads(): Flow<Boolean> = items.map { list -> list.any { it.isActive } }

    override suspend fun download(song: Song) {
        downloaded += song.path
        emit(
            libraryTestDownload(path = song.path, title = song.title, status = DownloadStatus.QUEUED),
        )
    }

    override suspend fun retry(song: Song) {
        download(song)
    }

    override suspend fun remove(songPath: String) {
        removed += songPath
        items.value = items.value.filterNot { it.songPath == songPath }
    }

    override suspend fun removeAll() {
        items.value = emptyList()
    }

    fun emit(vararg downloads: SongDownload) {
        items.value = downloads.toList()
    }
}

fun libraryTestDownload(
    path: String,
    title: String = "Hello",
    status: DownloadStatus = DownloadStatus.COMPLETED,
    progressPercent: Int = 0,
): SongDownload =
    SongDownload(
        songPath = path,
        songId = 1L,
        title = title,
        artist = "Artist",
        streamUrl = "https://example.com/api/song/file?file=$path.mp3",
        fileName = "$path.mp3",
        imageUrl = null,
        lyricFileName = null,
        coverLocalUri = null,
        status = status,
        progressPercent = progressPercent,
        bytesDownloaded = 0L,
        errorMessage = null,
        updatedAtEpochMs = 1L,
    )
