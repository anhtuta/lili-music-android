package com.anhtuta.lilimusic.core.playback

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SongMediaItemsTest {
    @Test
    fun mapsPathAsMediaIdAndStreamUrlAsUri() {
        val song =
            playbackTestSong(
                id = 9,
                title = "Hello",
                path = "hello-artist",
                streamUrl = "https://example.com/api/song/file?file=Hello%20Song.mp3",
            )

        val item = SongMediaItems.from(song)

        assertThat(item.mediaId).isEqualTo("hello-artist")
        assertThat(item.localConfiguration?.uri.toString())
            .isEqualTo("https://example.com/api/song/file?file=Hello%20Song.mp3")
        assertThat(item.mediaMetadata.title.toString()).isEqualTo("Hello")
        assertThat(item.mediaMetadata.artist.toString()).isEqualTo("Artist")
    }
}
