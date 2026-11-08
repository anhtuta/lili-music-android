package com.anhtuta.lilimusic.feature.library

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.anhtuta.lilimusic.core.model.SongSort
import com.anhtuta.lilimusic.core.model.SortOrder
import com.anhtuta.lilimusic.core.ui.theme.LiliTheme
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LibrarySortPillLayoutTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun sortPillStartsAlignedWithSearchField() {
        composeRule.setContent {
            LiliTheme {
                Column {
                    LibrarySearchField(query = "", onQueryChange = {})
                    LibrarySortPill(
                        selected = SongSort.DATE,
                        sortOrder = SortOrder.DESC,
                        onSortFieldChosen = {},
                    )
                }
            }
        }

        val searchLeft = composeRule.onNodeWithTag(LibraryTestTags.SEARCH).getUnclippedBoundsInRoot().left
        val pillLeft = composeRule.onNodeWithTag(LibraryTestTags.SORT_PILL).getUnclippedBoundsInRoot().left
        assertThat(pillLeft.value).isWithin(2f).of(searchLeft.value)
    }
}
