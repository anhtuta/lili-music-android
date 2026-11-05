package com.anhtuta.lilimusic.core.network

import com.google.common.truth.Truth.assertThat
import com.anhtuta.lilimusic.core.network.model.NetworkSong
import com.anhtuta.lilimusic.core.network.model.toPlayableSong
import org.junit.Test

class NetworkSongMapperTest {
    private val baseUrl = "https://blog.nenmongvietnam.com/laravel/public"

    @Test
    fun relativeImageUrlIsPrefixedAndStreamUrlIsEncoded() {
        val song =
            NetworkSong(
                id = 1,
                title = "Hello",
                artist = "Artist",
                path = "hello",
                fileName = "Charlie Puth - Attention.mp3",
                imageUrl = "/api/song/picture?file=cover.jpg",
                lyric = "hello.lrc",
            ).toPlayableSong(baseUrl)

        checkNotNull(song)
        assertThat(song.imageUrl).isEqualTo("$baseUrl/api/song/picture?file=cover.jpg")
        assertThat(song.streamUrl).isEqualTo(
            "$baseUrl/api/song/file?file=Charlie%20Puth%20-%20Attention.mp3",
        )
        assertThat(song.lyric?.fileName).isEqualTo("hello.lrc")
    }

    @Test
    fun absoluteImageUrlIsKept() {
        val song =
            NetworkSong(
                id = 2,
                title = "Abs",
                path = "abs",
                fileName = "song.mp3",
                imageUrl = "https://cdn.example/cover.jpg",
            ).toPlayableSong(baseUrl)

        assertThat(song?.imageUrl).isEqualTo("https://cdn.example/cover.jpg")
    }

    @Test
    fun nullImageUrlStaysNull() {
        val song =
            NetworkSong(
                id = 3,
                title = "None",
                path = "none",
                fileName = "none.mp3",
                imageUrl = null,
            ).toPlayableSong(baseUrl)

        assertThat(song?.imageUrl).isNull()
    }

    @Test
    fun zingOnlySongWithNullFileNameIsDropped() {
        val song =
            NetworkSong(
                id = 44,
                title = "Zing",
                path = "zing",
                fileName = null,
                zingId = "ZWADZBB9",
            ).toPlayableSong(baseUrl)

        assertThat(song).isNull()
    }

    @Test
    fun blankFileNameIsDropped() {
        val song =
            NetworkSong(
                id = 5,
                title = "Blank",
                path = "blank",
                fileName = "  ",
            ).toPlayableSong(baseUrl)

        assertThat(song).isNull()
    }
}
