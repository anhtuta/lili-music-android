package com.anhtuta.lilimusic.feature.lyrics

import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class KaraokeWrappedFillTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun fillProgressesFirstVisualLineBeforeSecond() {
        val layout = measureWrapped(WRAPPED_LYRIC)
        assertThat(layout.lineCount).isAtLeast(2)
        val range = 0 until layout.layoutInput.text.length
        val firstWidth = layout.getLineRight(0) - layout.getLineLeft(0)
        val secondWidth = layout.getLineRight(1) - layout.getLineLeft(1)
        val totalWidth = firstWidth + secondWidth
        assertThat(totalWidth).isGreaterThan(0f)

        val early = karaokeFillExtents(layout, range, (firstWidth * 0.4f) / totalWidth)
        assertThat(early).isNotEmpty()
        assertThat(early.all { it.lineIndex == 0 }).isTrue()
        assertThat(early.first().filledWidth).isLessThan(firstWidth)

        val lateFraction = ((firstWidth + secondWidth * 0.5f) / totalWidth).coerceAtMost(0.99f)
        val late = karaokeFillExtents(layout, range, lateFraction)
        assertThat(late.map { it.lineIndex }).contains(0)
        assertThat(late.map { it.lineIndex }).contains(1)
        val filledFirst = late.first { it.lineIndex == 0 }
        assertThat(filledFirst.filledWidth).isWithin(1.5f).of(filledFirst.width)
        assertThat(filledFirst.top).isLessThan(late.first { it.lineIndex == 1 }.top)
    }

    @Test
    fun lastVisualLineRangeDoesNotPaintFirstLine() {
        val layout = measureWrapped(WRAPPED_LYRIC)
        assertThat(layout.lineCount).isAtLeast(2)
        val start = layout.getLineStart(1)
        val extents =
            karaokeFillExtents(
                layout,
                start until layout.layoutInput.text.length,
                fill = 0.6f,
            )
        assertThat(extents).isNotEmpty()
        assertThat(extents.none { it.lineIndex == 0 }).isTrue()
        assertThat(extents.minOf { it.top }).isAtLeast(layout.getLineTop(1) - 1f)
    }

    private fun measureWrapped(text: String): TextLayoutResult {
        var layout by mutableStateOf<TextLayoutResult?>(null)
        composeRule.setContent {
            Text(
                text = text,
                fontSize = 28.sp,
                modifier = Modifier.width(220.dp),
                onTextLayout = { layout = it },
            )
        }
        composeRule.waitForIdle()
        return requireNotNull(layout)
    }

    private companion object {
        // Two visual lines (same layout as a wrap). Robolectric CJK/Latin fallback
        // glyphs are near-zero width, so a narrow maxWidth often still reports 1 line.
        const val WRAPPED_LYRIC = "Be brave a little more and I will\ngo"
    }
}
