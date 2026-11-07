package com.anhtuta.lilimusic.feature.player

import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.anhtuta.lilimusic.core.model.PlayMode
import com.anhtuta.lilimusic.core.ui.palette.CoverPaletteColors
import com.anhtuta.lilimusic.core.ui.theme.LiliTheme
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PlayerScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsMetadataAndFiresControlCallbacks() {
        var playPause = 0
        var next = 0
        var previous = 0
        var rewind = 0
        var forward = 0
        var shuffle = 0
        var back = 0
        composeRule.setContent {
            LiliTheme {
                PlayerScreen(
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 1, title = "Alpha", path = "alpha", artist = "Ann", imageUrl = null),
                            isPlaying = false,
                            positionMs = 15_000L,
                            durationMs = 60_000L,
                            playMode = PlayMode.SEQUENCE,
                            speed = 1.0f,
                        ),
                    callbacks =
                        PlayerCallbacks(
                            onBack = { back++ },
                            onPlayPause = { playPause++ },
                            onSkipNext = { next++ },
                            onSkipPrevious = { previous++ },
                            onSeek = {},
                            onSeekBack = { rewind++ },
                            onSeekForward = { forward++ },
                            onToggleShuffle = { shuffle++ },
                            onToggleRepeatOne = {},
                            onIncreaseSpeed = {},
                            onDecreaseSpeed = {},
                        ),
                )
            }
        }

        composeRule.onNodeWithTag(PlayerTestTags.TITLE).assertIsDisplayed()
        composeRule.onNodeWithText("Alpha").assertIsDisplayed()
        composeRule.onNodeWithText("Ann").assertIsDisplayed()
        composeRule.onNodeWithTag(PlayerTestTags.ALBUM).assertIsDisplayed()
        composeRule.onNodeWithText("(Album)").assertIsDisplayed()
        composeRule.onNodeWithTag(PlayerTestTags.POSITION).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("0:15").assertIsDisplayed()
        composeRule.onNodeWithText("1:00").assertIsDisplayed()

        composeRule.onNodeWithTag(PlayerTestTags.PLAY_PAUSE).performScrollTo().performClick()
        assertThat(playPause).isEqualTo(1)
        composeRule.onNodeWithTag(PlayerTestTags.NEXT).performScrollTo().performClick()
        assertThat(next).isEqualTo(1)
        composeRule.onNodeWithTag(PlayerTestTags.PREVIOUS).performScrollTo().performClick()
        assertThat(previous).isEqualTo(1)
        composeRule.onNodeWithTag(PlayerTestTags.REWIND).performScrollTo().performClick()
        assertThat(rewind).isEqualTo(1)
        composeRule.onNodeWithTag(PlayerTestTags.FORWARD).performScrollTo().performClick()
        assertThat(forward).isEqualTo(1)
        composeRule.onNodeWithTag(PlayerTestTags.SHUFFLE).performScrollTo().performClick()
        assertThat(shuffle).isEqualTo(1)
        composeRule.onNodeWithTag(PlayerTestTags.BACK).performClick()
        assertThat(back).isEqualTo(1)
    }

    @Test
    fun missingCoverUsesPlaceholderAndShowsEndOfQueue() {
        composeRule.setContent {
            LiliTheme {
                PlayerScreen(
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 2, title = "Beta", path = "beta", imageUrl = null),
                            reachedEnd = true,
                        ),
                    callbacks =
                        PlayerCallbacks(
                            onBack = {},
                            onPlayPause = {},
                            onSkipNext = {},
                            onSkipPrevious = {},
                            onSeek = {},
                            onToggleShuffle = {},
                            onToggleRepeatOne = {},
                            onIncreaseSpeed = {},
                            onDecreaseSpeed = {},
                        ),
                )
            }
        }

        composeRule.onNodeWithTag(PlayerTestTags.COVER_PLACEHOLDER).assertIsDisplayed()
        composeRule.onNodeWithTag(PlayerTestTags.END_OF_QUEUE).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun firstSongOfQueueShowsWebToastEquivalent() {
        composeRule.setContent {
            LiliTheme {
                PlayerScreen(
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 1, title = "Alpha", path = "alpha", imageUrl = null),
                            atStartOfQueue = true,
                        ),
                    callbacks =
                        PlayerCallbacks(
                            onBack = {},
                            onPlayPause = {},
                            onSkipNext = {},
                            onSkipPrevious = {},
                            onSeek = {},
                            onToggleShuffle = {},
                            onToggleRepeatOne = {},
                            onIncreaseSpeed = {},
                            onDecreaseSpeed = {},
                        ),
                )
            }
        }

        composeRule.onNodeWithTag(PlayerTestTags.FIRST_OF_QUEUE).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("This is the first song of playing list!").assertIsDisplayed()
    }

    @Test
    fun missingAlbumShowsNoAlbumFallback() {
        composeRule.setContent {
            LiliTheme {
                PlayerScreen(
                    state =
                        PlayerUiState(
                            song =
                                playerTestSong(
                                    id = 3,
                                    title = "Gamma",
                                    path = "gamma",
                                    album = null,
                                    imageUrl = null,
                                ),
                        ),
                    callbacks =
                        PlayerCallbacks(
                            onBack = {},
                            onPlayPause = {},
                            onSkipNext = {},
                            onSkipPrevious = {},
                            onSeek = {},
                            onToggleShuffle = {},
                            onToggleRepeatOne = {},
                            onIncreaseSpeed = {},
                            onDecreaseSpeed = {},
                        ),
                )
            }
        }

        composeRule.onNodeWithTag(PlayerTestTags.ALBUM).assertIsDisplayed()
        composeRule.onNodeWithText("(No Album)").assertIsDisplayed()
    }

    @Test
    fun lyricsSlotIsComposedOnNowPlaying() {
        composeRule.setContent {
            LiliTheme {
                PlayerScreen(
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 1, title = "Alpha", path = "alpha", imageUrl = null),
                        ),
                    callbacks =
                        PlayerCallbacks(
                            onBack = {},
                            onPlayPause = {},
                            onSkipNext = {},
                            onSkipPrevious = {},
                            onSeek = {},
                            onToggleShuffle = {},
                            onToggleRepeatOne = {},
                            onIncreaseSpeed = {},
                            onDecreaseSpeed = {},
                        ),
                    lyrics = {
                        Text(
                            text = "Lyric slot",
                            modifier = Modifier.testTag("lyric-slot"),
                        )
                    },
                )
            }
        }

        composeRule.onNodeWithTag("lyric-slot").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun paletteOverrideTintsChromeAndSettingsFires() {
        var settings = 0
        composeRule.setContent {
            LiliTheme {
                PlayerScreen(
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 1, title = "Alpha", path = "alpha", imageUrl = null),
                        ),
                    callbacks =
                        PlayerCallbacks(
                            onBack = {},
                            onPlayPause = {},
                            onSkipNext = {},
                            onSkipPrevious = {},
                            onSeek = {},
                            onToggleShuffle = {},
                            onToggleRepeatOne = {},
                            onIncreaseSpeed = {},
                            onDecreaseSpeed = {},
                            onSettings = { settings++ },
                        ),
                    coverPaletteOverride =
                        CoverPaletteColors(
                            background = Color(0xFF112233),
                            onBackground = Color.White,
                        ),
                )
            }
        }

        composeRule.onNodeWithTag(PlayerTestTags.PALETTE).assertExists()
        composeRule.onNodeWithTag(PlayerTestTags.SETTINGS).performClick()
        assertThat(settings).isEqualTo(1)
    }

    @Test
    fun downloadActionFiresCallback() {
        var downloads = 0
        composeRule.setContent {
            LiliTheme {
                PlayerScreen(
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 1, title = "Alpha", path = "alpha", imageUrl = null),
                        ),
                    callbacks =
                        PlayerCallbacks(
                            onBack = {},
                            onPlayPause = {},
                            onSkipNext = {},
                            onSkipPrevious = {},
                            onSeek = {},
                            onToggleShuffle = {},
                            onToggleRepeatOne = {},
                            onIncreaseSpeed = {},
                            onDecreaseSpeed = {},
                            onDownload = { downloads++ },
                        ),
                )
            }
        }

        composeRule.onNodeWithTag(PlayerTestTags.DOWNLOAD).performClick()
        assertThat(downloads).isEqualTo(1)
    }

    @Test
    fun lyricCardActionFiresCallback() {
        var cards = 0
        composeRule.setContent {
            LiliTheme {
                PlayerScreen(
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 1, title = "Alpha", path = "alpha", imageUrl = null),
                        ),
                    callbacks =
                        PlayerCallbacks(
                            onBack = {},
                            onPlayPause = {},
                            onSkipNext = {},
                            onSkipPrevious = {},
                            onSeek = {},
                            onToggleShuffle = {},
                            onToggleRepeatOne = {},
                            onIncreaseSpeed = {},
                            onDecreaseSpeed = {},
                            onLyricCard = { cards++ },
                        ),
                )
            }
        }

        composeRule.onNodeWithTag(PlayerTestTags.LYRIC_CARD).performClick()
        assertThat(cards).isEqualTo(1)
    }
}
