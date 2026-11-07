package com.anhtuta.lilimusic.core.lyrics

import com.anhtuta.lilimusic.core.model.LyricFormat
import com.anhtuta.lilimusic.core.model.ParsedLyric
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LyricCardTextTest {
    @Test
    fun emptyLyricCannotCreateCard() {
        assertThat(LyricCardText.canCreate(null)).isFalse()
        assertThat(LyricCardText.canCreate(ParsedLyric.empty(LyricFormat.LRC))).isFalse()
    }

    @Test
    fun joinsLrcLinesAndOmitsEmptySlots() {
        val parsed =
            LyricParser.parse(
                """
                [ti:Simple Song]
                [ar:Test Artist]
                [00:00.00]Intro title
                [00:02.00]
                [00:07.50]First sung line
                """.trimIndent(),
                "simple.lrc",
            )
        assertThat(LyricCardText.canCreate(parsed)).isTrue()
        assertThat(LyricCardText.selectableLines(parsed, includeTranslations = false))
            .containsExactly(
                "Intro title",
                "First sung line",
                "-----------",
                "Simple Song - Test Artist",
            ).inOrder()
    }

    @Test
    fun translationsOnlyWhenRequested() {
        val parsed =
            LyricParser.parse(
                """
                [00:00.00]First
                [tran1]Primero
                [00:01.00]Second
                """.trimIndent(),
                "song.xlrc",
            )
        assertThat(LyricCardText.selectableLines(parsed, includeTranslations = false))
            .containsExactly("First", "Second")
            .inOrder()
        assertThat(LyricCardText.selectableLines(parsed, includeTranslations = true))
            .containsExactly("First", "Primero", "Second")
            .inOrder()
    }

    @Test
    fun trcJoinsWordsAndAddsMetaLabels() {
        val parsed =
            LyricParser.parse(
                """
                [ar:Alan Walker]
                [ti:All Falls Down]
                [by:Liliana]
                [source:ttpod]
                [date:2019-12-06]
                [00:00.463]<249>What’s <132>the <528>trick?
                [00:38.687]<680>
                """.trimIndent(),
                "song.trc",
            )
        val lines = LyricCardText.selectableLines(parsed, includeTranslations = false)
        assertThat(lines.first()).isEqualTo("What’s the trick?")
        assertThat(lines).doesNotContain("")
        assertThat(lines).contains("Lyric creator: Liliana")
        assertThat(lines).contains("Source: ttpod")
        assertThat(lines).contains("Date: 12/2019")
        assertThat(LyricCardText.plain(parsed, includeTranslations = false)).contains("What’s the trick?")
    }
}
