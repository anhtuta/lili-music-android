package com.anhtuta.lilimusic.feature.library

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import com.anhtuta.lilimusic.core.ui.theme.LiliTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h600dp")
class LibraryNowPlayingBadgeTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun playingBadgeOnlyWhenRowMarkedNowPlaying() {
        val playing = libraryTestSong(id = 1, title = "Playing", path = "p")
        composeRule.setContent {
            LiliTheme {
                LibrarySongRow(
                    song = playing,
                    soyYears = emptyList(),
                    onClick = {},
                    onCoverClick = {},
                    isNowPlaying = true,
                    isPlaying = true,
                    onDownloadAction = {},
                )
            }
        }

        composeRule.onNodeWithContentDescription("Now playing: Playing").assertExists()
    }

    @Test
    fun noBadgeWhenRowIsNotNowPlaying() {
        val other = libraryTestSong(id = 2, title = "Other", path = "o")
        composeRule.setContent {
            LiliTheme {
                LibrarySongRow(
                    song = other,
                    soyYears = emptyList(),
                    onClick = {},
                    onCoverClick = {},
                    isNowPlaying = false,
                    isPlaying = false,
                    onDownloadAction = {},
                )
            }
        }

        composeRule.onNodeWithTag(LibraryTestTags.playbackBadge(other.id)).assertDoesNotExist()
    }
}
