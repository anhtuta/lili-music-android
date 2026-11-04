package com.nenmongvietnam.lilimusic.core.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SongSortTest {
    @Test
    fun whitelistMatchesLiveApiColumns() {
        assertThat(SongSort.entries.map { it.apiValue }).containsExactly(
            "title",
            "listens",
            "song_of_the_year",
            "type",
            "createdDate",
        )
    }

    @Test
    fun defaultOrdersMatchWebTable() {
        assertThat(SongSort.TITLE.defaultOrder).isEqualTo(SortOrder.ASC)
        assertThat(SongSort.LISTENS.defaultOrder).isEqualTo(SortOrder.DESC)
        assertThat(SongSort.SONG_OF_THE_YEAR.defaultOrder).isEqualTo(SortOrder.DESC)
        assertThat(SongSort.TYPE.defaultOrder).isEqualTo(SortOrder.ASC)
        assertThat(SongSort.CREATED_DATE.defaultOrder).isEqualTo(SortOrder.DESC)
    }

    @Test
    fun unknownSortByIsNotParsed() {
        assertThat(SongSort.fromApiValue("not_a_column")).isNull()
        assertThat(SongSort.fromApiValue("zing_id")).isNull()
    }

    @Test
    fun createdDateAliasIsAccepted() {
        assertThat(SongSort.fromApiValue("createdDate")).isEqualTo(SongSort.CREATED_DATE)
        assertThat(SongSort.fromApiValue("created_date")).isEqualTo(SongSort.CREATED_DATE)
    }

    @Test
    fun sortOrderToggleFlipsDirection() {
        assertThat(SortOrder.ASC.toggle()).isEqualTo(SortOrder.DESC)
        assertThat(SortOrder.DESC.toggle()).isEqualTo(SortOrder.ASC)
    }
}
