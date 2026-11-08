package com.anhtuta.lilimusic.feature.player

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.anhtuta.lilimusic.core.ui.theme.LiliTheme
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w800dp-h720dp")
class NowPlayingAppBarStyleTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun artistColorKeepsOnSurfaceHueWithMutedAlpha() {
        val light = Color(0xFF1C1B1F)
        val dark = Color(0xFFE6E1E5)
        assertThat(NowPlayingAppBarStyle.ARTIST_ALPHA).isAtLeast(0.6f)
        assertThat(NowPlayingAppBarStyle.ARTIST_ALPHA).isAtMost(0.7f)
        val mutedLight = NowPlayingAppBarStyle.artistColor(light)
        val mutedDark = NowPlayingAppBarStyle.artistColor(dark)
        assertThat(mutedLight.alpha).isWithin(0.001f).of(NowPlayingAppBarStyle.ARTIST_ALPHA)
        assertThat(mutedDark.alpha).isWithin(0.001f).of(NowPlayingAppBarStyle.ARTIST_ALPHA)
        assertThat(mutedLight.red).isWithin(0.001f).of(light.red)
        assertThat(mutedLight.green).isWithin(0.001f).of(light.green)
        assertThat(mutedLight.blue).isWithin(0.001f).of(light.blue)
        assertThat(mutedDark.red).isWithin(0.001f).of(dark.red)
        assertThat(mutedDark.green).isWithin(0.001f).of(dark.green)
        assertThat(mutedDark.blue).isWithin(0.001f).of(dark.blue)
    }

    @Test
    fun artistLineIsVisibleInLightAndDark() {
        var dark by mutableStateOf(false)
        composeRule.setContent {
            LiliTheme(darkTheme = dark) {
                SizedPlayer(
                    width = PlayerScreenCompactWidth,
                    height = PlayerScreenPhoneHeight,
                    state =
                        PlayerUiState(
                            song =
                                playerTestSong(
                                    id = 1,
                                    title = "All Falls Down",
                                    path = "alpha",
                                    artist = "Alan Walker, Noah Cyrus",
                                    imageUrl = null,
                                ),
                        ),
                    callbacks = idlePlayerCallbacks(),
                )
            }
        }
        composeRule.onNodeWithTag(PlayerTestTags.APP_BAR_TITLE).assertIsDisplayed()
        composeRule.onNodeWithTag(PlayerTestTags.APP_BAR_ARTIST).assertIsDisplayed()
        dark = true
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(PlayerTestTags.APP_BAR_TITLE).assertIsDisplayed()
        composeRule.onNodeWithTag(PlayerTestTags.APP_BAR_ARTIST).assertIsDisplayed()
    }
}
