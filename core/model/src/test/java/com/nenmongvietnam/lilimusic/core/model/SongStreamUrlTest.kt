package com.nenmongvietnam.lilimusic.core.model

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test

class SongStreamUrlTest {
    private val baseUrl = "https://blog.nenmongvietnam.com/laravel/public"

    @Test
    fun encodesSpacesAsPercentTwenty() {
        val url = SongStreamUrl.build(baseUrl, "Charlie Puth - Attention.mp3")
        assertThat(url).isEqualTo(
            "$baseUrl/api/song/file?file=Charlie%20Puth%20-%20Attention.mp3",
        )
    }

    @Test
    fun encodesUnicodeFileName() {
        val url = SongStreamUrl.build(baseUrl, "新樂塵符-賀子玲 - 123我愛你.mp3")
        assertThat(url).startsWith("$baseUrl/api/song/file?file=")
        assertThat(url).doesNotContain(" ")
        assertThat(url).doesNotContain("+")
        assertThat(url).contains("%")
    }

    @Test
    fun trimsTrailingSlashOnBaseUrl() {
        val url = SongStreamUrl.build("$baseUrl/", "song.mp3")
        assertThat(url).isEqualTo("$baseUrl/api/song/file?file=song.mp3")
    }

    @Test
    fun rejectsBlankFileName() {
        assertThrows(IllegalArgumentException::class.java) {
            SongStreamUrl.build(baseUrl, "")
        }
    }
}
