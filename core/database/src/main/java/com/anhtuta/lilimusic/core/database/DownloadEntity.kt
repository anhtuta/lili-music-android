package com.anhtuta.lilimusic.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anhtuta.lilimusic.core.model.DownloadStatus
import com.anhtuta.lilimusic.core.model.SongDownload
import java.io.File

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey val songPath: String,
    val songId: Long,
    val title: String,
    val artist: String,
    val streamUrl: String,
    val fileName: String,
    val imageUrl: String?,
    val lyricFileName: String?,
    val coverLocalPath: String?,
    val status: String,
    val progressPercent: Int,
    val bytesDownloaded: Long,
    val errorMessage: String?,
    val updatedAtEpochMs: Long,
)

fun DownloadEntity.toSongDownload(): SongDownload =
    SongDownload(
        songPath = songPath,
        songId = songId,
        title = title,
        artist = artist,
        streamUrl = streamUrl,
        fileName = fileName,
        imageUrl = imageUrl,
        lyricFileName = lyricFileName,
        coverLocalUri = coverLocalPath?.let { File(it).toURI().toString() },
        status = DownloadStatus.fromStorage(status),
        progressPercent = progressPercent,
        bytesDownloaded = bytesDownloaded,
        errorMessage = errorMessage,
        updatedAtEpochMs = updatedAtEpochMs,
    )

fun SongDownload.toEntity(): DownloadEntity =
    DownloadEntity(
        songPath = songPath,
        songId = songId,
        title = title,
        artist = artist,
        streamUrl = streamUrl,
        fileName = fileName,
        imageUrl = imageUrl,
        lyricFileName = lyricFileName,
        coverLocalPath = coverLocalUri?.toFilePath(),
        status = status.name,
        progressPercent = progressPercent,
        bytesDownloaded = bytesDownloaded,
        errorMessage = errorMessage,
        updatedAtEpochMs = updatedAtEpochMs,
    )

private fun String.toFilePath(): String =
    if (startsWith("file:")) {
        File(java.net.URI(this)).path
    } else {
        this
    }
