package com.anhtuta.lilimusic.feature.library

import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.anhtuta.lilimusic.core.model.SongSort
import com.anhtuta.lilimusic.core.model.SortOrder
import com.anhtuta.lilimusic.core.ui.theme.LiliTheme
import com.google.common.truth.Truth.assertThat
import kotlin.math.abs
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h600dp")
class LibraryRowMoreAlignTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun songMoreAlignsWithSettingsAndSearchRightEdge() {
        val song = libraryTestSong(id = 1, title = "Alpha", path = "alpha")
        composeRule.setContent {
            LiliTheme {
                LibraryScreen(
                    modifier = Modifier.height(600.dp),
                    state =
                        LibraryUiState(
                            songs = listOf(song),
                            isLoading = false,
                            sort = SongSort.TITLE,
                            sortOrder = SortOrder.ASC,
                        ),
                    onQueryChange = {},
                    onSortFieldChosen = {},
                    onRefresh = {},
                    onSongClick = {},
                    onDownloadAction = { },
                )
            }
        }

        val searchRight =
            composeRule
                .onNodeWithTag(LibraryTestTags.SEARCH)
                .getUnclippedBoundsInRoot()
                .right.value
        val settingsRight =
            composeRule
                .onNodeWithTag(LibraryTestTags.SETTINGS)
                .getUnclippedBoundsInRoot()
                .right.value
        val moreRight =
            composeRule
                .onNodeWithTag(LibraryTestTags.more(song.id))
                .getUnclippedBoundsInRoot()
                .right.value

        assertThat(abs(moreRight - settingsRight)).isAtMost(2f)
        assertThat(abs(moreRight - searchRight)).isAtMost(2f)
    }
}
