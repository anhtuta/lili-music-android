package com.anhtuta.lilimusic.feature.library

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SongInfoFieldsTest {
    @Test
    fun missingValuesBecomeNone() {
        assertThat(SongInfoFields.display(null as String?)).isEqualTo("None")
        assertThat(SongInfoFields.display("")).isEqualTo("None")
        assertThat(SongInfoFields.display("  ")).isEqualTo("None")
        assertThat(SongInfoFields.display(null as Int?)).isEqualTo("None")
        assertThat(SongInfoFields.display(2018)).isEqualTo("2018")
    }

    @Test
    fun lyricTypeUsesFileExtension() {
        assertThat(SongInfoFields.lyricType(null)).isEqualTo("None")
        assertThat(SongInfoFields.lyricType("")).isEqualTo("None")
        assertThat(SongInfoFields.lyricType("hello.lrc")).isEqualTo("lrc")
        assertThat(SongInfoFields.lyricType("hello.trc")).isEqualTo("trc")
        assertThat(SongInfoFields.lyricType("hello.xlrc")).isEqualTo("xlrc")
        assertThat(SongInfoFields.lyricType("hello.xtrc")).isEqualTo("xtrc")
    }

    @Test
    fun rowsCoverEverySongFieldIncludingMissingSoy() {
        val song = libraryTestSong(id = 7, title = "Alpha", songOfTheYear = null)
        val labels =
            SongInfoLabels(
                title = "Title",
                artist = "Artist",
                album = "Album",
                type = "Type",
                songOfTheYear = "Song of the year",
                lyricType = "Lyric type",
                lyricFile = "Lyric file",
                listens = "Listens",
                date = "Date",
                createdDate = "Created date",
                fileName = "File name",
                path = "Path",
                id = "Id",
                zingId = "Zing id",
                imageName = "Image name",
                imageUrl = "Image URL",
                streamUrl = "Stream URL",
            )
        val rows = SongInfoFields.rows(song, labels)
        assertThat(rows.map { it.label }).containsExactly(
            "Title",
            "Artist",
            "Album",
            "Type",
            "Song of the year",
            "Lyric type",
            "Lyric file",
            "Listens",
            "Date",
            "Created date",
            "File name",
            "Path",
            "Id",
            "Zing id",
            "Image name",
            "Image URL",
            "Stream URL",
        ).inOrder()
        assertThat(rows.single { it.label == "Song of the year" }.value).isEqualTo("None")
        assertThat(rows.single { it.label == "Zing id" }.value).isEqualTo("None")
        assertThat(rows.single { it.label == "Lyric type" }.value).isEqualTo("lrc")
        assertThat(rows.single { it.label == "Id" }.value).isEqualTo("7")
    }
}
