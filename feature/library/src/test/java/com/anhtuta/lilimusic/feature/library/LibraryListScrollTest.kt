package com.anhtuta.lilimusic.feature.library

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LibraryListScrollTest {
    @Test
    fun listIndexAccountsForChromeItems() {
        val songs =
            listOf(
                libraryTestSong(id = 1, path = "a"),
                libraryTestSong(id = 2, path = "b"),
            )

        assertThat(
            LibraryListScroll.listIndexForSong(songs, "b", showOfflineBanner = false),
        ).isEqualTo(4)

        assertThat(
            LibraryListScroll.listIndexForSong(songs, "b", showOfflineBanner = true),
        ).isEqualTo(5)
    }

    @Test
    fun missingSongReturnsNull() {
        val songs = listOf(libraryTestSong(id = 1, path = "a"))
        assertThat(
            LibraryListScroll.listIndexForSong(songs, "missing", showOfflineBanner = false),
        ).isNull()
    }
}
