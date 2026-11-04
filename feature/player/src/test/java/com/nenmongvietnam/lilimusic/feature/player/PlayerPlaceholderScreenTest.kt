package com.nenmongvietnam.lilimusic.feature.player

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PlayerPlaceholderScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsSongIdAndBackNavigates() {
        var backClicked = false
        composeRule.setContent {
            PlayerPlaceholderScreen(
                songId = 42L,
                onBack = { backClicked = true },
            )
        }

        composeRule.onNodeWithTag(PlayerTestTags.SCREEN).assertIsDisplayed()
        composeRule.onNodeWithText("Playback for song 42 is coming in a later phase.").assertIsDisplayed()
        composeRule.onNodeWithTag(PlayerTestTags.BACK).performClick()
        assertThat(backClicked).isTrue()
    }
}
