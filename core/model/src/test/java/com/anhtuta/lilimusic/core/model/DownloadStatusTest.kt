package com.anhtuta.lilimusic.core.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DownloadStatusTest {
    @Test
    fun unknownStorageValueDefaultsToNone() {
        assertThat(DownloadStatus.fromStorage(null)).isEqualTo(DownloadStatus.NONE)
        assertThat(DownloadStatus.fromStorage("")).isEqualTo(DownloadStatus.NONE)
        assertThat(DownloadStatus.fromStorage("paused")).isEqualTo(DownloadStatus.NONE)
    }

    @Test
    fun parsesStoredNames() {
        assertThat(DownloadStatus.fromStorage("QUEUED")).isEqualTo(DownloadStatus.QUEUED)
        assertThat(DownloadStatus.fromStorage("DOWNLOADING")).isEqualTo(DownloadStatus.DOWNLOADING)
        assertThat(DownloadStatus.fromStorage("COMPLETED")).isEqualTo(DownloadStatus.COMPLETED)
        assertThat(DownloadStatus.fromStorage("FAILED")).isEqualTo(DownloadStatus.FAILED)
    }

    @Test
    fun queuedAndDownloadingAreActive() {
        assertThat(DownloadStatus.QUEUED.isActive).isTrue()
        assertThat(DownloadStatus.DOWNLOADING.isActive).isTrue()
        assertThat(DownloadStatus.COMPLETED.isActive).isFalse()
        assertThat(DownloadStatus.FAILED.isActive).isFalse()
        assertThat(DownloadStatus.NONE.isActive).isFalse()
    }
}
