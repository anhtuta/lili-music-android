package com.anhtuta.lilimusic.feature.player

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
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
import com.anhtuta.lilimusic.core.ui.theme.LiliTheme
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w800dp-h720dp")
class PlayerScreenPolishTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun playModeIconCyclesThroughWebPlayTypeOrder() {
        var cycles = 0
        composeRule.setContent {
            var playMode by remember { mutableStateOf(PlayMode.SEQUENCE) }
            LiliTheme {
                SizedPlayer(
                    width = PlayerScreenCompactWidth,
                    height = PlayerScreenPhoneHeight,
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 1, title = "Alpha", path = "alpha", imageUrl = null),
                            playMode = playMode,
                        ),
                    callbacks =
                        idlePlayerCallbacks().copy(
                            onCyclePlayMode = {
                                cycles++
                                playMode = playMode.cycle()
                            },
                        ),
                )
            }
        }

        composeRule.onNodeWithText("Shuffle").assertDoesNotExist()
        composeRule.onNodeWithText("Repeat one").assertDoesNotExist()
        composeRule.onNodeWithTag(PlayerTestTags.SHUFFLE).assertDoesNotExist()
        composeRule.onNodeWithTag(PlayerTestTags.REPEAT_ONE).assertDoesNotExist()
        composeRule.onNodeWithContentDescription("Play mode: Sequence").assertIsDisplayed().performClick()
        assertThat(cycles).isEqualTo(1)
        composeRule.onNodeWithContentDescription("Play mode: Shuffle").assertIsDisplayed()
        val nextBounds = composeRule.onNodeWithTag(PlayerTestTags.NEXT).getUnclippedBoundsInRoot()
        val modeBounds = composeRule.onNodeWithTag(PlayerTestTags.PLAY_MODE).getUnclippedBoundsInRoot()
        assertThat(modeBounds.left).isGreaterThan(nextBounds.left)
        composeRule.onNodeWithContentDescription("Play mode: Shuffle").performClick()
        composeRule.onNodeWithContentDescription("Play mode: Repeat one").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Play mode: Repeat one").performClick()
        composeRule.onNodeWithContentDescription("Play mode: Sequence").assertIsDisplayed()
    }

    @Test
    fun longPressPlayModeShowsCurrentModeTooltip() {
        composeRule.setContent {
            LiliTheme {
                SizedPlayer(
                    width = PlayerScreenCompactWidth,
                    height = PlayerScreenPhoneHeight,
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 1, title = "Alpha", path = "alpha", imageUrl = null),
                            playMode = PlayMode.SHUFFLE,
                        ),
                    callbacks = idlePlayerCallbacks(),
                )
            }
        }

        composeRule.onNodeWithContentDescription("Play mode: Shuffle").performTouchInput { longClick() }
        composeRule.onNodeWithText("Play mode: Shuffle").assertIsDisplayed()
    }

    @Test
    fun lyricHandleExpandsAndBackRestores() {
        var back = 0
        composeRule.setContent {
            LiliTheme {
                SizedPlayer(
                    width = PlayerScreenCompactWidth,
                    height = PlayerScreenPhoneHeight,
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 1, title = "Alpha", path = "alpha", imageUrl = null),
                        ),
                    callbacks = idlePlayerCallbacks().copy(onBack = { back++ }),
                )
            }
        }

        composeRule.onNodeWithTag(PlayerTestTags.LYRIC_HANDLE).assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Expand lyrics").assertIsDisplayed().performClick()
        composeRule.onNodeWithTag(PlayerTestTags.LYRICS_EXPANDED).assertExists()
        composeRule.onNodeWithTag(PlayerTestTags.CONTROLS).assertDoesNotExist()
        composeRule.onNodeWithContentDescription("Restore lyrics").assertIsDisplayed()
        composeRule.onNodeWithTag(PlayerTestTags.BACK).performClick()
        assertThat(back).isEqualTo(0)
        composeRule.onNodeWithTag(PlayerTestTags.LYRICS_EXPANDED).assertDoesNotExist()
        composeRule.onNodeWithTag(PlayerTestTags.CONTROLS).assertIsDisplayed()
        composeRule.onNodeWithTag(PlayerTestTags.BACK).performClick()
        assertThat(back).isEqualTo(1)
    }

    @Test
    fun lyricHandleDragDownExpands() {
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
                )
            }
        }

        composeRule
            .onNodeWithTag(PlayerTestTags.LYRIC_HANDLE)
            .performTouchInput {
                down(center)
                moveBy(Offset(0f, 120f))
                up()
            }
        composeRule.onNodeWithTag(PlayerTestTags.LYRICS_EXPANDED).assertExists()
    }

    @Test
    fun singleControlRowPutsSpeedPlayModeAndTransportTogether() {
        composeRule.setContent {
            LiliTheme {
                SizedPlayer(
                    width = PlayerScreenCompactWidth,
                    height = PlayerScreenPhoneHeight,
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 1, title = "Alpha", path = "alpha", imageUrl = null),
                            speed = 1.1f,
                        ),
                    callbacks = idlePlayerCallbacks(),
                )
            }
        }

        val speed = composeRule.onNodeWithTag(PlayerTestTags.SPEED_CHIP).getUnclippedBoundsInRoot()
        val previous = composeRule.onNodeWithTag(PlayerTestTags.PREVIOUS).getUnclippedBoundsInRoot()
        val play = composeRule.onNodeWithTag(PlayerTestTags.PLAY_PAUSE).getUnclippedBoundsInRoot()
        val next = composeRule.onNodeWithTag(PlayerTestTags.NEXT).getUnclippedBoundsInRoot()
        val mode = composeRule.onNodeWithTag(PlayerTestTags.PLAY_MODE).getUnclippedBoundsInRoot()
        assertThat(kotlin.math.abs(speed.top.value - play.top.value)).isLessThan(24f)
        assertThat(previous.left).isGreaterThan(speed.left)
        assertThat(play.left).isGreaterThan(previous.left)
        assertThat(next.left).isGreaterThan(play.left)
        assertThat(mode.left).isGreaterThan(next.left)
        assertThat(play.bottom.value - play.top.value).isAtLeast(56f)
    }

    @Test
    fun speedChipOpensStepMenu() {
        var speed = 0f
        composeRule.setContent {
            LiliTheme {
                SizedPlayer(
                    width = PlayerScreenCompactWidth,
                    height = PlayerScreenPhoneHeight,
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 1, title = "Alpha", path = "alpha", imageUrl = null),
                            speed = 1.1f,
                        ),
                    callbacks = idlePlayerCallbacks().copy(onSetSpeed = { speed = it }),
                )
            }
        }

        composeRule.onNodeWithTag(PlayerTestTags.SPEED_CHIP).performClick()
        composeRule.onNodeWithTag(PlayerTestTags.SPEED_MENU).assertExists()
        composeRule.onNodeWithText("0.5×").performClick()
        assertThat(speed).isEqualTo(0.5f)
    }

    @Test
    fun appBarIconsHaveTooltips() {
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
                )
            }
        }

        composeRule.onNodeWithContentDescription("Back").performTouchInput { longClick() }
        composeRule.onNodeWithText("Back").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("More options").performTouchInput { longClick() }
        composeRule.onNodeWithText("More options").assertIsDisplayed()
    }

    @Test
    fun darkThemeTransportIconsStayFindable() {
        composeRule.setContent {
            LiliTheme(darkTheme = true) {
                SizedPlayer(
                    width = PlayerScreenCompactWidth,
                    height = PlayerScreenPhoneHeight,
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 1, title = "Alpha", path = "alpha", imageUrl = null),
                        ),
                    callbacks = idlePlayerCallbacks(),
                )
            }
        }
        composeRule.onNodeWithContentDescription("Play").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Previous song").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Next song").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Play mode: Sequence").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Back").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("More options").assertIsDisplayed()
    }

    @Test
    fun transportGroupsMatchWidthAndPlayIsCentered() {
        composeRule.setContent {
            LiliTheme {
                SizedPlayer(
                    width = PlayerScreenCompactWidth,
                    height = PlayerScreenPhoneHeight,
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 1, title = "Alpha", path = "alpha", imageUrl = null),
                            speed = 1.0f,
                        ),
                    callbacks = idlePlayerCallbacks(),
                )
            }
        }

        val left = composeRule.onNodeWithTag(PlayerTestTags.TRANSPORT_LEFT).getUnclippedBoundsInRoot()
        val right = composeRule.onNodeWithTag(PlayerTestTags.TRANSPORT_RIGHT).getUnclippedBoundsInRoot()
        val play = composeRule.onNodeWithTag(PlayerTestTags.PLAY_PAUSE).getUnclippedBoundsInRoot()
        val controls = composeRule.onNodeWithTag(PlayerTestTags.CONTROLS).getUnclippedBoundsInRoot()
        val leftWidth = left.right.value - left.left.value
        val rightWidth = right.right.value - right.left.value
        assertThat(kotlin.math.abs(leftWidth - rightWidth)).isLessThan(1f)
        val playCenter = (play.left.value + play.right.value) / 2f
        val rowCenter = (controls.left.value + controls.right.value) / 2f
        assertThat(kotlin.math.abs(playCenter - rowCenter)).isLessThan(2f)
        composeRule.onNodeWithContentDescription("Playback speed").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Play mode: Sequence").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Playback speed").performTouchInput { longClick() }
        composeRule.onNodeWithText("Playback speed").assertIsDisplayed()
    }

    @Test
    fun overflowMenuHoldsDownloadLyricCardAndSettings() {
        composeRule.setContent {
            LiliTheme {
                SizedPlayer(
                    width = PlayerScreenCompactWidth,
                    height = PlayerScreenPhoneHeight,
                    state =
                        PlayerUiState(
                            song = playerTestSong(id = 1, title = "Alpha", path = "alpha", imageUrl = null),
                            downloadStatus = DownloadStatus.NONE,
                        ),
                    callbacks = idlePlayerCallbacks(),
                )
            }
        }

        assertThat(NowPlayingOverflowMetrics.SettingsIconSize.value).isEqualTo(16f)
        composeRule.onNodeWithContentDescription("More options").performClick()
        composeRule.onNodeWithTag(PlayerTestTags.OVERFLOW_MENU).assertIsDisplayed()
        composeRule.onNodeWithTag(PlayerTestTags.DOWNLOAD).assertIsDisplayed()
        composeRule.onNodeWithTag(PlayerTestTags.LYRIC_CARD).assertIsDisplayed()
        composeRule.onNodeWithTag(PlayerTestTags.SETTINGS_SCREEN).assertIsDisplayed()
    }
}
