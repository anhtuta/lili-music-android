package com.anhtuta.lilimusic.core.lyrics

import com.anhtuta.lilimusic.core.model.LyricFormat
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LyricParserTest {
    @Test
    fun parsesLrcMetaAndTimedLines() {
        val parsed = LyricParser.parse(fixture("simple.lrc"), "simple.lrc")

        assertThat(parsed.format).isEqualTo(LyricFormat.LRC)
        assertThat(parsed.meta.offsetMs).isEqualTo(-200L)
        assertThat(parsed.meta.title).isEqualTo("Simple Song")
        assertThat(parsed.meta.artist).isEqualTo("Test Artist")
        assertThat(parsed.meta.by).isEqualTo("Tuzaku")
        assertThat(parsed.meta.date).isEqualTo("2021-06-28")
        assertThat(parsed.meta.source).isEqualTo("manual")
        assertThat(parsed.lines).hasSize(5)
        assertThat(parsed.lines[0].words.single().text).isEqualTo("Intro title")
        assertThat(parsed.lines[1].isEmpty).isTrue()
        assertThat(parsed.lines[1].startMs).isEqualTo(2_000L)
        assertThat(parsed.lines[1].endMs).isEqualTo(7_500L)
        assertThat(parsed.lines[2].words.single().text).isEqualTo("First sung line")
        assertThat(parsed.lines.last().startMs).isEqualTo(60_500L)
        assertThat(parsed.lines.last().endMs).isNull()
    }

    @Test
    fun duplicateTimestampsExpandAndSort() {
        val parsed = LyricParser.parse(fixture("duplicates.lrc"), "chorus.lrc")

        assertThat(parsed.lines.map { it.startMs }).containsExactly(28_130L, 32_000L, 113_410L).inOrder()
        assertThat(parsed.lines[0].words.single().text).isEqualTo("Người theo hương hoa")
        assertThat(parsed.lines[2].words.single().text).isEqualTo("Người theo hương hoa")
        assertThat(parsed.lines[1].words.single().text).isEqualTo("Only once")
        assertThat(parsed.meta.by).isEqualTo("Old Author")
    }

    @Test
    fun deprecatedMetaTagsFillByAndDate() {
        val parsed = LyricParser.parse(fixture("deprecated-meta.lrc"), "old.lrc")

        assertThat(parsed.meta.by).isEqualTo("Old Author")
        assertThat(parsed.meta.date).isEqualTo("2020-01-01")
    }

    @Test
    fun hourStampAndUnknownMetaAreHandled() {
        val hours = LyricParser.parse(fixture("hours.lrc"), "book.lrc")
        assertThat(hours.lines.map { it.startMs }).containsExactly(1_500L, 3_825_320L).inOrder()

        val unknown = LyricParser.parse(fixture("unknown-meta.lrc"), "song.lrc")
        assertThat(unknown.meta).isEqualTo(com.anhtuta.lilimusic.core.model.LyricMeta())
        assertThat(unknown.lines.single().words.single().text).isEqualTo("轻轻贴近你的耳朵")
    }

    @Test
    fun stripsBomAndParsesPositiveOffset() {
        val parsed = LyricParser.parse(fixture("bom-crlf.lrc"), "bom.lrc")

        assertThat(parsed.meta.offsetMs).isEqualTo(150L)
        assertThat(parsed.lines.map { it.words.single().text }).containsExactly("Hello CRLF", "Second").inOrder()
    }

    @Test
    fun xlrcBindsTranslationsToPrecedingLine() {
        val parsed = LyricParser.parse(fixture("translations.xlrc"), "song.xlrc")

        assertThat(parsed.format).isEqualTo(LyricFormat.XLRC)
        assertThat(parsed.lines[0].translations.map { it.tag }).containsExactly("tran1", "tran2")
        assertThat(parsed.lines[0].translations[0].text).isEqualTo("qīng qīng tiē jìn nǐ de ěr duǒ")
        assertThat(parsed.lines[1].translations.single().text).isEqualTo("shā lǎng hēi yō")

        val shared = parsed.lines.filter { it.words.single().text == "Shared chorus" }
        assertThat(shared).hasSize(2)
        val byStart = shared.associate { it.startMs to it.translations }
        assertThat(byStart[28_130L]?.single()?.text).isEqualTo("only on last stamp")
        assertThat(byStart[113_410L]).isEmpty()
    }

    @Test
    fun trcParsesWordDurationsEmptySlotsAndSkipsUntimedPromo() {
        val parsed = LyricParser.parse(fixture("words.trc"), "song.trc")

        assertThat(parsed.format).isEqualTo(LyricFormat.TRC)
        assertThat(parsed.meta.offsetMs).isEqualTo(-300L)
        assertThat(parsed.meta.artist).isEqualTo("Alan Walker")
        assertThat(parsed.meta.title).isEqualTo("All Falls Down")
        assertThat(parsed.meta.by).isEqualTo("Liliana")
        assertThat(parsed.meta.date).isEqualTo("2019-12-06")
        assertThat(parsed.meta.source).isEqualTo("ttpod")

        val first = parsed.lines.first().words
        assertThat(first.map { it.text }).containsExactly("What’s ", "the ", "trick?").inOrder()
        assertThat(first[0].startMs).isEqualTo(463L)
        assertThat(first[0].endMs).isEqualTo(712L)
        assertThat(first[1].startMs).isEqualTo(712L)
        assertThat(first[1].endMs).isEqualTo(844L)
        assertThat(first[2].endMs).isEqualTo(1_372L)

        val emptyLine = parsed.lines.single { it.startMs == 38_687L }
        assertThat(emptyLine.words.single().isEmpty).isTrue()
        assertThat(emptyLine.words.single().endMs).isEqualTo(39_367L)

        assertThat(parsed.lines.none { line -> line.words.any { it.text.contains("好音质") } }).isTrue()
    }

    @Test
    fun missingOrBlankBodyYieldsEmptyLyric() {
        assertThat(LyricParser.parse(fixture("missing.txt"), "gone.lrc").isEmpty).isTrue()
        assertThat(LyricParser.parse("  ", "gone.trc").isEmpty).isTrue()
        assertThat(LyricParser.parse("", "gone.xlrc").format).isEqualTo(LyricFormat.XLRC)
    }

    private fun fixture(name: String): String {
        val stream =
            checkNotNull(javaClass.classLoader?.getResourceAsStream("lyrics/$name")) {
                "Missing fixture lyrics/$name"
            }
        return stream.bufferedReader().use { it.readText() }
    }
}
