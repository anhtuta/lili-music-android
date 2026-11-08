package com.anhtuta.lilimusic.feature.player

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.anhtuta.lilimusic.core.ui.theme.LiliTheme
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MiniPlayerBarTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun tapOpensPlayerAndPlayPauseDelegates() {
        var clicked = false
        var playPause = 0
        composeRule.setContent {
            LiliTheme {
                MiniPlayerBar(
                    song = playerTestSong(id = 1, title = "Alpha", path = "alpha", imageUrl = null),
                    isPlaying = true,
                    onPlayPause = { playPause++ },
                    onClick = { clicked = true },
                )
            }
        }

        composeRule.onNodeWithText("Alpha").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Pause").performClick()
        composeRule.onNodeWithTag(PlayerTestTags.MINI_BAR).performClick()

        assertThat(playPause).isEqualTo(1)
        assertThat(clicked).isTrue()
    }
}
