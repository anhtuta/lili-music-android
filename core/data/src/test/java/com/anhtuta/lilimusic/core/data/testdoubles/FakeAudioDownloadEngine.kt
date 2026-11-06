package com.anhtuta.lilimusic.core.data.testdoubles

import com.anhtuta.lilimusic.core.data.download.AudioDownloadEngine
import com.anhtuta.lilimusic.core.data.download.AudioDownloadSnapshot
import com.anhtuta.lilimusic.core.model.DownloadStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAudioDownloadEngine : AudioDownloadEngine {
    private val snapshots = MutableStateFlow<List<AudioDownloadSnapshot>>(emptyList())
    val downloadCalls = mutableListOf<Pair<String, String>>()
    val removed = mutableListOf<String>()
    var removedAll = false

    override fun observe(): Flow<List<AudioDownloadSnapshot>> = snapshots

    override fun download(
        id: String,
        streamUrl: String,
    ) {
        downloadCalls += id to streamUrl
        upsert(AudioDownloadSnapshot(id, DownloadStatus.QUEUED, 0, 0L, null))
    }

    override fun remove(id: String) {
        removed += id
        snapshots.value = snapshots.value.filterNot { it.id == id }
    }

    override fun removeAll() {
        removedAll = true
        snapshots.value = emptyList()
    }

    fun emitProgress(
        id: String,
        percent: Int,
        bytes: Long = percent * 10L,
    ) {
        upsert(AudioDownloadSnapshot(id, DownloadStatus.DOWNLOADING, percent, bytes, null))
    }

    fun emitCompleted(id: String) {
        upsert(AudioDownloadSnapshot(id, DownloadStatus.COMPLETED, 100, 1_000L, null))
    }

    fun emitFailed(
        id: String,
        message: String = "failed",
    ) {
        upsert(AudioDownloadSnapshot(id, DownloadStatus.FAILED, 0, 0L, message))
    }

    private fun upsert(snapshot: AudioDownloadSnapshot) {
        snapshots.value = snapshots.value.filterNot { it.id == snapshot.id } + snapshot
    }
}
