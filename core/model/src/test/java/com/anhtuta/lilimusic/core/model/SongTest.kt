package com.anhtuta.lilimusic.core.model

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test

class SongTest {
    @Test
    fun playableSongKeepsFileName() {
        val song = testSong(fileName = "track.mp3")
        assertThat(song.fileName).isEqualTo("track.mp3")
    }

    @Test
    fun blankFileNameIsRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            testSong(fileName = "  ")
        }
    }

    @Test
    fun emptyFileNameIsRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            testSong(fileName = "")
        }
    }

    @Test
    fun isPlayableFileNameRejectsNullAndBlank() {
        assertThat(SongCatalog.isPlayableFileName(null)).isFalse()
        assertThat(SongCatalog.isPlayableFileName("")).isFalse()
        assertThat(SongCatalog.isPlayableFileName("   ")).isFalse()
        assertThat(SongCatalog.isPlayableFileName("song.mp3")).isTrue()
    }
}
