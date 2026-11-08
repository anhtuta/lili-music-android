package com.anhtuta.lilimusic.feature.lyrics

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LyricLineMetricsTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun wrapMultiplierIsInBreathingRange() {
        assertThat(LyricLineMetrics.WRAP_LINE_HEIGHT_MULTIPLIER).isAtLeast(1.35f)
        assertThat(LyricLineMetrics.WRAP_LINE_HEIGHT_MULTIPLIER).isAtMost(1.4f)
    }

    @Test
    fun lineHeightStyleKeepsCenteredUntrimmedLeading() {
        assertThat(LyricLineMetrics.lineHeightStyle.alignment).isEqualTo(LineHeightStyle.Alignment.Center)
        assertThat(LyricLineMetrics.lineHeightStyle.trim).isEqualTo(LineHeightStyle.Trim.None)
    }

    @Test
    fun wrappedVisualLinesTallerThanTightGlyphRows() {
        val fontSize = 20.sp
        var layout by mutableStateOf<TextLayoutResult?>(null)
        composeRule.setContent {
            Text(
                text = "First visual line of a lyric\nSecond visual line",
                style = LyricLineMetrics.textStyle(fontSize).copy(fontSize = fontSize),
                onTextLayout = { layout = it },
            )
        }
        composeRule.waitForIdle()
        val measured = requireNotNull(layout)
        assertThat(measured.lineCount).isAtLeast(2)
        val fontPx = with(measured.layoutInput.density) { fontSize.toPx() }
        val firstLineHeight = measured.getLineBottom(0) - measured.getLineTop(0)
        val totalHeight = measured.getLineBottom(1) - measured.getLineTop(0)
        assertThat(firstLineHeight).isAtLeast(fontPx * 1.35f)
        assertThat(totalHeight).isAtLeast(fontPx * 1.35f * 2f)

        val extents =
            karaokeFillExtents(
                measured,
                0 until measured.layoutInput.text.length,
                fill = 1f,
            )
        assertThat(extents).isNotEmpty()
        assertThat(extents.first().top).isWithin(0.5f).of(measured.getLineTop(0))
        assertThat(extents.first().bottom).isWithin(0.5f).of(measured.getLineBottom(0))
        val lastCharBox = measured.getBoundingBox(measured.layoutInput.text.length - 1)
        assertThat(extents.last().top).isAtMost(lastCharBox.top)
        assertThat(extents.last().bottom).isAtLeast(lastCharBox.bottom)
    }
}
