package com.anhtuta.lilimusic.feature.library

import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.anhtuta.lilimusic.core.model.SongSort
import com.anhtuta.lilimusic.core.model.SortOrder
import com.anhtuta.lilimusic.core.ui.theme.LiliTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h600dp")
class LibraryScrollToNowPlayingTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun listScrollsToNowPlayingSong() {
        val songs = (1..30).map { libraryTestSong(id = it.toLong(), title = "Song $it", path = "s-$it") }
        val current = songs.last()
        composeRule.setContent {
            LiliTheme {
                LibraryScreen(
                    modifier = Modifier.height(400.dp),
                    state =
                        LibraryUiState(
                            songs = songs,
                            isLoading = false,
                            sort = SongSort.TITLE,
                            sortOrder = SortOrder.ASC,
                            nowPlayingPath = current.path,
                            isPlaying = true,
                        ),
                    onQueryChange = {},
                    onSortFieldChosen = {},
                    onRefresh = {},
                    onSongClick = {},
                )
            }
        }

        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag(LibraryTestTags.song(current.id)).assertExists()
        composeRule.onNodeWithTag(LibraryTestTags.SEARCH).assertIsNotDisplayed()
    }
}
