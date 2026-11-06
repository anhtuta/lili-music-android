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
        assertThat(SongCatalog.filter(songs, "ann").map { it.id })
            .containsExactly(2L, 3L)
            .inOrder()
        assertThat(SongCatalog.filter(songs, "ALPHA").map { it.title })
            .containsExactly("Alpha")
    }

    @Test
    fun blankQueryKeepsIncomingOrder() {
        val songs = listOf(gamma, alpha, beta)
        assertThat(
            SongCatalog.filter(songs, "  ").map { it.title },
        ).containsExactly("Gamma", "Alpha", "Beta").inOrder()
    }

    @Test
    fun searchPreservesIncomingOrder() {
        val songs = listOf(gamma, alpha, beta)
        assertThat(
            SongCatalog.filter(songs, "ann").map { it.title },
        ).containsExactly("Gamma", "Beta").inOrder()
    }

    @Test
    fun localSortByListensDescending() {
        val songs = listOf(alpha, beta, gamma)
        assertThat(
            SongCatalog.sorted(songs, SongSort.LISTENS, SortOrder.DESC).map { it.listens },
        ).containsExactly(9, 5, 2).inOrder()
    }

    @Test
    fun localSortByTitleAscending() {
        val songs = listOf(gamma, alpha, beta)
        assertThat(
            SongCatalog.sorted(songs, SongSort.TITLE, SortOrder.ASC).map { it.title },
        ).containsExactly("Alpha", "Beta", "Gamma").inOrder()
    }

    @Test
    fun localSortNullsMatchMysql() {
        val songs = listOf(alpha, gamma)
        assertThat(
            SongCatalog.sorted(songs, SongSort.SONG_OF_THE_YEAR, SortOrder.ASC).map { it.songOfTheYear },
        ).containsExactly(null, 2020).inOrder()
        assertThat(
            SongCatalog.sorted(songs, SongSort.SONG_OF_THE_YEAR, SortOrder.DESC).map { it.songOfTheYear },
        ).containsExactly(2020, null).inOrder()
        assertThat(
            SongCatalog.sorted(songs, SongSort.TYPE, SortOrder.ASC).map { it.type },
        ).containsExactly(null, "Vietnam").inOrder()
        assertThat(
            SongCatalog.sorted(songs, SongSort.TYPE, SortOrder.DESC).map { it.type },
        ).containsExactly("Vietnam", null).inOrder()
    }
}
