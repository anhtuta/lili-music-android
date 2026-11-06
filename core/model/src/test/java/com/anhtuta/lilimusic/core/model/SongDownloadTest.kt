package com.anhtuta.lilimusic.core.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SongDownloadTest {
    @Test
    fun queuedCopiesCatalogFields() {
        val song = testSong(path = "hello-artist", title = "Hello")

        val download = SongDownload.queued(song, updatedAtEpochMs = 42L)

        assertThat(download.songPath).isEqualTo("hello-artist")
        assertThat(download.status).isEqualTo(DownloadStatus.QUEUED)
        assertThat(download.progressPercent).isEqualTo(0)
        assertThat(download.lyricFileName).isEqualTo("hello.lrc")
        assertThat(download.updatedAtEpochMs).isEqualTo(42L)
        assertThat(download.isActive).isTrue()
        assertThat(download.isComplete).isFalse()
    }

    @Test
    fun toSongRebuildsPlayableSongUsingLocalCover() {
        val download =
            SongDownload.queued(testSong(), updatedAtEpochMs = 1L).copy(
                status = DownloadStatus.COMPLETED,
                coverLocalUri = "file:///covers/hello.jpg",
            )

        val song = download.toSong()

        assertThat(song.path).isEqualTo("hello-artist")
        assertThat(song.fileName).isEqualTo("hello.mp3")
        assertThat(song.imageUrl).isEqualTo("file:///covers/hello.jpg")
        assertThat(song.lyric?.fileName).isEqualTo("hello.lrc")
        assertThat(song.streamUrl).isEqualTo("https://example.com/api/song/file?file=hello.mp3")
    }
}
