package com.anhtuta.lilimusic.feature.lyrics

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.anhtuta.lilimusic.core.lyrics.LyricParser
import com.anhtuta.lilimusic.core.lyrics.LyricSynchronizer
import com.anhtuta.lilimusic.core.model.PlayerSettings
import com.anhtuta.lilimusic.core.ui.theme.LiliTheme
import com.google.common.truth.Truth.assertThat
import kotlin.math.abs
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LyricPaneScrollTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun centerDeltaIsItemCenterMinusViewportCenter() {
        assertThat(
            LyricPaneScroll.centerDelta(
                viewportStart = 0,
                viewportEnd = 400,
                itemOffset = 0,
                itemSize = 80,
            ),
        ).isEqualTo(-160)
        assertThat(
            LyricPaneScroll.centerDelta(
                viewportStart = 0,
                viewportEnd = 400,
                itemOffset = 160,
                itemSize = 80,
            ),
        ).isEqualTo(0)
        assertThat(
            LyricPaneScroll.centerDelta(
                viewportStart = 0,
                viewportEnd = 400,
                itemOffset = 300,
                itemSize = 80,
            ),
        ).isEqualTo(140)
    }

    @Test
    fun shouldAutoScrollIsFalseWhileUserIsDragging() {
        assertThat(LyricPaneScroll.shouldAutoScroll(userDragging = true)).isFalse()
        assertThat(LyricPaneScroll.shouldAutoScroll(userDragging = false)).isTrue()
    }

    @Test
    fun activeLineIsVerticallyCenteredInPane() {
        composeRule.setContent {
            LiliTheme {
                Box(
                    modifier =
                        Modifier
                            .width(320.dp)
                            .height(360.dp),
                ) {
                    LyricsPane(
                        state =
                            LyricsUiState(
                                hasSong = true,
                                frame = manyLinesFrame(activeIndex = 8),
                                settings = PlayerSettings(translate = true),
                            ),
                        onNudgeOffset = {},
                        onSeekToLine = {},
                    )
                }
            }
        }
        composeRule.waitForIdle()
        val pane = composeRule.onNodeWithTag(LyricsTestTags.EDGE_FADE).getUnclippedBoundsInRoot()
        val active = composeRule.onNodeWithTag(LyricsTestTags.ACTIVE_LINE).getUnclippedBoundsInRoot()
        val paneCenter = (pane.top.value + pane.bottom.value) / 2f
        val activeCenter = (active.top.value + active.bottom.value) / 2f
        assertThat(abs(activeCenter - paneCenter)).isLessThan(48f)
    }
}

private fun manyLinesFrame(activeIndex: Int) =
    LyricSynchronizer.sync(
        LyricParser.parse(
            buildString {
                repeat(20) { index ->
                    append("[00:")
                    append(index.toString().padStart(2, '0'))
                    append(".00]Line ")
                    append(index)
                    append('\n')
                    if (index == activeIndex) {
                        append("[tran1]Pinyin for line ")
                        append(index)
                        append('\n')
                    }
                }
            },
            "song.xlrc",
        ),
        positionMs = activeIndex * 1_000L + 200L,
        durationMs = 60_000L,
    )
