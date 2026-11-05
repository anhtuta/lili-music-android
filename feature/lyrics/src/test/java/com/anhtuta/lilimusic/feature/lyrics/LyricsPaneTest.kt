package com.anhtuta.lilimusic.feature.lyrics

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.anhtuta.lilimusic.core.lyrics.LyricParser
import com.anhtuta.lilimusic.core.lyrics.LyricSynchronizer
import com.anhtuta.lilimusic.core.ui.theme.LiliTheme
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LyricsPaneTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun missingLyricShowsContributionCopy() {
        composeRule.setContent {
            LiliTheme {
                LyricsPane(
                    state = LyricsUiState(hasSong = true, isMissing = true),
                    onNudgeOffset = {},
                    onSeekToLine = {},
                )
            }
        }

        composeRule.onNodeWithTag(LyricsTestTags.MISSING).assertIsDisplayed()
        composeRule.onNodeWithText("taanhtu95@gmail.com", substring = true).assertIsDisplayed()
    }

    @Test
    fun lineTapAndOffsetButtonsFireCallbacks() {
        var seekTo = -1L
        var nudged = 0L
        val frame =
            LyricSynchronizer.sync(
                LyricParser.parse(
                    """
                    [00:00.00]First
                    [tran1]Primero
                    [00:01.00]Second
                    """.trimIndent(),
                    "song.xlrc",
                ),
                positionMs = 200L,
                durationMs = 10_000L,
            )
        assertThat(
            frame.lines[0]
                .translations
                .single()
                .text,
        ).isEqualTo("Primero")
        composeRule.setContent {
            LiliTheme {
                LyricsPane(
                    state =
                        LyricsUiState(
                            hasSong = true,
                            frame = frame,
                            userOffsetMs = 0L,
                        ),
                    onNudgeOffset = { nudged = it },
                    onSeekToLine = { seekTo = it },
                )
            }
        }

        composeRule.onNodeWithText("First").performClick()
        assertThat(seekTo).isEqualTo(0L)
        composeRule.onNodeWithTag(LyricsTestTags.OFFSET_LATER).performClick()
        assertThat(nudged).isEqualTo(100L)
        composeRule.onNodeWithTag(LyricsTestTags.OFFSET_EARLIER).performClick()
        assertThat(nudged).isEqualTo(-100L)
        composeRule.onNodeWithText("Primero").assertExists()
    }

    @Test
    fun fetchingStateShowsWaitCopy() {
        composeRule.setContent {
            LiliTheme {
                LyricsPane(
                    state = LyricsUiState(hasSong = true, isLoading = true),
                    onNudgeOffset = {},
                    onSeekToLine = {},
                )
            }
        }

        composeRule.onNodeWithTag(LyricsTestTags.FETCHING).assertIsDisplayed()
    }
}
