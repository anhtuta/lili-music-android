package com.anhtuta.lilimusic.feature.player

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anhtuta.lilimusic.core.model.PlayMode
import com.anhtuta.lilimusic.core.ui.palette.CoverPaletteColors
import com.anhtuta.lilimusic.core.ui.theme.LiliTheme
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w800dp-h720dp")
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
                SizedPlayer(
                    width = COMPACT_WIDTH,
                    height = PHONE_HEIGHT,
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
        composeRule.onNodeWithTag(PlayerTestTags.POSITION).assertIsDisplayed()
        composeRule.onNodeWithText("0:15").assertIsDisplayed()
        composeRule.onNodeWithText("1:00").assertIsDisplayed()

        composeRule.onNodeWithTag(PlayerTestTags.PLAY_PAUSE).performClick()
        assertThat(playPause).isEqualTo(1)
        composeRule.onNodeWithTag(PlayerTestTags.NEXT).performClick()
        assertThat(next).isEqualTo(1)
        composeRule.onNodeWithTag(PlayerTestTags.PREVIOUS).performClick()
        assertThat(previous).isEqualTo(1)
        composeRule.onNodeWithTag(PlayerTestTags.REWIND).performClick()
        assertThat(rewind).isEqualTo(1)
        composeRule.onNodeWithTag(PlayerTestTags.FORWARD).performClick()
        assertThat(forward).isEqualTo(1)
        composeRule.onNodeWithTag(PlayerTestTags.SHUFFLE).performClick()
        assertThat(shuffle).isEqualTo(1)
        composeRule.onNodeWithTag(PlayerTestTags.BACK).performClick()
        assertThat(back).isEqualTo(1)
    }

    @Test
    fun expandedMissingCoverUsesPlaceholderAndShowsEndOfQueue() {
        composeRule.setContent {
            LiliTheme {
                SizedPlayer(
                    width = EXPANDED_WIDTH,
                    height = PHONE_HEIGHT,
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 2, title = "Beta", path = "beta", imageUrl = null),
                            reachedEnd = true,
                        ),
                    callbacks = idlePlayerCallbacks(),
                )
            }
        }

        composeRule.onNodeWithTag(PlayerTestTags.COVER_PLACEHOLDER).assertIsDisplayed()
        composeRule.onNodeWithTag(PlayerTestTags.END_OF_QUEUE).assertIsDisplayed()
    }

    @Test
    fun firstSongOfQueueShowsWebToastEquivalent() {
        composeRule.setContent {
            LiliTheme {
                SizedPlayer(
                    width = COMPACT_WIDTH,
                    height = PHONE_HEIGHT,
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 1, title = "Alpha", path = "alpha", imageUrl = null),
                            atStartOfQueue = true,
                        ),
                    callbacks = idlePlayerCallbacks(),
                )
            }
        }

        composeRule.onNodeWithTag(PlayerTestTags.FIRST_OF_QUEUE).assertIsDisplayed()
        composeRule.onNodeWithText("This is the first song of playing list!").assertIsDisplayed()
    }

    @Test
    fun missingAlbumShowsNoAlbumFallback() {
        composeRule.setContent {
            LiliTheme {
                SizedPlayer(
                    width = COMPACT_WIDTH,
                    height = PHONE_HEIGHT,
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
                    callbacks = idlePlayerCallbacks(),
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
                SizedPlayer(
                    width = COMPACT_WIDTH,
                    height = PHONE_HEIGHT,
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 1, title = "Alpha", path = "alpha", imageUrl = null),
                        ),
                    callbacks = idlePlayerCallbacks(),
                    lyrics = {
                        Text(
                            text = "Lyric slot",
                            modifier = Modifier.testTag("lyric-slot"),
                        )
                    },
                )
            }
        }

        composeRule.onNodeWithTag("lyric-slot").assertIsDisplayed()
    }

    @Test
    fun paletteOverrideTintsChromeAndSettingsFires() {
        var settings = 0
        composeRule.setContent {
            LiliTheme {
                SizedPlayer(
                    width = COMPACT_WIDTH,
                    height = PHONE_HEIGHT,
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 1, title = "Alpha", path = "alpha", imageUrl = null),
                        ),
                    callbacks =
                        idlePlayerCallbacks().copy(onSettings = { settings++ }),
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
                SizedPlayer(
                    width = COMPACT_WIDTH,
                    height = PHONE_HEIGHT,
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 1, title = "Alpha", path = "alpha", imageUrl = null),
                        ),
                    callbacks = idlePlayerCallbacks().copy(onDownload = { downloads++ }),
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
                SizedPlayer(
                    width = COMPACT_WIDTH,
                    height = PHONE_HEIGHT,
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 1, title = "Alpha", path = "alpha", imageUrl = null),
                        ),
                    callbacks = idlePlayerCallbacks().copy(onLyricCard = { cards++ }),
                )
            }
        }

        composeRule.onNodeWithTag(PlayerTestTags.LYRIC_CARD).performClick()
        assertThat(cards).isEqualTo(1)
    }

    @Test
    fun compactHidesCoverAndFillsBodyWithLyrics() {
        composeRule.setContent {
            LiliTheme {
                SizedPlayer(
                    width = COMPACT_WIDTH,
                    height = PHONE_HEIGHT,
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 1, title = "Alpha", path = "alpha", imageUrl = null),
                        ),
                    callbacks = idlePlayerCallbacks(),
                    lyrics = {
                        Text(
                            text = "Lyric slot",
                            modifier = Modifier.testTag("lyric-slot"),
                        )
                    },
                )
            }
        }

        composeRule.onNodeWithTag(PlayerTestTags.COVER).assertDoesNotExist()
        composeRule.onNodeWithTag(PlayerTestTags.COVER_PLACEHOLDER).assertDoesNotExist()
        composeRule.onNodeWithTag("lyric-slot").assertIsDisplayed()
        composeRule.onNodeWithTag(PlayerTestTags.CONTROLS).assertIsDisplayed()
        composeRule.onNodeWithTag(PlayerTestTags.SEEK).assertIsDisplayed()
        composeRule.onNodeWithTag(PlayerTestTags.PLAY_PAUSE).assertIsDisplayed()
        composeRule.onNodeWithTag(PlayerTestTags.SPEED_VALUE).assertIsDisplayed()
        val bounds =
            composeRule
                .onNodeWithTag(PlayerTestTags.LYRICS)
                .getUnclippedBoundsInRoot()
        assertThat(bounds.bottom.value - bounds.top.value).isGreaterThan(200f)
    }

    @Test
    fun expandedShowsCoverBesideLyrics() {
        composeRule.setContent {
            LiliTheme {
                SizedPlayer(
                    width = EXPANDED_WIDTH,
                    height = PHONE_HEIGHT,
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 1, title = "Alpha", path = "alpha", imageUrl = null),
                        ),
                    callbacks = idlePlayerCallbacks(),
                )
            }
        }

        composeRule.onNodeWithTag(PlayerTestTags.COVER_PLACEHOLDER).assertIsDisplayed()
        composeRule.onNodeWithTag(PlayerTestTags.LYRICS).assertIsDisplayed()
        composeRule.onNodeWithTag(PlayerTestTags.CONTROLS).assertIsDisplayed()
        val bounds =
            composeRule
                .onNodeWithTag(PlayerTestTags.LYRICS)
                .getUnclippedBoundsInRoot()
        assertThat(bounds.bottom.value - bounds.top.value).isGreaterThan(200f)
    }

    @Test
    fun transportIconsExposeWebMatchingContentDescriptions() {
        var previous = 0
        var rewind = 0
        var playPause = 0
        var forward = 0
        var next = 0
        composeRule.setContent {
            LiliTheme {
                SizedPlayer(
                    width = COMPACT_WIDTH,
                    height = PHONE_HEIGHT,
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 1, title = "Alpha", path = "alpha", imageUrl = null),
                            isPlaying = false,
                        ),
                    callbacks =
                        idlePlayerCallbacks().copy(
                            onSkipPrevious = { previous++ },
                            onSeekBack = { rewind++ },
                            onPlayPause = { playPause++ },
                            onSeekForward = { forward++ },
                            onSkipNext = { next++ },
                        ),
                )
            }
        }

        composeRule.onNodeWithText("Previous").assertDoesNotExist()
        composeRule.onNodeWithContentDescription("Previous").assertIsDisplayed().performClick()
        composeRule.onNodeWithContentDescription("Seek back 5 seconds").assertIsDisplayed().performClick()
        composeRule.onNodeWithContentDescription("Play").assertIsDisplayed().performClick()
        composeRule.onNodeWithContentDescription("Seek forward 5 seconds").assertIsDisplayed().performClick()
        composeRule.onNodeWithContentDescription("Next").assertIsDisplayed().performClick()
        assertThat(previous).isEqualTo(1)
        assertThat(rewind).isEqualTo(1)
        assertThat(playPause).isEqualTo(1)
        assertThat(forward).isEqualTo(1)
        assertThat(next).isEqualTo(1)
    }

    @Test
    fun playingTransportUsesPauseContentDescription() {
        composeRule.setContent {
            LiliTheme {
                SizedPlayer(
                    width = COMPACT_WIDTH,
                    height = PHONE_HEIGHT,
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 1, title = "Alpha", path = "alpha", imageUrl = null),
                            isPlaying = true,
                        ),
                    callbacks = idlePlayerCallbacks(),
                )
            }
        }

        composeRule.onNodeWithContentDescription("Pause").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Play").assertDoesNotExist()
    }
}

private val COMPACT_WIDTH = 400.dp
private val EXPANDED_WIDTH = 800.dp
private val PHONE_HEIGHT = 720.dp

@Composable
private fun SizedPlayer(
    width: Dp,
    height: Dp,
    state: PlayerUiState,
    callbacks: PlayerCallbacks,
    lyrics: @Composable () -> Unit = {},
    coverPaletteOverride: CoverPaletteColors? = null,
) {
    Box(modifier = Modifier.size(width = width, height = height)) {
        PlayerScreen(
            modifier = Modifier.fillMaxSize(),
            state = state,
            callbacks = callbacks,
            lyrics = lyrics,
            coverPaletteOverride = coverPaletteOverride,
        )
    }
}

private fun idlePlayerCallbacks() =
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
    )
