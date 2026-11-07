package com.anhtuta.lilimusic.feature.downloads

import com.anhtuta.lilimusic.core.data.repository.DownloadRepository
import com.anhtuta.lilimusic.core.model.Song
import com.anhtuta.lilimusic.core.model.SongDownload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeDownloadRepository : DownloadRepository {
    private val items = MutableStateFlow<List<SongDownload>>(emptyList())
    val downloaded = mutableListOf<String>()
    val retried = mutableListOf<String>()
    val removed = mutableListOf<String>()
    val cancelled = mutableListOf<String>()
    var removedAll = false

    override fun observeDownloads(): Flow<List<SongDownload>> = items

    override fun observeDownload(songPath: String): Flow<SongDownload?> = items.map { list -> list.find { it.songPath == songPath } }

    override fun observeCompletedPaths(): Flow<Set<String>> =
        items.map { list -> list.filter { it.isComplete }.map { it.songPath }.toSet() }

    override fun observeHasActiveDownloads(): Flow<Boolean> = items.map { list -> list.any { it.isActive } }

    override suspend fun download(song: Song) {
        downloaded += song.path
        emit(testDownload(path = song.path, title = song.title, status = com.anhtuta.lilimusic.core.model.DownloadStatus.QUEUED))
    }

    override suspend fun retry(song: Song) {
        retried += song.path
        downloaded += song.path
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
        removedAll = true
        items.value = emptyList()
    }

    fun emit(vararg downloads: SongDownload) {
        items.value = downloads.toList()
    }
}
