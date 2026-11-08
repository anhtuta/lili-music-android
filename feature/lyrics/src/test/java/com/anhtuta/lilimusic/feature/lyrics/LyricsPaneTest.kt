package com.anhtuta.lilimusic.feature.lyrics

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.anhtuta.lilimusic.core.lyrics.LyricParser
import com.anhtuta.lilimusic.core.lyrics.LyricSynchronizer
import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricTheme
import com.anhtuta.lilimusic.core.model.PlayerSettings
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
        composeRule.onNodeWithTag(LyricsTestTags.MISSING_MAIL).assertIsDisplayed().assertHasClickAction()
        composeRule.onNodeWithTag(LyricsTestTags.MISSING_FB).assertIsDisplayed().assertHasClickAction()
        composeRule.onNodeWithText("Mail").assertIsDisplayed()
        composeRule.onNodeWithText("FB").assertIsDisplayed()
        composeRule.onNodeWithTag(LyricCardTestTags.OPEN).assertDoesNotExist()
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

        composeRule.onNodeWithTag(LyricsTestTags.OFFSET_LATER).assertDoesNotExist()
        composeRule.onNodeWithText("Offset: 0ms").assertDoesNotExist()
        composeRule.onNodeWithText("First").performClick()
        assertThat(seekTo).isEqualTo(0L)
        composeRule.onNodeWithTag(LyricsTestTags.OFFSET_LATER).assertIsDisplayed().performClick()
        composeRule.onNodeWithContentDescription("Lyric 100ms later").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Lyric 100ms earlier").assertIsDisplayed()
        assertThat(nudged).isEqualTo(100L)
        composeRule.onNodeWithTag(LyricsTestTags.OFFSET_EARLIER).assertIsDisplayed().performClick()
        assertThat(nudged).isEqualTo(-100L)
        composeRule.onNodeWithText("Offset: 0ms").assertDoesNotExist()
        assertThat(composeRule.onAllNodesWithText("Primero").fetchSemanticsNodes()).isEmpty()
    }

    @Test
    fun translationIsVisibleOnlyWhenEnabled() {
        val frame = sampleFrame()
        composeRule.setContent {
            LiliTheme {
                LyricsPane(
                    state =
                        LyricsUiState(
                            hasSong = true,
                            frame = frame,
                            settings = PlayerSettings(translate = true),
                        ),
                    onNudgeOffset = {},
                    onSeekToLine = {},
                )
            }
        }
        composeRule.onNodeWithText("Primero").assertIsDisplayed()
        composeRule.onNodeWithTag(LyricsTestTags.TRANSLATE).assertExists()
    }

    @Test
    fun visualSettingsExposeTagsAndHideFutureWords() {
        composeRule.setContent {
            LiliTheme {
                LyricsPane(
                    state =
                        LyricsUiState(
                            hasSong = true,
                            frame = sampleFrame(),
                            settings =
                                PlayerSettings(
                                    lyricAlign = LyricAlign.LEFT,
                                    lyricTheme = LyricTheme.DARK,
                                    karaokeMode = true,
                                    blurMode = true,
                                    giganticLine = true,
                                    wordAppear = true,
                                    lineAppear = true,
                                    translate = false,
                                ),
                            coverUrl = "https://example.com/cover.jpg",
                        ),
                    onNudgeOffset = {},
                    onSeekToLine = {},
                )
            }
        }
        composeRule.onNodeWithTag(LyricsTestTags.align(LyricAlign.LEFT)).assertExists()
        composeRule.onNodeWithTag(LyricsTestTags.theme(LyricTheme.DARK)).assertExists()
        composeRule.onNodeWithTag(LyricsTestTags.KARAOKE).assertExists()
        composeRule.onNodeWithTag(LyricsTestTags.BLUR).assertExists()
        composeRule.onNodeWithTag(LyricsTestTags.GIGANTIC).assertExists()
        composeRule.onNodeWithTag(LyricsTestTags.WORD_APPEAR).assertExists()
        composeRule.onNodeWithTag(LyricsTestTags.LINE_APPEAR).assertExists()
        composeRule.onNodeWithText("First").assertExists()
        assertThat(composeRule.onAllNodesWithText("Second").fetchSemanticsNodes()).isEmpty()
    }

    @Test
    fun pictureBgShowsCoverBackdrop() {
        composeRule.setContent {
            LiliTheme {
                LyricsPane(
                    state =
                        LyricsUiState(
                            hasSong = true,
                            frame = sampleFrame(),
                            settings = PlayerSettings(lyricTheme = LyricTheme.PICTURE_BG),
                            coverUrl = "https://example.com/cover.jpg",
                        ),
                    onNudgeOffset = {},
                    onSeekToLine = {},
                )
            }
        }
        composeRule.onNodeWithTag(LyricsTestTags.theme(LyricTheme.PICTURE_BG)).assertExists()
        composeRule.onNodeWithTag(LyricsTestTags.COVER_BACKDROP).assertExists()
    }

    @Test
    fun lyricMetaFooterShowsWebFields() {
        val frame =
            LyricSynchronizer.sync(
                LyricParser.parse(
                    """
                    [ti:Simple Song]
                    [ar:Test Artist]
                    [by:Tuzaku]
                    [date:2021-06-28]
                    [source:manual]
                    [00:00.00]Hello
                    """.trimIndent(),
                    "song.lrc",
                ),
                positionMs = 0L,
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
        composeRule.onNodeWithTag(LyricsTestTags.META_FOOTER).assertIsDisplayed()
        composeRule.onNodeWithText("Simple Song - Test Artist").assertIsDisplayed()
        composeRule.onNodeWithText("Lyric creator: Tuzaku").assertIsDisplayed()
        composeRule.onNodeWithText("Source: manual").assertIsDisplayed()
        composeRule.onNodeWithText("Date: 6/2021").assertIsDisplayed()
    }

    @Test
    fun lyricListUsesEdgeFadeMask() {
        composeRule.setContent {
            LiliTheme {
                LyricsPane(
                    state = LyricsUiState(hasSong = true, frame = sampleFrame()),
                    onNudgeOffset = {},
                    onSeekToLine = {},
                )
            }
        }
        composeRule.onNodeWithTag(LyricsTestTags.EDGE_FADE).assertExists()
        composeRule.onNodeWithText("First").assertIsDisplayed()
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

    @Test
    fun longKaraokeLineWrapsInsteadOfStackingCharacters() {
        val frame =
            LyricSynchronizer.sync(
                LyricParser.parse(
                    """
                    [00:00.00]<100>You're <100>coming <100>back <100>down <100>and <100>you <100>really <100>don't <100>care <100>about <100>the <100>dirty
                    """.trimIndent(),
                    "song.trc",
                ),
                positionMs = 250L,
                durationMs = 20_000L,
            )
        assertThat(
            frame.lines
                .first()
                .words.size,
        ).isAtLeast(10)
        composeRule.setContent {
            LiliTheme {
                Box(
                    modifier =
                        Modifier
                            .width(180.dp)
                            .height(400.dp),
                ) {
                    LyricsPane(
                        state = LyricsUiState(hasSong = true, frame = frame),
                        onNudgeOffset = {},
                        onSeekToLine = {},
                    )
                }
            }
        }

        val bounds =
            composeRule
                .onNodeWithTag(LyricsTestTags.LINE_WORDS, useUnmergedTree = true)
                .getUnclippedBoundsInRoot()
        assertThat(bounds.right.value - bounds.left.value).isAtMost(180f)
        composeRule
            .onNodeWithTag(LyricsTestTags.LINE_WORDS, useUnmergedTree = true)
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("You're coming back down", substring = true)
            .assertIsDisplayed()
        composeRule.onNodeWithText("dirty", substring = true).assertIsDisplayed()
    }
}

private fun sampleFrame() =
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
