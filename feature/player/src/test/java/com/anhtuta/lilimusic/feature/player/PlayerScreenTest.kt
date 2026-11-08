package com.anhtuta.lilimusic.feature.player

import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import com.anhtuta.lilimusic.core.model.DownloadStatus
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
        var playMode = 0
        var back = 0
        composeRule.setContent {
            LiliTheme {
                SizedPlayer(
                    width = PlayerScreenCompactWidth,
                    height = PlayerScreenPhoneHeight,
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
                            onCyclePlayMode = { playMode++ },
                            onIncreaseSpeed = {},
                            onDecreaseSpeed = {},
                        ),
                )
            }
        }

        composeRule.onNodeWithTag(PlayerTestTags.APP_BAR_TITLE).assertIsDisplayed()
        composeRule.onNodeWithText("Alpha").assertIsDisplayed()
        composeRule.onNodeWithText("Ann").assertIsDisplayed()
        composeRule.onNodeWithTag(PlayerTestTags.ALBUM).assertDoesNotExist()
        composeRule.onNodeWithText("(Album)").assertDoesNotExist()
        composeRule.onNodeWithText("Now playing").assertDoesNotExist()
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
        composeRule.onNodeWithTag(PlayerTestTags.PLAY_MODE).performClick()
        assertThat(playMode).isEqualTo(1)
        composeRule.onNodeWithTag(PlayerTestTags.BACK).performClick()
        assertThat(back).isEqualTo(1)
    }

    @Test
    fun expandedMissingCoverUsesPlaceholderAndShowsEndOfQueue() {
        composeRule.setContent {
            LiliTheme {
                SizedPlayer(
                    width = PlayerScreenExpandedWidth,
                    height = PlayerScreenPhoneHeight,
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
                    width = PlayerScreenCompactWidth,
                    height = PlayerScreenPhoneHeight,
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
                    width = PlayerScreenCompactWidth,
                    height = PlayerScreenPhoneHeight,
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

        composeRule.onNodeWithTag(PlayerTestTags.ALBUM).assertDoesNotExist()
        composeRule.onNodeWithText("(No Album)").assertDoesNotExist()
        composeRule.onNodeWithText("Gamma").assertIsDisplayed()
    }

    @Test
    fun lyricsSlotIsComposedOnNowPlaying() {
        composeRule.setContent {
            LiliTheme {
                SizedPlayer(
                    width = PlayerScreenCompactWidth,
                    height = PlayerScreenPhoneHeight,
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
                    width = PlayerScreenCompactWidth,
                    height = PlayerScreenPhoneHeight,
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
        composeRule.onNodeWithTag(PlayerTestTags.BACKGROUND).assertExists()
        composeRule.onNodeWithTag(PlayerTestTags.SETTINGS).performClick()
        assertThat(settings).isEqualTo(1)
    }

    @Test
    fun downloadActionFiresCallback() {
        var downloads = 0
        composeRule.setContent {
            LiliTheme {
                SizedPlayer(
                    width = PlayerScreenCompactWidth,
                    height = PlayerScreenPhoneHeight,
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 1, title = "Alpha", path = "alpha", imageUrl = null),
                        ),
                    callbacks = idlePlayerCallbacks().copy(onDownload = { downloads++ }),
                )
            }
        }

        composeRule.onNodeWithContentDescription("Download").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Download").performClick()
        assertThat(downloads).isEqualTo(1)
    }

    @Test
    fun downloadingActionShowsCancel() {
        composeRule.setContent {
            LiliTheme {
                SizedPlayer(
                    width = PlayerScreenCompactWidth,
                    height = PlayerScreenPhoneHeight,
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 1, title = "Alpha", path = "alpha", imageUrl = null),
                            downloadStatus = DownloadStatus.DOWNLOADING,
                            downloadProgress = 40,
                        ),
                    callbacks = idlePlayerCallbacks(),
                )
            }
        }

        composeRule.onNodeWithContentDescription("Cancel").assertIsDisplayed()
    }

    @Test
    fun completedActionShowsDelete() {
        composeRule.setContent {
            LiliTheme {
                SizedPlayer(
                    width = PlayerScreenCompactWidth,
                    height = PlayerScreenPhoneHeight,
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 1, title = "Alpha", path = "alpha", imageUrl = null),
                            downloadStatus = DownloadStatus.COMPLETED,
                        ),
                    callbacks = idlePlayerCallbacks(),
                )
            }
        }

        composeRule.onNodeWithContentDescription("Delete").assertIsDisplayed()
    }

    @Test
    fun lyricCardActionFiresCallback() {
        var cards = 0
        composeRule.setContent {
            LiliTheme {
                SizedPlayer(
                    width = PlayerScreenCompactWidth,
                    height = PlayerScreenPhoneHeight,
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
                    width = PlayerScreenCompactWidth,
                    height = PlayerScreenPhoneHeight,
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
        composeRule.onNodeWithTag(PlayerTestTags.SPEED_CHIP).assertIsDisplayed()
        composeRule.onNodeWithTag(PlayerTestTags.SPEED_DOWN).assertDoesNotExist()
        composeRule.onNodeWithTag(PlayerTestTags.SPEED_UP).assertDoesNotExist()
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
                    width = PlayerScreenExpandedWidth,
                    height = PlayerScreenPhoneHeight,
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
                    width = PlayerScreenCompactWidth,
                    height = PlayerScreenPhoneHeight,
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

        composeRule.onNodeWithText("Previous song").assertDoesNotExist()
        composeRule.onNodeWithContentDescription("Previous song").assertIsDisplayed().performClick()
        composeRule.onNodeWithContentDescription("Seek back 5 seconds").assertIsDisplayed().performClick()
        composeRule.onNodeWithContentDescription("Play").assertIsDisplayed().performClick()
        composeRule.onNodeWithContentDescription("Seek forward 5 seconds").assertIsDisplayed().performClick()
        composeRule.onNodeWithContentDescription("Next song").assertIsDisplayed().performClick()
        assertThat(previous).isEqualTo(1)
        assertThat(rewind).isEqualTo(1)
        assertThat(playPause).isEqualTo(1)
        assertThat(forward).isEqualTo(1)
        assertThat(next).isEqualTo(1)
    }

    @Test
    fun longPressPreviousShowsTooltip() {
        composeRule.setContent {
            LiliTheme {
                SizedPlayer(
                    width = PlayerScreenCompactWidth,
                    height = PlayerScreenPhoneHeight,
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 1, title = "Alpha", path = "alpha", imageUrl = null),
                            isPlaying = false,
                        ),
                    callbacks = idlePlayerCallbacks(),
                )
            }
        }

        composeRule.onNodeWithContentDescription("Previous song").performTouchInput { longClick() }
        composeRule.onNodeWithText("Previous song").assertIsDisplayed()
    }

    @Test
    fun playingTransportUsesPauseContentDescription() {
        composeRule.setContent {
            LiliTheme {
                SizedPlayer(
                    width = PlayerScreenCompactWidth,
                    height = PlayerScreenPhoneHeight,
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
