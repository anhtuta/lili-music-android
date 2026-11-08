package com.anhtuta.lilimusic.feature.lyrics

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.anhtuta.lilimusic.core.lyrics.LyricParser
import com.anhtuta.lilimusic.core.lyrics.LyricSynchronizer
import com.anhtuta.lilimusic.core.ui.theme.LiliTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EqualsLyricLineTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun equalsSeparatorLyricLineIsShownAsPlainText() {
        val frame =
            LyricSynchronizer.sync(
                LyricParser.parse(
                    """
                    [00:00.00]Hello
                    [00:01.00]=====
                    [00:02.00]World
                    """.trimIndent(),
                    "song.lrc",
                ),
                positionMs = 1_200L,
                durationMs = 10_000L,
            )
        composeRule.setContent {
            LiliTheme {
                LyricsPane(
                    state = LyricsUiState(hasSong = true, frame = frame),
                    onNudgeOffset = {},
                    onSeekToLine = {},
                )
            }
        }

        composeRule.onNodeWithText("=====").assertIsDisplayed()
        composeRule.onNodeWithText("Hello").assertIsDisplayed()
        composeRule.onNodeWithText("World").assertIsDisplayed()
    }
}
