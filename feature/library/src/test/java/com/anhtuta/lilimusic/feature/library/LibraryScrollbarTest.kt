package com.anhtuta.lilimusic.feature.library

import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsAtLeast
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
class LibraryScrollbarTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun scrollbarShownForLongCatalog() {
        val songs = (1..40).map { libraryTestSong(id = it.toLong(), title = "Song $it", path = "s-$it") }
        composeRule.setContent {
            LiliTheme {
                LibraryScreen(
                    modifier = Modifier.height(500.dp),
                    state =
                        LibraryUiState(
                            songs = songs,
                            isLoading = false,
                            sort = SongSort.TITLE,
                            sortOrder = SortOrder.ASC,
                        ),
                    onQueryChange = {},
                    onSortFieldChosen = {},
                    onRefresh = {},
                    onSongClick = {},
                )
            }
        }

        composeRule.onNodeWithTag(LibraryTestTags.SCROLLBAR).assertIsDisplayed()
        composeRule
            .onNodeWithTag(LibraryTestTags.SCROLLBAR_THUMB)
            .assertWidthIsAtLeast(20.dp)
    }
}
