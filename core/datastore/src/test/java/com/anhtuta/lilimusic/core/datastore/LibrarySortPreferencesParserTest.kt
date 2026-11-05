package com.anhtuta.lilimusic.core.datastore

import com.anhtuta.lilimusic.core.model.SongSort
import com.anhtuta.lilimusic.core.model.SortOrder
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LibrarySortPreferencesParserTest {
    @Test
    fun missingValuesDefaultToTitleAscending() {
        assertThat(LibrarySortPreferencesParser.parse(null, null))
            .isEqualTo(LibrarySortPreferences(SongSort.TITLE, SortOrder.ASC))
    }

    @Test
    fun unknownSortFallsBackToTitle() {
        val parsed = LibrarySortPreferencesParser.parse("not_a_column", "DESC")
        assertThat(parsed.sort).isEqualTo(SongSort.TITLE)
        assertThat(parsed.order).isEqualTo(SortOrder.DESC)
    }

    @Test
    fun parsesWhitelistedSort() {
        val parsed = LibrarySortPreferencesParser.parse("listens", "DESC")
        assertThat(parsed.sort).isEqualTo(SongSort.LISTENS)
        assertThat(parsed.order).isEqualTo(SortOrder.DESC)
    }
}
