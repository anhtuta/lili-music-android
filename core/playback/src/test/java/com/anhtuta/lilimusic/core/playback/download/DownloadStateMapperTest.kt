@file:OptIn(UnstableApi::class)

package com.anhtuta.lilimusic.core.playback.download

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import com.anhtuta.lilimusic.core.model.DownloadStatus
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DownloadStateMapperTest {
    @Test
    fun mapsMedia3States() {
        assertThat(DownloadStateMapper.status(Download.STATE_QUEUED)).isEqualTo(DownloadStatus.QUEUED)
        assertThat(DownloadStateMapper.status(Download.STATE_STOPPED)).isEqualTo(DownloadStatus.QUEUED)
        assertThat(DownloadStateMapper.status(Download.STATE_DOWNLOADING)).isEqualTo(DownloadStatus.DOWNLOADING)
        assertThat(DownloadStateMapper.status(Download.STATE_RESTARTING)).isEqualTo(DownloadStatus.DOWNLOADING)
        assertThat(DownloadStateMapper.status(Download.STATE_COMPLETED)).isEqualTo(DownloadStatus.COMPLETED)
        assertThat(DownloadStateMapper.status(Download.STATE_FAILED)).isEqualTo(DownloadStatus.FAILED)
        assertThat(DownloadStateMapper.status(Download.STATE_REMOVING)).isEqualTo(DownloadStatus.NONE)
    }

    @Test
    fun unknownPercentIsZero() {
        assertThat(DownloadStateMapper.percent(-1f)).isEqualTo(0)
        assertThat(DownloadStateMapper.percent(42.9f)).isEqualTo(42)
        assertThat(DownloadStateMapper.percent(120f)).isEqualTo(100)
    }
}
