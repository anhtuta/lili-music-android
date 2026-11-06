package com.anhtuta.lilimusic.core.lyrics

import com.anhtuta.lilimusic.core.model.LyricFormat
import com.anhtuta.lilimusic.core.model.LyricLine
import com.anhtuta.lilimusic.core.model.LyricMeta
import com.anhtuta.lilimusic.core.model.LyricWord
import com.anhtuta.lilimusic.core.model.ParsedLyric
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LyricSynchronizerTest {
    @Test
    fun highlightsCurrentWordAndFillsKaraoke() {
        val lyric = sampleTrc()
        val mid = LyricSynchronizer.sync(lyric, positionMs = 200L, durationMs = 10_000L)

        assertThat(mid.activeWordIndex).isEqualTo(0)
        assertThat(mid.lines[0].isActive).isTrue()
        assertThat(mid.lines[0].words[0].fill).isWithin(0.001f).of(0.5f)
        assertThat(mid.lines[0].words[1].fill).isEqualTo(0f)

        val passed = LyricSynchronizer.sync(lyric, positionMs = 450L, durationMs = 10_000L)
        assertThat(passed.activeWordIndex).isEqualTo(1)
        assertThat(passed.lines[0].words[0].isPassed).isTrue()
        assertThat(passed.lines[0].words[0].fill).isEqualTo(1f)
        assertThat(passed.lines[0].words[1].fill).isWithin(0.001f).of(0.5f)
    }

    @Test
    fun overlappingTimesPreferTheLaterWord() {
        val words =
            listOf(
                LyricSynchronizer.TimedWord(startMs = 0L, endMs = 400L),
                LyricSynchronizer.TimedWord(startMs = 300L, endMs = 600L),
            )
        assertThat(LyricSynchronizer.findCurrentWordIndex(words, positionMs = 350L, offsetMs = 0L))
            .isEqualTo(1)
        assertThat(LyricSynchronizer.findCurrentWordIndex(words, positionMs = 100L, offsetMs = 0L))
            .isEqualTo(0)
    }

    @Test
    fun beforeFirstAndAfterLastHaveNoCurrentWord() {
        val words =
            listOf(
                LyricSynchronizer.TimedWord(startMs = 1_000L, endMs = 2_000L),
                LyricSynchronizer.TimedWord(startMs = 2_000L, endMs = 3_000L),
            )
        assertThat(LyricSynchronizer.findCurrentWordIndex(words, positionMs = 0L, offsetMs = 0L)).isEqualTo(-1)
        assertThat(LyricSynchronizer.findCurrentWordIndex(words, positionMs = 4_000L, offsetMs = 0L)).isEqualTo(-1)
    }

    @Test
    fun lastLrcLineUsesDuration() {
        val lyric =
            ParsedLyric(
                format = LyricFormat.LRC,
                lines =
                    listOf(
                        LyricLine(
                            startMs = 1_000L,
                            endMs = null,
                            words = listOf(LyricWord(1_000L, null, "Last", isEmpty = false)),
                        ),
                    ),
            )
        val frame = LyricSynchronizer.sync(lyric, positionMs = 1_500L, durationMs = 5_000L)
        assertThat(frame.activeLineIndex).isEqualTo(0)
        assertThat(frame.lines[0].words[0].fill).isWithin(0.001f).of(0.125f)
    }

    @Test
    fun combinedOffsetShiftsActiveWord() {
        val lyric =
            sampleTrc().copy(
                meta = LyricMeta(offsetMs = -200L),
            )
        val withoutUser = LyricSynchronizer.sync(lyric, positionMs = 0L, durationMs = 10_000L, userOffsetMs = 0L)
        assertThat(withoutUser.activeWordIndex).isEqualTo(0)
        assertThat(withoutUser.combinedOffsetMs).isEqualTo(-200L)

        val delayed =
            LyricSynchronizer.sync(
                sampleTrc(),
                positionMs = 0L,
                durationMs = 10_000L,
                userOffsetMs = 200L,
            )
        assertThat(delayed.activeWordIndex).isEqualTo(-1)
        assertThat(delayed.combinedOffsetMs).isEqualTo(200L)
    }

    @Test
    fun countdownUsesFiveSecondWindowThenThree() {
        assertThat(
            LyricSynchronizer.countdownDots(startMs = 0L, endMs = 8_000L, positionMs = 3_000L, offsetMs = 0L),
        ).isEqualTo(5)
        assertThat(
            LyricSynchronizer.countdownDots(startMs = 0L, endMs = 8_000L, positionMs = 4_000L, offsetMs = 0L),
        ).isEqualTo(4)
        assertThat(
            LyricSynchronizer.countdownDots(startMs = 0L, endMs = 8_000L, positionMs = 2_900L, offsetMs = 0L),
        ).isEqualTo(0)
        assertThat(
            LyricSynchronizer.countdownDots(startMs = 0L, endMs = 4_000L, positionMs = 1_500L, offsetMs = 0L),
        ).isEqualTo(3)
        assertThat(
            LyricSynchronizer.countdownDots(startMs = 0L, endMs = 4_000L, positionMs = 500L, offsetMs = 0L),
        ).isEqualTo(0)
        assertThat(
            LyricSynchronizer.countdownDots(startMs = 0L, endMs = 2_000L, positionMs = 1_500L, offsetMs = 0L),
        ).isEqualTo(0)
    }

    @Test
    fun countdownSkippedWhenNoFollowingWord() {
        val lyric =
            ParsedLyric(
                format = LyricFormat.TRC,
                lines =
                    listOf(
                        LyricLine(
                            startMs = 0L,
                            endMs = 8_000L,
                            words = listOf(LyricWord(startMs = 0L, endMs = 8_000L, text = "", isEmpty = true)),
                        ),
                    ),
            )
        val frame = LyricSynchronizer.sync(lyric, positionMs = 4_000L, durationMs = 8_000L)
        assertThat(frame.lines[0].words[0].countdownDots).isEqualTo(0)
    }

    @Test
    fun countdownShowsWhenFollowingWordExists() {
        val lyric =
            ParsedLyric(
                format = LyricFormat.TRC,
                lines =
                    listOf(
                        LyricLine(
                            startMs = 0L,
                            endMs = 9_000L,
                            words =
                                listOf(
                                    LyricWord(startMs = 0L, endMs = 8_000L, text = "", isEmpty = true),
                                    LyricWord(startMs = 8_000L, endMs = 9_000L, text = "next", isEmpty = false),
                                ),
                        ),
                    ),
            )
        val frame = LyricSynchronizer.sync(lyric, positionMs = 4_000L, durationMs = 9_000L)
        assertThat(frame.lines[0].words[0].countdownDots).isEqualTo(4)
    }

    @Test
    fun emptyLyricYieldsEmptyFrame() {
        val frame =
            LyricSynchronizer.sync(
                ParsedLyric.empty(LyricFormat.LRC),
                positionMs = 1_000L,
                durationMs = 10_000L,
            )
        assertThat(frame.lines).isEmpty()
        assertThat(frame.activeLineIndex).isEqualTo(-1)
        assertThat(frame.activeWordIndex).isEqualTo(-1)
    }

    @Test
    fun seekPositionAppliesCombinedOffset() {
        val lyric = sampleTrc().copy(meta = LyricMeta(offsetMs = -300L))
        val frame = LyricSynchronizer.sync(lyric, positionMs = 0L, durationMs = 10_000L, userOffsetMs = 100L)
        assertThat(frame.lines[0].seekPositionMs).isEqualTo(0L)
        assertThat(frame.lines[1].seekPositionMs).isEqualTo(300L)
    }

    private fun sampleTrc(): ParsedLyric =
        ParsedLyric(
            format = LyricFormat.TRC,
            lines =
                listOf(
                    LyricLine(
                        startMs = 0L,
                        endMs = 400L,
                        words =
                            listOf(
                                LyricWord(startMs = 0L, endMs = 400L, text = "Hello ", isEmpty = false),
                                LyricWord(startMs = 400L, endMs = 500L, text = "world", isEmpty = false),
                            ),
                    ),
                    LyricLine(
                        startMs = 500L,
                        endMs = 1_000L,
                        words =
                            listOf(
                                LyricWord(startMs = 500L, endMs = 1_000L, text = "next", isEmpty = false),
                            ),
                    ),
                ),
        )
}
