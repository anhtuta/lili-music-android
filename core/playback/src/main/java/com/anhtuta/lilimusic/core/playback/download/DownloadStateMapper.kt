@file:OptIn(UnstableApi::class)

package com.anhtuta.lilimusic.core.playback.download

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import com.anhtuta.lilimusic.core.data.download.AudioDownloadSnapshot
import com.anhtuta.lilimusic.core.model.DownloadStatus

object DownloadStateMapper {
    fun status(state: Int): DownloadStatus =
        when (state) {
            Download.STATE_COMPLETED -> DownloadStatus.COMPLETED
            Download.STATE_DOWNLOADING, Download.STATE_RESTARTING -> DownloadStatus.DOWNLOADING
            Download.STATE_FAILED -> DownloadStatus.FAILED
            Download.STATE_QUEUED, Download.STATE_STOPPED -> DownloadStatus.QUEUED
            else -> DownloadStatus.NONE
        }

    fun percent(percentDownloaded: Float): Int =
        if (percentDownloaded < 0f) {
            0
        } else {
            percentDownloaded.toInt().coerceIn(0, 100)
        }

    fun snapshot(download: Download): AudioDownloadSnapshot =
        AudioDownloadSnapshot(
            id = download.request.id,
            status = status(download.state),
            progressPercent = percent(download.percentDownloaded),
            bytesDownloaded = download.bytesDownloaded,
            errorMessage =
                if (download.state == Download.STATE_FAILED) {
                    "download failed (${download.failureReason})"
                } else {
                    null
                },
        )
}
