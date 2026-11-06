package com.anhtuta.lilimusic.feature.downloads

import com.anhtuta.lilimusic.core.model.DownloadStatus
import com.anhtuta.lilimusic.core.model.SongDownload

fun testDownload(
    path: String = "hello-artist",
    title: String = "Hello",
    status: DownloadStatus = DownloadStatus.COMPLETED,
    progressPercent: Int = if (status == DownloadStatus.COMPLETED) 100 else 0,
): SongDownload =
    SongDownload(
        songPath = path,
        songId = 1L,
        title = title,
        artist = "Artist",
        streamUrl = "https://example.com/api/song/file?file=hello.mp3",
        fileName = "hello.mp3",
        imageUrl = null,
        lyricFileName = null,
        coverLocalUri = null,
        status = status,
        progressPercent = progressPercent,
        bytesDownloaded = 0L,
        errorMessage = if (status == DownloadStatus.FAILED) "disk" else null,
        updatedAtEpochMs = 1L,
    )
