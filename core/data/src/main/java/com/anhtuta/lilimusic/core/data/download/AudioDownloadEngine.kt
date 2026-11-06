package com.anhtuta.lilimusic.core.data.download

import com.anhtuta.lilimusic.core.model.DownloadStatus
import kotlinx.coroutines.flow.Flow

data class AudioDownloadSnapshot(
    val id: String,
    val status: DownloadStatus,
    val progressPercent: Int,
    val bytesDownloaded: Long,
    val errorMessage: String?,
)

interface AudioDownloadEngine {
    fun observe(): Flow<List<AudioDownloadSnapshot>>

    fun download(
        id: String,
        streamUrl: String,
    )

    fun remove(id: String)

    fun removeAll()
}
