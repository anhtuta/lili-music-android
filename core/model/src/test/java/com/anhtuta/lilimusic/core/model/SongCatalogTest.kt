package com.anhtuta.lilimusic.core.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SongCatalogTest {
    private val alpha = testSong(id = 1, title = "Alpha", artist = "Zed", listens = 2, type = "Vietnam")
    private val beta = testSong(id = 2, title = "Beta", artist = "Ann", listens = 9, type = "Korean")
    private val gamma =
        testSong(
            id = 3,
            title = "Gamma",
            artist = "Ann",
            listens = 5,
            type = null,
            songOfTheYear = 2020,
            createdDate = "2022-01-01T00:00:00.000000Z",
        )

    @Test
    fun searchMatchesTitleOrArtistIgnoringCase() {
        val songs = listOf(alpha, beta, gamma)
        assertThat(SongCatalog.filterAndSort(songs, "ann", SongSort.TITLE, SortOrder.ASC).map { it.id })
            .containsExactly(2L, 3L)
            .inOrder()
        assertThat(SongCatalog.filterAndSort(songs, "ALPHA", SongSort.TITLE, SortOrder.ASC).map { it.title })
            .containsExactly("Alpha")
    }

    @Test
    fun blankQueryReturnsAllSongs() {
        val songs = listOf(alpha, beta)
        assertThat(SongCatalog.filterAndSort(songs, "  ", SongSort.TITLE, SortOrder.ASC)).hasSize(2)
    }

    @Test
    fun sortByListensDescending() {
        val songs = listOf(alpha, beta, gamma)
        assertThat(
            SongCatalog.filterAndSort(songs, "", SongSort.LISTENS, SortOrder.DESC).map { it.listens },
        ).containsExactly(9, 5, 2).inOrder()
    }

    @Test
    fun sortByTitleAscending() {
        val songs = listOf(gamma, alpha, beta)
        assertThat(
            SongCatalog.filterAndSort(songs, "", SongSort.TITLE, SortOrder.ASC).map { it.title },
        ).containsExactly("Alpha", "Beta", "Gamma").inOrder()
    }

    @Test
    fun sortBySongOfTheYearPutsNullsLastWhenAscending() {
        val songs = listOf(alpha, gamma)
        assertThat(
            SongCatalog
                .filterAndSort(songs, "", SongSort.SONG_OF_THE_YEAR, SortOrder.ASC)
                .map { it.songOfTheYear },
        ).containsExactly(2020, null).inOrder()
    }
}
