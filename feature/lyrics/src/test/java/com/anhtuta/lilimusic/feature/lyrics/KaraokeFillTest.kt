package com.anhtuta.lilimusic.feature.lyrics

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import com.anhtuta.lilimusic.core.lyrics.LyricLineFrame
import com.anhtuta.lilimusic.core.lyrics.LyricWordFrame
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class KaraokeFillTest {
    @Test
    fun pausedFillUsesSampledWordFill() {
        val word = testWord(fill = 0.1f)
        assertThat(
            activeWordFill(
                word = word,
                displayPositionMs = 370L,
                offsetMs = 0L,
                isPlaying = false,
            ),
        ).isEqualTo(0.1f)
    }

    @Test
    fun playingFillUsesDisplayPositionFraction() {
        val word = testWord(fill = 0.1f)
        assertThat(
            activeWordFill(
                word = word,
                displayPositionMs = 370L,
                offsetMs = 0L,
                isPlaying = true,
            ),
        ).isWithin(0.001f).of(0.37f)
    }

    @Test
    fun structureKeyIgnoresFillSoAnnotatedStringCanStayStable() {
        val first = lineOf(testWord(fill = 0.2f, isActive = true))
        val second = lineOf(testWord(fill = 0.9f, isActive = true))
        assertThat(lineStructureKey(first)).isEqualTo(lineStructureKey(second))
    }

    @Test
    fun structureKeyChangesWhenActiveWordMoves() {
        val active = lineOf(testWord(fill = 0.2f, isActive = true))
        val passed = lineOf(testWord(fill = 1f, isActive = false, isPassed = true))
        assertThat(lineStructureKey(active)).isNotEqualTo(lineStructureKey(passed))
    }

    @Test
    fun activeKaraokeBrushIsSolidRestNotFillGradient() {
        val word = testWord(fill = 0.4f, isActive = true)
        val brush = wordBrush(word, karaokeMode = true, fillColor = Color.Red, restColor = Color.Gray)
        assertThat(brush).isEqualTo(SolidColor(Color.Gray))
    }

    @Test
    fun passedKaraokeBrushUsesDimmedSungColor() {
        val word = testWord(fill = 1f, isActive = false, isPassed = true)
        val sung = Color.Red.copy(alpha = 0.55f)
        val brush =
            wordBrush(
                word = word,
                karaokeMode = true,
                fillColor = Color.Red,
                restColor = Color.Gray,
                sungColor = sung,
            )
        assertThat(brush).isEqualTo(SolidColor(sung))
    }

    private fun lineOf(word: LyricWordFrame): LyricLineFrame =
        LyricLineFrame(
            words = listOf(word),
            translations = emptyList(),
            isActive = word.isActive,
            isPassed = word.isPassed,
            seekPositionMs = 0L,
        )

    private fun testWord(
        fill: Float,
        isActive: Boolean = true,
        isPassed: Boolean = false,
    ): LyricWordFrame =
        LyricWordFrame(
            text = "Hello",
            isEmpty = false,
            fill = fill,
            countdownDots = 0,
            isActive = isActive,
            isPassed = isPassed,
            seekPositionMs = 0L,
            startMs = 0L,
            endMs = 1_000L,
        )
}
