package com.anhtuta.lilimusic.core.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class OfflinePlaybackPolicyTest {
    @Test
    fun playableWhenOnlineEvenIfNotDownloaded() {
        assertThat(OfflinePlaybackPolicy.isPlayable(isOnline = true, status = DownloadStatus.NONE)).isTrue()
        assertThat(OfflinePlaybackPolicy.isPlayable(isOnline = true, status = DownloadStatus.FAILED)).isTrue()
    }

    @Test
    fun playableOfflineOnlyWhenCompleted() {
        assertThat(OfflinePlaybackPolicy.isPlayable(isOnline = false, status = DownloadStatus.COMPLETED)).isTrue()
        assertThat(OfflinePlaybackPolicy.isPlayable(isOnline = false, status = DownloadStatus.NONE)).isFalse()
        assertThat(OfflinePlaybackPolicy.isPlayable(isOnline = false, status = DownloadStatus.DOWNLOADING)).isFalse()
        assertThat(OfflinePlaybackPolicy.isPlayable(isOnline = false, status = DownloadStatus.FAILED)).isFalse()
    }

    @Test
    fun unavailableOfflineMarksNonCompletedRows() {
        assertThat(
            OfflinePlaybackPolicy.isUnavailableOffline(isOnline = false, status = DownloadStatus.NONE),
        ).isTrue()
        assertThat(
            OfflinePlaybackPolicy.isUnavailableOffline(isOnline = false, status = DownloadStatus.COMPLETED),
        ).isFalse()
        assertThat(
            OfflinePlaybackPolicy.isUnavailableOffline(isOnline = true, status = DownloadStatus.NONE),
        ).isFalse()
    }

    @Test
    fun onlinePlayQueueKeepsFullCatalogOrder() {
        val catalog = listOf(testSong(path = "a"), testSong(id = 2, path = "b"), testSong(id = 3, path = "c"))

        val queue =
            OfflinePlaybackPolicy.playQueue(
                catalog = catalog,
                isOnline = true,
                completedPaths = setOf("b"),
            )

        assertThat(queue.map { it.path }).containsExactly("a", "b", "c").inOrder()
    }

    @Test
    fun offlinePlayQueueKeepsOnlyCompletedDownloads() {
        val songs = listOf(testSong(path = "a"), testSong(id = 2, path = "b"), testSong(id = 3, path = "c"))

        val queue =
            OfflinePlaybackPolicy.playQueue(
                catalog = songs,
                isOnline = false,
                completedPaths = setOf("b", "c"),
            )

        assertThat(queue.map { it.path }).containsExactly("b", "c").inOrder()
    }

    @Test
    fun displayImageUrlPrefersLocalCover() {
        assertThat(
            OfflinePlaybackPolicy.displayImageUrl(
                remoteImageUrl = "https://example.com/cover.jpg",
                localCoverUri = "file:///data/covers/a.jpg",
            ),
        ).isEqualTo("file:///data/covers/a.jpg")
        assertThat(
            OfflinePlaybackPolicy.displayImageUrl(
                remoteImageUrl = "https://example.com/cover.jpg",
                localCoverUri = null,
            ),
        ).isEqualTo("https://example.com/cover.jpg")
        assertThat(
            OfflinePlaybackPolicy.displayImageUrl(
                remoteImageUrl = null,
                localCoverUri = null,
            ),
        ).isNull()
    }

    @Test
    fun withDisplayImageCopiesLocalCoverOntoSong() {
        val song = testSong(imageUrl = "https://example.com/cover.jpg")

        val updated = OfflinePlaybackPolicy.withDisplayImage(song, "file:///covers/a.jpg")

        assertThat(updated.imageUrl).isEqualTo("file:///covers/a.jpg")
        assertThat(updated.path).isEqualTo(song.path)
    }
}
